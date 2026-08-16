package com.Julian.game.gfx;

import com.Julian.game.web.WebAssets;

public class SpriteSheet {

	// This is the path to the SpriteSheet image
	public String path;
	public int width;
	public int height;

	// This is the pixel data of the SpriteSheet
	public int[] pixels;

	public SpriteSheet(String path) {
		// This is looking up the already-decoded image from WebAssets (populated via
		// WebAssets.preload(...) before the game boots), instead of the old
		// ImageIO.read(...)/BufferedImage.getRGB(...) desktop mechanism.
		WebAssets.DecodedImage image = WebAssets.get(path);

		if (image == null) {
			return;
		}

		this.path = path;
		this.width = image.width;
		this.height = image.height;

		// This is setting the pixels array to the color data of all the pixels from the
		// image
		pixels = image.argbPixels.clone();

		for (int i = 0; i < pixels.length; i += 1) {
			// This is removing the alpha channel fromt the pixel data.
			// This is also setting it into only 4 different colors by dividing by the total amount of color shades by 4
			pixels[i] = (pixels[i] & 0xff) / (256/4);
		}
	}
}
