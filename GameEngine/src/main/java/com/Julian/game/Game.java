package com.Julian.game;

import com.Julian.game.entities.BasicBunny;
import com.Julian.game.entities.BunnyKing;
import com.Julian.game.entities.Dad;
import com.Julian.game.entities.Fairy;
import com.Julian.game.entities.Frog;
import com.Julian.game.entities.FrogQueen;
import com.Julian.game.entities.GoblinBully;
import com.Julian.game.entities.Mom;
import com.Julian.game.entities.NiceFairy;
import com.Julian.game.entities.Owl;
import com.Julian.game.entities.Player;
import com.Julian.game.entities.Plesiosaur;
import com.Julian.game.entities.Raptor;
import com.Julian.game.entities.SavageFairy;
import com.Julian.game.entities.Triceratops;
import com.Julian.game.gfx.Colors;
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

	public static final String NAME = "Julian's General Game Engine";
	public static Game game;

	public int tickCount = 0;

	// This is the low-res frame buffer that render() populates each frame. The
	// web entry point (WebMain) is responsible for pushing this onto a real
	// HTML canvas. Sized to width*height, which now tracks the browser
	// viewport (via init()/resize()) instead of a fixed constant.
	public int[] pixels;
	public int width;
	public int height;
	// This will contain information of the 4 colors of pixels within the tiles
	public int[] colors = new int[6 * 6 * 6];

	private Screen screen;
	public static InputHandler input;

	public Level level;
	public Player player;

	public void init(int width, int height) {
		game = this;
		this.width = width;
		this.height = height;
		this.pixels = new int[width * height];

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

		screen = new Screen(width, height, new SpriteSheet("/game/SpriteSheet.png"));
		input = new InputHandler();
		level = new Level("/game/Levels/Adventure.png");

		Mom mom = new Mom(level, "Amy", 100, 1446, 1);
		level.addEntity(mom);
		Dad dad = new Dad(level, "Art", 32 * 8, 183 * 8, 1);
		level.addEntity(dad);
		BunnyKing sadLonelyBunny = new BunnyKing(level, "BunnyKingWEIRD", 58 * 8, 183 * 8, 1,
				"Did you know that dogs can be used as trampolines? It's true! You just need the key of spaces.");
		level.addEntity(sadLonelyBunny);
		BunnyKing bulliedBunny = new BunnyKing(level, "BunnyKingBullied", 196 * 8, 187 * 8, 1, "I wish I had friends..");
		level.addEntity(bulliedBunny);
		GoblinBully bully = new GoblinBully(level, "cool guy", 189 * 8, 188 * 8, 1);
		level.addEntity(bully);
		BasicBunny mushroomBunny = new BasicBunny(level, "MushroomHopper", 110 * 8, 183 * 8, 1);
		level.addEntity(mushroomBunny);
		SavageFairy hungryFairy = new SavageFairy(level, "HungryFairy", 146 * 8, 180 * 8, 1);
		level.addEntity(hungryFairy);
		Fairy fairyInPeril = new NiceFairy(level, "FairyInPeril", 158 * 8, 181 * 8, 1,
				"Please! We need your help. Some of our evil fairies want to eat bunny island! You have to stop them.");
		level.addEntity(fairyInPeril);
		FrogQueen frogQueen = new FrogQueen(level, "Frog Queen", 144 * 8, 156 * 8, 1);
		level.addEntity(frogQueen);
		Frog jerry = new Frog(level, "Jerry", 140 * 8, 153 * 8, 1, 1000);
		level.addEntity(jerry);
		Frog lilah = new Frog(level, "Lilah", 95 * 8, 155 * 8, 1, new Colors(-1, 412, 523, 555),
				"I love jumping around so much! You should try it!", 2000, 1000);
		level.addEntity(lilah);
		Frog michael = new Frog(level, "Michael", 100 * 8, 156 * 8, 1, new Colors(-1, 421, 532, 555),
				"Cake tastes so good... but do you ever wonder about that cake's family?", 3500, 700);
		level.addEntity(michael);
		BunnyKing happyBunny = new BunnyKing(level, "BunnyKingHappy", 150 * 8, 156 * 8, 1,
				"These frogs make me so happy! They're so nice to me");
		level.addEntity(happyBunny);
		Owl god = new Owl(level, "God Owl", 490, 104 * 8, 1, "I see all. And you scare me.", 2000);
		level.addEntity(god);
		Owl bigBro = new Owl(level, "bigBro", 92 * 8, 103 * 8, 1, "I can't believe it! My brother ate all the seed cookies",
				3000);
		level.addEntity(bigBro);
		Owl littleBro = new Owl(level, "littleBro", 90 * 8, 112 * 8, 1,
				"I've heard that fairies can turn into terrifying demons. I wonder if they're stronger than my brother.",
				4000);
		level.addEntity(littleBro);
		Owl hideAndSeekChampion = new Owl(level, "hideAndSeekChampion", 105 * 8, 101 * 8, 1,
				"Shh.. I'm playing hide and seek. I've been winning for 3 months now. Going for the world record.", 4000);
		level.addEntity(hideAndSeekChampion);
		Fairy assassin = new Fairy(level, "assassin", 105 * 8, 111 * 8, 1, new Colors(-1, 302, 502, 555).getColor(),
				"I've been ordered to kill you... but I'm not one to follow orders.", 3500);
		level.addEntity(assassin);
		Owl curiousOwl = new Owl(level, "curiousOwl", 117 * 8, 117 * 8, 1,
				"Why do our houses only have this one chair as furniture?", 2000);
		level.addEntity(curiousOwl);
		Plesiosaur plessie = new Plesiosaur(level, "plessie", 130 * 8, 63 * 8, 1);
		level.addEntity(plessie);
		Triceratops Cera = new Triceratops(level, "Cera", 146 * 8, 63 * 8, 1);
		level.addEntity(Cera);
		Raptor raptorRun = new Raptor(level, "raptorRun", 156 * 8, 63 * 8, 1);
		level.addEntity(raptorRun);
		BunnyKing bunnyKingThanks = new BunnyKing(level, "bunnyKingThanks", 142 * 8, 63 * 8, 1,
				"Hey. I just wanted to say thank you. I've seen you use your courage to progress, and it's really inspiring");
		level.addEntity(bunnyKingThanks);
		BasicBunny fairySqausher = new BasicBunny(level, "fairySqausher", 43 * 8, 45 * 8, 1);
		level.addEntity(fairySqausher);
		BasicBunny happyBunny1 = new BasicBunny(level, "happyBunny1", 18 * 8, 47 * 8, 1);
		level.addEntity(happyBunny1);
		BasicBunny happyBunny2 = new BasicBunny(level, "fairySqausher", 21 * 8, 46 * 8, 1);
		level.addEntity(happyBunny2);
		BunnyKing bunnyKingVictorious = new BunnyKing(level, "BunnyKingVictorious", 28 * 8, 46 * 8, 1,
				"These fairies wanted to eat us! Not on my watch. I'm the bunny king!");
		level.addEntity(bunnyKingVictorious);
		Fairy happyFairy = new NiceFairy(level, "happyFairy", 54 * 8, 42 * 8, 1,
				"I told them they shouldn't try to eat bunny island.. bunnies are too strong.");
		level.addEntity(happyFairy);
		Fairy bunnyFairy = new NiceFairy(level, "bunnyFairy", 15 * 8, 47 * 8, 1, "I love these cute little bunnies");
		level.addEntity(bunnyFairy);

		player = new Player(level, 11 * 8, 183 * 8, input);
		level.addEntity(player);
	}

	// Called when the browser window resizes, so more (or less) of the level
	// becomes visible instead of the same fixed view just being stretched.
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = new int[width * height];
		screen.resize(width, height);
	}

	// This updates the game, it updates the internal variables and the logic of the
	// game
	public void tick() {
		tickCount += 1;
		level.tick();
	}

	public void render() {
		// These are the coordinates of the center of the screen
		int xOffset = (int) player.x - (screen.width / 2);
		int yOffset = (int) player.y - (screen.height / 2);

		level.renderTiles(screen, xOffset, yOffset);

		level.renderEntities(screen);

		for (int y = 0; y < screen.height; y++) {
			for (int x = 0; x < screen.width; x++) {
				int ColourCode = screen.pixels[x + y * screen.width];
				if (ColourCode < 255) {
					pixels[x + y * width] = colors[ColourCode];
				}
			}
		}
	}

}
