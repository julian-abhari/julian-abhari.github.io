package com.Julian.game.web;

import java.util.HashMap;
import java.util.Map;

import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.canvas.ImageData;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLImageElement;
import org.teavm.jso.typedarrays.Uint8ClampedArray;

/**
 * Loads PNG assets in the browser (via an offscreen &lt;img&gt; + &lt;canvas&gt;)
 * and decodes them into plain packed-ARGB int[] pixel buffers, in exactly the
 * same format java.awt.image.BufferedImage#getRGB(...) would have produced on
 * the desktop build. This lets SpriteSheet/Level keep their original pixel
 * math unchanged.
 */
public final class WebAssets {

	public static final class DecodedImage {
		public final int width;
		public final int height;
		public final int[] argbPixels;

		DecodedImage(int width, int height, int[] argbPixels) {
			this.width = width;
			this.height = height;
			this.argbPixels = argbPixels;
		}
	}

	private static final Map<String, DecodedImage> cache = new HashMap<>();

	private WebAssets() {
	}

	public static void preload(String[] paths, Runnable onAllLoaded) {
		int[] remaining = { paths.length };
		if (paths.length == 0) {
			onAllLoaded.run();
			return;
		}
		for (String path : paths) {
			decode(path, image -> {
				cache.put(path, image);
				remaining[0] -= 1;
				if (remaining[0] == 0) {
					onAllLoaded.run();
				}
			});
		}
	}

	public static DecodedImage get(String path) {
		return cache.get(path);
	}

	private interface DecodeCallback {
		void onDecoded(DecodedImage image);
	}

	private static void decode(String path, DecodeCallback callback) {
		HTMLDocument document = HTMLDocument.current();
		HTMLImageElement img = (HTMLImageElement) document.createElement("img");

		img.addEventListener("load", (EventListener<Event>) event -> {
			int width = img.getNaturalWidth();
			int height = img.getNaturalHeight();

			HTMLCanvasElement canvas = (HTMLCanvasElement) document.createElement("canvas");
			canvas.setWidth(width);
			canvas.setHeight(height);

			CanvasRenderingContext2D context = (CanvasRenderingContext2D) canvas.getContext("2d");
			context.drawImage(img, 0, 0);

			ImageData imageData = context.getImageData(0, 0, width, height);
			Uint8ClampedArray data = imageData.getData();

			int[] argbPixels = new int[width * height];
			for (int i = 0; i < argbPixels.length; i += 1) {
				int a = data.get(i * 4 + 3) & 0xFF;
				int r = data.get(i * 4) & 0xFF;
				int g = data.get(i * 4 + 1) & 0xFF;
				int b = data.get(i * 4 + 2) & 0xFF;
				argbPixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
			}

			callback.onDecoded(new DecodedImage(width, height, argbPixels));
		});

		img.setSrc(path);
	}
}
