package com.Julian.game.web;

import org.teavm.interop.Export;
import org.teavm.jso.JSProperty;
import org.teavm.jso.browser.AnimationFrameCallback;
import org.teavm.jso.browser.Window;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.canvas.ImageData;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.typedarrays.Uint8ClampedArray;

import com.Julian.game.Game;

/**
 * Real web bootstrap/entry point for the ported game engine.
 *
 * Responsibilities:
 * - Preload the game's PNG assets (via WebAssets) before anything else touches them
 *   (SpriteSheet/Level read them synchronously from WebAssets' cache).
 * - Grab the visible on-page &lt;canvas id="game-canvas"&gt; (wired up by
 *   src/components/GameCanvas.tsx on the Next.js side) and create a second,
 *   offscreen low-res canvas used as a cheap 1:1 pixel blit target for Game's
 *   frame buffer. The low-res resolution is chosen by the React side (based on
 *   window size) and passed in via main()'s args, then updated at runtime
 *   through the exported resize() method.
 * - Drive Game.tick()/Game.render() from requestAnimationFrame, then upscale the
 *   low-res buffer onto the visible canvas each frame (nearest-neighbor, to match
 *   the crisp pixelated look the desktop AWT version got for free).
 */
public final class WebMain {

	// Fallback used only if the React side ever calls main() without args.
	private static final int DEFAULT_WIDTH = 160;
	private static final int DEFAULT_HEIGHT = 120;

	/**
	 * TeaVM's JSO CanvasRenderingContext2D interface (0.15.0) doesn't expose
	 * imageSmoothingEnabled, so it's added here as a small overlay-type extension
	 * rather than reaching for a raw @JSBody snippet.
	 */
	private interface SmoothingContext2D extends CanvasRenderingContext2D {
		@JSProperty
		void setImageSmoothingEnabled(boolean value);
	}

	private static final String[] ASSET_PATHS = {
			"/game/SpriteSheet.png",
			"/game/Levels/water_test.png",
			"/game/Levels/cage_test_2.png",
			"/game/Levels/cage_test_level.png",
	};

	private static CanvasRenderingContext2D visibleContext;
	private static HTMLCanvasElement visibleCanvas;
	private static CanvasRenderingContext2D offscreenContext;
	private static HTMLCanvasElement offscreenCanvas;
	private static ImageData frameImageData;
	private static Uint8ClampedArray frameData;
	private static WebJoystick joystick;
	private static boolean lastNearInteractive = false;
	private static Game game;

	private static AnimationFrameCallback frameCallback;

	private WebMain() {
	}

	public static void main(String[] args) {
		int initialWidth = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_WIDTH;
		int initialHeight = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_HEIGHT;
		WebAssets.preload(ASSET_PATHS, () -> start(initialWidth, initialHeight));
	}

	private static void start(int width, int height) {
		HTMLDocument document = HTMLDocument.current();

		visibleCanvas = (HTMLCanvasElement) document.getElementById("game-canvas");
		visibleContext = (CanvasRenderingContext2D) visibleCanvas.getContext("2d");
		// Nearest-neighbor scaling, so the low-res buffer stays crisp/pixelated when
		// upscaled instead of coming out blurry (AWT's default drawImage scaling gave
		// us this for free on desktop; the browser canvas defaults to smoothing it).
		((SmoothingContext2D) visibleContext).setImageSmoothingEnabled(false);

		offscreenCanvas = (HTMLCanvasElement) document.createElement("canvas");
		offscreenContext = (CanvasRenderingContext2D) offscreenCanvas.getContext("2d");
		allocateFrameBuffer(width, height);

		game = new Game();
		game.init(width, height);

		joystick = new WebJoystick(visibleCanvas, Game.input);

		frameCallback = timestamp -> {
			game.tick();
			game.render();
			blit();
			joystick.render(visibleContext);

			boolean nearInteractive = game.player.isNearInteractive();
			if (nearInteractive != lastNearInteractive) {
				lastNearInteractive = nearInteractive;
				WebBridge.setNearInteractive(nearInteractive);
			}

			Window.requestAnimationFrame(frameCallback);
		};
		Window.requestAnimationFrame(frameCallback);
	}

	// Called from the React side (src/components/GameCanvas.tsx) whenever the
	// window/canvas is resized, so the camera's internal resolution tracks the
	// new viewport instead of staying locked to whatever size main() booted
	// with.
	@Export(name = "resize")
	public static void resize(int width, int height) {
		if (game == null) {
			return;
		}
		game.resize(width, height);
		allocateFrameBuffer(width, height);
	}

	private static void allocateFrameBuffer(int width, int height) {
		offscreenCanvas.setWidth(width);
		offscreenCanvas.setHeight(height);

		frameImageData = new ImageData(width, height);
		frameData = frameImageData.getData();
		// Every pixel is fully opaque.
		for (int i = 3; i < frameData.getLength(); i += 4) {
			frameData.set(i, 255);
		}
	}

	private static void blit() {
		int[] pixels = game.pixels;
		for (int i = 0; i < pixels.length; i += 1) {
			int p = pixels[i];
			int r = (p >> 16) & 0xFF;
			int g = (p >> 8) & 0xFF;
			int b = p & 0xFF;
			int offset = i * 4;
			frameData.set(offset, r);
			frameData.set(offset + 1, g);
			frameData.set(offset + 2, b);
			// alpha channel (offset + 3) was pre-filled to 255 and never changes.
		}

		offscreenContext.putImageData(frameImageData, 0, 0);
		visibleContext.drawImage(offscreenCanvas, 0, 0, offscreenCanvas.getWidth(), offscreenCanvas.getHeight(), 0, 0,
				visibleCanvas.getWidth(), visibleCanvas.getHeight());
	}
}
