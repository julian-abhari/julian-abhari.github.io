package com.Julian.game;

import com.Julian.game.entities.Player;
import com.Julian.game.gfx.Screen;
import com.Julian.game.gfx.SpriteSheet;
import com.Julian.game.level.Level;

/**
 * General Game Engine 10/3/17
 *
 * @author Julian Abhari
 */

// Ported from the original desktop AWT/Swing engine to run headless (no
// Canvas/JFrame/BufferStrategy) so it can be driven from a browser via
// TeaVM. See com.Julian.game.web.WebMain for the requestAnimationFrame-driven
// loop that now owns ticking/rendering/blitting to the real <canvas>.
public class Game {

	public static final int WIDTH = 160;
	public static final int HEIGHT = (WIDTH / 12) * 9;
	public static final int SCALE = 4;
	public static final String NAME = "Julian's General Game Engine";
	public static Game game;

	public int tickCount = 0;

	// This is the low-res frame buffer that render() populates each frame. The
	// web entry point (WebMain) is responsible for pushing this onto a real
	// HTML canvas.
	public int[] pixels = new int[WIDTH * HEIGHT];
	// This will contain information of the 4 colors of pixels within the tiles
	public int[] colors = new int[6 * 6 * 6];

	private Screen screen;
	public static InputHandler input;

	public Level level;
	public Player player;

	public void init() {
		game = this;
		int index = 0;
		for (int r = 0; r < 6; r += 1) {
			for (int g = 0; g < 6; g += 1) {
				for (int b = 0; b < 6; b += 1) {
					// This is the red shade from 0-5
					int rr = (r * 255 / 5);
					// This is the green shade from 0-5
					int gg = (g * 255 / 5);
					// This is the blue shade from 0-5
					int bb = (b * 255 / 5);
					// This populates the color array with color values.
					colors[index] = rr << 16 | gg << 8 | bb;
					index += 1;
				}
			}
		}

		screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/game/SpriteSheet.png"));
		input = new InputHandler();
		level = new Level("/game/Levels/water_test.png");
		player = new Player(level, (level.width / 2) * 8, (level.height / 2) * 8, input, null);
		level.addEntity(player);
	}

	// This updates the game, it updates the internal variables and the logic of the
	// game
	public void tick() {
		tickCount += 1;
		level.tick();
	}

	public void render() {
		// These are the coordinates of the center of the screen
		int xOffset = player.x - (screen.width / 2);
		int yOffset = player.y - (screen.height / 2);

		level.renderTiles(screen, xOffset, yOffset);

		level.renderEntities(screen);

		for (int y = 0; y < screen.height; y++) {
			for (int x = 0; x < screen.width; x++) {
				int ColourCode = screen.pixels[x + y * screen.width];
				if (ColourCode < 255) {
					pixels[x + y * WIDTH] = colors[ColourCode];
				}
			}
		}
	}

}
