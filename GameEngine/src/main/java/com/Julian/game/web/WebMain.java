package com.Julian.game.web;

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
 *   src/components/GameCanvas.tsx on the Next.js side, 640x480) and create a second,
 *   offscreen low-res (Game.WIDTH x Game.HEIGHT) canvas used as a cheap 1:1 pixel
 *   blit target for Game's frame buffer.
 * - Drive Game.tick()/Game.render() from requestAnimationFrame, then upscale the
 *   low-res buffer onto the visible canvas each frame (nearest-neighbor, to match
 *   the crisp pixelated look the desktop AWT version got for free).
 */
public final class WebMain {

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
	private static CanvasRenderingContext2D offscreenContext;
	private static ImageData frameImageData;
	private static Uint8ClampedArray frameData;
	private static WebJoystick joystick;
	private static boolean lastNearInteractive = false;

	private static AnimationFrameCallback frameCallback;

	private WebMain() {
	}

	public static void main(String[] args) {
		WebAssets.preload(ASSET_PATHS, WebMain::start);
	}

	private static void start() {
		HTMLDocument document = HTMLDocument.current();

		HTMLCanvasElement visibleCanvas = (HTMLCanvasElement) document.getElementById("game-canvas");
		visibleContext = (CanvasRenderingContext2D) visibleCanvas.getContext("2d");
		// Nearest-neighbor scaling, so the low-res buffer stays crisp/pixelated when
		// upscaled instead of coming out blurry (AWT's default drawImage scaling gave
		// us this for free on desktop; the browser canvas defaults to smoothing it).
		((SmoothingContext2D) visibleContext).setImageSmoothingEnabled(false);

		HTMLCanvasElement offscreenCanvas = (HTMLCanvasElement) document.createElement("canvas");
		offscreenCanvas.setWidth(Game.WIDTH);
		offscreenCanvas.setHeight(Game.HEIGHT);
		offscreenContext = (CanvasRenderingContext2D) offscreenCanvas.getContext("2d");

		frameImageData = new ImageData(Game.WIDTH, Game.HEIGHT);
		frameData = frameImageData.getData();
		// Every pixel is fully opaque.
		for (int i = 3; i < frameData.getLength(); i += 4) {
			frameData.set(i, 255);
		}

		Game game = new Game();
		game.init();

		joystick = new WebJoystick(visibleCanvas, Game.input);

		frameCallback = timestamp -> {
			game.tick();
			game.render();
			blit(game, offscreenCanvas, visibleCanvas);
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

	private static void blit(Game game, HTMLCanvasElement offscreenCanvas, HTMLCanvasElement visibleCanvas) {
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
		visibleContext.drawImage(offscreenCanvas, 0, 0, Game.WIDTH, Game.HEIGHT, 0, 0, visibleCanvas.getWidth(),
				visibleCanvas.getHeight());
	}
}
