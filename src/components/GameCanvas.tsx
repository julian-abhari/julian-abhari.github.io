"use client";

import { useEffect, useRef, useState } from "react";

declare global {
  interface Window {
    TeaVM?: {
      wasmGC: {
        load: (
          url: string,
          options?: Record<string, unknown>
        ) => Promise<{
          // Entry points that need JS<->Java argument marshalling (e.g.
          // main(String[])) show up here as wrapper functions. Plain
          // @Export-annotated static methods with only primitive
          // parameters (e.g. resize(int, int)) don't need marshalling, so
          // TeaVM emits them as raw wasm function exports instead — those
          // are only reachable via `instance.exports`, not `exports`.
          exports: Record<string, (...args: unknown[]) => unknown>;
          instance: { exports: Record<string, (...args: unknown[]) => unknown> };
        }>;
      };
    };
    __portfolioGame?: {
      setNearInteractive: (isNear: boolean) => void;
    };
  }
}

// CSS pixels per internal game pixel. Keeps tiles/sprites at a consistent,
// comfortable on-screen size regardless of window size (each 8x8 tile renders
// at 32x32 CSS px, matching the original engine's fixed 640x480 @ 160x120 setup)
// instead of stretching a fixed-resolution image to fill the screen.
const TILE_SCALE = 4;

// Internal (camera/viewport) resolution clamps, in game pixels. The camera
// reveals more of the level on larger windows and less on smaller ones, but
// these keep it from ever going unplayably small or, on an ultra-wide
// monitor, revealing an entire hand-built level at a glance. (480x360 caps
// out at a 1920x1440 CSS-px display size, comfortably above most desktop
// browser windows, so the cap mostly only bites on genuinely huge/ultra-wide
// setups rather than everyday full-screen windows.)
const MIN_GAME_WIDTH = 80;
const MIN_GAME_HEIGHT = 60;
const MAX_GAME_WIDTH = 480;
const MAX_GAME_HEIGHT = 360;

// Debounces resize handling so a window drag-resize doesn't reallocate the
// engine's frame buffers on every intermediate frame.
const RESIZE_DEBOUNCE_MS = 150;

type Status = "loading" | "ready" | "error";
type GameSize = { width: number; height: number };
type GameExports = { resize: (width: number, height: number) => void };

function clamp(value: number, min: number, max: number): number {
  return Math.min(max, Math.max(min, value));
}

function computeGameSize(cssWidth: number, cssHeight: number): GameSize {
  return {
    width: clamp(Math.round(cssWidth / TILE_SCALE), MIN_GAME_WIDTH, MAX_GAME_WIDTH),
    height: clamp(Math.round(cssHeight / TILE_SCALE), MIN_GAME_HEIGHT, MAX_GAME_HEIGHT),
  };
}

// Sizes the canvas to an exact integer multiple of TILE_SCALE — e.g. at
// gameSize 240x180 that's exactly 960x720 CSS px — rather than stretching it
// to fill the container's raw (usually non-multiple-of-4) CSS size. That
// guarantees drawImage's upscale from the internal game buffer is always a
// clean integer ratio; any leftover sliver between the canvas and the
// container's true size is letterboxed (shown as the page's background)
// instead of being filled with an unevenly stretched, slightly-blurry image.
// Resizing a canvas's width/height attributes also clears its 2D context
// back to defaults, so imageSmoothingEnabled has to be re-applied each time.
function applyCanvasSize(canvas: HTMLCanvasElement, gameSize: GameSize) {
  const width = gameSize.width * TILE_SCALE;
  const height = gameSize.height * TILE_SCALE;
  if (canvas.width === width && canvas.height === height) {
    return;
  }
  canvas.style.width = `${width}px`;
  canvas.style.height = `${height}px`;
  canvas.width = width;
  canvas.height = height;
  const ctx = canvas.getContext("2d");
  if (ctx) {
    ctx.imageSmoothingEnabled = false;
  }
}

export function GameCanvas() {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const gameExportsRef = useRef<GameExports | null>(null);
  const lastGameSizeRef = useRef<GameSize | null>(null);
  const [status, setStatus] = useState<Status>("loading");
  const [error, setError] = useState<string | null>(null);
  const [nearInteractive, setNearInteractive] = useState(false);
  const [isTouchDevice] = useState(
    () =>
      typeof window !== "undefined" &&
      ("ontouchstart" in window || navigator.maxTouchPoints > 0)
  );

  useEffect(() => {
    const container = containerRef.current;
    const canvas = canvasRef.current;
    if (!container || !canvas) return;

    let cancelled = false;

    const rect = container.getBoundingClientRect();
    const initialGameSize = computeGameSize(rect.width, rect.height);
    applyCanvasSize(canvas, initialGameSize);
    lastGameSizeRef.current = initialGameSize;

    bootGameEngine(setNearInteractive, initialGameSize)
      .then((exports) => {
        if (cancelled) return;
        gameExportsRef.current = exports;

        // The container may have resized while the wasm module was
        // downloading; make sure the engine reflects the current size
        // rather than whatever it was when the boot started.
        const currentRect = container.getBoundingClientRect();
        const currentGameSize = computeGameSize(currentRect.width, currentRect.height);
        if (
          currentGameSize.width !== lastGameSizeRef.current?.width ||
          currentGameSize.height !== lastGameSizeRef.current?.height
        ) {
          applyCanvasSize(canvas, currentGameSize);
          lastGameSizeRef.current = currentGameSize;
          exports.resize(currentGameSize.width, currentGameSize.height);
        }

        setStatus("ready");
      })
      .catch((err) => {
        console.error("Failed to boot the game engine:", err);
        if (!cancelled) {
          setError(err instanceof Error ? err.message : String(err));
          setStatus("error");
        }
      });

    let resizeTimeout: ReturnType<typeof setTimeout> | null = null;
    const observer = new ResizeObserver((entries) => {
      const entry = entries[0];
      if (!entry) return;
      const { width: cssWidth, height: cssHeight } = entry.contentRect;

      if (resizeTimeout) clearTimeout(resizeTimeout);
      resizeTimeout = setTimeout(() => {
        const gameSize = computeGameSize(cssWidth, cssHeight);
        const last = lastGameSizeRef.current;
        if (last && last.width === gameSize.width && last.height === gameSize.height) {
          return;
        }
        applyCanvasSize(canvas, gameSize);
        lastGameSizeRef.current = gameSize;
        gameExportsRef.current?.resize(gameSize.width, gameSize.height);
      }, RESIZE_DEBOUNCE_MS);
    });
    observer.observe(container);

    return () => {
      cancelled = true;
      observer.disconnect();
      if (resizeTimeout) clearTimeout(resizeTimeout);
    };
  }, []);

  return (
    <div ref={containerRef} className="relative flex h-full w-full items-center justify-center">
      <canvas id="game-canvas" ref={canvasRef} className="block bg-black touch-none" />
      {status === "loading" && (
        <div className="absolute inset-0 flex items-center justify-center text-sm text-white/70">
          Loading engine…
        </div>
      )}
      {status === "error" && (
        <div className="absolute inset-0 flex flex-col items-center justify-center gap-1 bg-black/80 p-4 text-center text-sm text-red-400">
          <span>Failed to load the game engine.</span>
          <span className="text-xs text-red-400/70">{error}</span>
        </div>
      )}
      {status === "ready" && nearInteractive && (
        <div
          data-testid="interact-hud"
          className="pointer-events-none absolute bottom-4 left-1/2 -translate-x-1/2 rounded-full bg-black/70 px-4 py-1.5 text-xs font-medium tracking-wide text-white"
        >
          {isTouchDevice ? "TAP TO INTERACT" : "PRESS D TO INTERACT"}
        </div>
      )}
    </div>
  );
}

const scriptLoadPromises = new Map<string, Promise<void>>();

function loadScript(src: string): Promise<void> {
  let promise = scriptLoadPromises.get(src);
  if (!promise) {
    promise = new Promise((resolve, reject) => {
      const script = document.createElement("script");
      script.src = src;
      script.onload = () => resolve();
      script.onerror = () => reject(new Error(`Failed to load script: ${src}`));
      document.body.appendChild(script);
    });
    scriptLoadPromises.set(src, promise);
  }
  return promise;
}

// The TeaVM wasm module is a singleton by nature (it attaches its own
// document-level keydown/keyup/touch listeners and drives its own
// requestAnimationFrame loop) — booting it twice, which React StrictMode's
// dev-mode double-effect-invoke would otherwise cause, would attach two
// independent copies of everything. Cache the boot behind a module-level
// promise so it only ever actually runs once; the initial size is only ever
// taken from whichever call started that first boot.
let bootPromise: Promise<GameExports> | null = null;

function bootGameEngine(
  setNearInteractive: (isNear: boolean) => void,
  initialGameSize: GameSize
): Promise<GameExports> {
  if (!bootPromise) {
    bootPromise = (async () => {
      await loadScript("/game/game.wasm-runtime.js");
      if (!window.TeaVM) {
        throw new Error("TeaVM runtime script loaded but window.TeaVM is missing.");
      }
      window.__portfolioGame = { setNearInteractive };
      const teavm = await window.TeaVM.wasmGC.load("/game/game.wasm");
      teavm.exports.main([String(initialGameSize.width), String(initialGameSize.height)]);
      return teavm.instance.exports as unknown as GameExports;
    })();
  }
  return bootPromise;
}
