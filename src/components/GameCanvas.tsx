"use client";

import { useEffect, useRef, useState } from "react";

declare global {
  interface Window {
    TeaVM?: {
      wasmGC: {
        load: (
          url: string,
          options?: Record<string, unknown>
        ) => Promise<{ exports: Record<string, (...args: unknown[]) => unknown> }>;
      };
    };
    __portfolioGame?: {
      setNearInteractive: (isNear: boolean) => void;
    };
  }
}

const CANVAS_WIDTH = 640;
const CANVAS_HEIGHT = 480;

type Status = "loading" | "ready" | "error";

export function GameCanvas() {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [status, setStatus] = useState<Status>("loading");
  const [error, setError] = useState<string | null>(null);
  const [nearInteractive, setNearInteractive] = useState(false);
  const [isTouchDevice] = useState(
    () =>
      typeof window !== "undefined" &&
      ("ontouchstart" in window || navigator.maxTouchPoints > 0)
  );

  useEffect(() => {
    let cancelled = false;

    bootGameEngine(setNearInteractive)
      .then(() => {
        if (!cancelled) setStatus("ready");
      })
      .catch((err) => {
        console.error("Failed to boot the game engine:", err);
        if (!cancelled) {
          setError(err instanceof Error ? err.message : String(err));
          setStatus("error");
        }
      });

    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <div
      className="relative mx-auto"
      style={{ width: CANVAS_WIDTH, height: CANVAS_HEIGHT }}
    >
      <canvas
        id="game-canvas"
        ref={canvasRef}
        width={CANVAS_WIDTH}
        height={CANVAS_HEIGHT}
        className="block bg-black touch-none"
      />
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
// promise so it only ever actually runs once.
let bootPromise: Promise<void> | null = null;

function bootGameEngine(setNearInteractive: (isNear: boolean) => void): Promise<void> {
  if (!bootPromise) {
    bootPromise = (async () => {
      await loadScript("/game/game.wasm-runtime.js");
      if (!window.TeaVM) {
        throw new Error("TeaVM runtime script loaded but window.TeaVM is missing.");
      }
      window.__portfolioGame = { setNearInteractive };
      const teavm = await window.TeaVM.wasmGC.load("/game/game.wasm");
      teavm.exports.main([]);
    })();
  }
  return bootPromise;
}
