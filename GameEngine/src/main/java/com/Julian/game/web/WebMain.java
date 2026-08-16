package com.Julian.game.web;

import org.teavm.jso.browser.AnimationFrameCallback;
import org.teavm.jso.browser.Window;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.KeyboardEvent;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;

/**
 * Minimal TeaVM wasmGC proof-of-concept.
 *
 * This is intentionally NOT part of the real game engine port. It exists only to
 * prove that the TeaVM -> WebAssembly (GC backend) toolchain works end to end:
 * compiles, loads in a browser runtime, can grab a &lt;canvas&gt; via JSO, render
 * to it, and read keyboard input, all driven by requestAnimationFrame.
 *
 * Do not add real game logic here. See com.Julian.game.* for the desktop engine
 * that will eventually be ported.
 */
public final class WebMain {

    private static final int SQUARE_SIZE = 20;
    private static final int SPEED = 3;

    private static boolean upHeld;
    private static boolean downHeld;
    private static boolean leftHeld;
    private static boolean rightHeld;

    private static double x;
    private static double y;

    private static CanvasRenderingContext2D context;
    private static int canvasWidth;
    private static int canvasHeight;

    private static AnimationFrameCallback frameCallback;

    private WebMain() {
    }

    public static void main(String[] args) {
        HTMLDocument document = HTMLDocument.current();

        HTMLCanvasElement canvas = (HTMLCanvasElement) document.getElementById("game-canvas");
        context = (CanvasRenderingContext2D) canvas.getContext("2d");

        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();

        x = (canvasWidth - SQUARE_SIZE) / 2.0;
        y = (canvasHeight - SQUARE_SIZE) / 2.0;

        document.addEventListener("keydown", (EventListener<KeyboardEvent>) WebMain::onKeyDown);
        document.addEventListener("keyup", (EventListener<KeyboardEvent>) WebMain::onKeyUp);

        frameCallback = WebMain::update;
        Window.requestAnimationFrame(frameCallback);
    }

    private static void onKeyDown(KeyboardEvent event) {
        if (setKeyState(event.getKey(), true)) {
            event.preventDefault();
        }
    }

    private static void onKeyUp(KeyboardEvent event) {
        if (setKeyState(event.getKey(), false)) {
            event.preventDefault();
        }
    }

    private static boolean setKeyState(String key, boolean held) {
        switch (key) {
            case "ArrowUp":
                upHeld = held;
                return true;
            case "ArrowDown":
                downHeld = held;
                return true;
            case "ArrowLeft":
                leftHeld = held;
                return true;
            case "ArrowRight":
                rightHeld = held;
                return true;
            default:
                return false;
        }
    }

    private static void update(double timestamp) {
        if (upHeld) {
            y -= SPEED;
        }
        if (downHeld) {
            y += SPEED;
        }
        if (leftHeld) {
            x -= SPEED;
        }
        if (rightHeld) {
            x += SPEED;
        }

        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (x > canvasWidth - SQUARE_SIZE) {
            x = canvasWidth - SQUARE_SIZE;
        }
        if (y > canvasHeight - SQUARE_SIZE) {
            y = canvasHeight - SQUARE_SIZE;
        }

        context.clearRect(0, 0, canvasWidth, canvasHeight);
        context.setFillStyle("#39d353");
        context.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);

        Window.requestAnimationFrame(frameCallback);
    }
}
