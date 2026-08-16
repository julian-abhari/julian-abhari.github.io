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
  }
}

const CANVAS_WIDTH = 640;
const CANVAS_HEIGHT = 480;

type Status = "loading" | "ready" | "error";

export function GameCanvas() {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [status, setStatus] = useState<Status>("loading");
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function boot() {
      try {
        await loadScript("/game/game.wasm-runtime.js");
        if (cancelled) return;
        if (!window.TeaVM) {
          throw new Error("TeaVM runtime script loaded but window.TeaVM is missing.");
        }
        const teavm = await window.TeaVM.wasmGC.load("/game/game.wasm");
        if (cancelled) return;
        teavm.exports.main([]);
        setStatus("ready");
      } catch (err) {
        console.error("Failed to boot the game engine:", err);
        if (!cancelled) {
          setError(err instanceof Error ? err.message : String(err));
          setStatus("error");
        }
      }
    }

    boot();
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
        className="block bg-black"
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
