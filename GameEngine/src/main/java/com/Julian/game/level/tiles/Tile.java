package com.Julian.game.level.tiles;

import com.Julian.game.gfx.Colors;
import com.Julian.game.gfx.Screen;
import com.Julian.game.level.Level;

public abstract class Tile {

	// 256 is the maximum amount of tiles that can be in the board
	public static final Tile[] tiles = new Tile[256];
	// Envrionmental Tiles
	public static final Tile VOID = new BasicSolidTile(0, 0, 0, new Colors(000, -1, -1, -1), 0xFF000000);
	public static final Tile STONE = new BasicSolidTile(23, 14, 0, new Colors(-1, 223, 234, 455), 0xFF434579);
	public static final Tile WATER = new AnimatedFrictionTile(3, new int[][] { { 0, 5 }, { 1, 5 }, { 2, 5 }, { 1, 5 } },
			new Colors(-1, 004, 115, -1), 0xFF0000FF, 1000, 0.95);
	public static final Tile SHAGGY_GRASS = new BasicSolidTile(9, 5, 0, new Colors(-1, 242, 353, -1), 0xFF56ED7B);
	public static final Tile BG_SHAGGY_GRASS = new BasicTile(17, 5, 0, new Colors(-1, 131, 141, -1), 0xFF2C6E3C);
	public static final Tile BG_GRASS = new BasicTile(45, 1, 0, new Colors(-1, 131, 141, -1), 0xFF22562f);
	public static final Tile SOLID_DIRT_GRASS = new BasicSolidTile(20, 17, 0, new Colors(210, 321, 141, 252),
			0xFF68411A);
	public static final Tile DIRT_BG = new BasicTile(51, 5, 0, new Colors(-1, 110, 333, -1),
			0xFF794c41);
	public static final Tile LEAFY_BLUES = new BasicTile(19, 16, 0, new Colors(-1, -1, 123, 133), 0xFF3f88ca);
	public static final Tile VINE = new BasicSolidTile(10, 6, 0, new Colors(-1, -1, 020, 131), 0xFF05481F);
	public static final Tile GRASS_FLOWER = new BasicSolidTile(12, 7, 0, new Colors(242, 524, 412, 554), 0xFFB42B4B);
	public static final Tile BG_GRASS_FLOWER = new BasicTile(59, 7, 0, new Colors(131, 524, 412, 554), 0xFFee3b4d);
	public static final Tile BLUE_MOUNTAIN = new BasicSolidTile(15, 1, 0, new Colors(-1, 144, -1, -1), 0xFF15CAD0);
	public static final Tile COTTON_CLOUD = new BasicSolidTile(16, 13, 0, new Colors(435, -1, 545, 534), 0xFFCFB8D4);
	public static final Tile BG_BLUE_MOUNTAIN = new BasicTile(18, 1, 0, new Colors(-1, 023, 134, -1), 0xFF234F83);
	public static final Tile FRUITY_TREE = new TreeTile(25, 21, 0, new Colors(001, 302, 413, 153), 0xFF8a5809);
	public static final Tile LOG = new BasicSolidTile(28, 24, 0, new Colors(-1, 210, 320, -1), 0xFF4f321f);
	public static final Tile LOG_BG = new BasicTile(36, 24, 0, new Colors(-1, 210, 320, -1), 0xFF70472c);
	public static final Tile PINE_TREE_LOG = new BasicTile(50, 24, 0, new Colors(-1, 110, 110, -1), 0xFF5f3d26);
	public static final Tile SHROOM_HEAD = new BasicBouncyTile(52, 5, 1, new Colors(123, 333, 411, 555), 0xFFeb262d);
	public static final Tile SHROOM_STEM = new BasicSolidTile(53, 6, 1, new Colors(123, 333, 444, 555), 0xFFebc097);
	public static final Tile DEAD_BUNNY = new BasicSolidTile(54, 17, 28, new Colors(242, 111, 421, 555), 0xFFeba49d);
	public static final Tile FAIRY_HOUSE_TL = new BasicTile(55, 7, 1, new Colors(123, 333, 411, 555), 0xFFcb233d);
	public static final Tile FAIRY_HOUSE_TR = new BasicTile(56, 8, 1, new Colors(123, 333, 411, 555), 0xFFbd2139);
	public static final Tile FAIRY_HOUSE_BL = new BasicTile(57, 7, 2, new Colors(123, 333, 411, 555), 0xFFb01e35);
	public static final Tile FAIRY_HOUSE_BR = new BasicTile(58, 8, 2, new Colors(001, 333, 411, 555), 0xFF931a2c);
	public static final Tile LEAFY_GREENS = new BasicTile(60, 16, 0, new Colors(-1, -1, 231, 241), 0xFF127921);
	public static final Tile DEAD_FAIRY = new BasicSolidTile(62, 18, 28, new Colors(435, 302, 502, 555), 0xFFd9a19e);
	public static final Tile LAVA = new AnimatedFrictionTile(63, new int[][] { { 0, 5 }, { 1, 5 }, { 2, 5 }, { 1, 5 } },
			new Colors(-1, 400, 511, -1), 0xFFFF0000, 1000, 0.95);
	// Overworld
	public static final Tile AIR = new BasicTile(4, 0, 0, new Colors(435, -1, -1, -1), 0xFF7db8ff); //345
	public static final Tile AIR_FLOWER = new BasicTile(11, 7, 0, new Colors(345, 524, 412, 554), 0xFFFC3664);
	// Caves
	public static final Tile BG_CAVE = new BasicTile(21, 1, 0, new Colors(-1, 001, -1, -1), 0xFF182534);
	public static final Tile ORE = new BasicTile(24, 18, 0, new Colors(-1, 001, 222, 411), 0xFFcc2e65);
	public static final Tile LUMINOUS_CRYSTAL = new BasicSolidTile(22, 19, 0, new Colors(-1, -1, 505, 524), 0xFF640A83);
	public static final Tile TITAN_STONE = new BasicSolidTile(26, 22, 0, new Colors(224, 243, 434, 423), 0xFF00a8aa);
	public static final Tile TITAN_STONE_HEAD = new BasicSolidTile(27, 23, 0, new Colors(224, 243, 434, 423), 0xFFab27a9);
	// Obstacle Tiles
	public static final Tile FLOOR_SNAPPER = new AnimatedBouncyTile(13, new int[][] { { 8, 0 }, { 9, 0 } },
			new Colors(345, 142, 032, 555), 0xFFAB803C, 500);
	public static final Tile CEILING_SNAPPER = new AnimatedBouncyTile(14, new int[][] { { 10, 0 }, { 11, 0 } },
			new Colors(345, 142, 032, 555), 0xFF9C6208, 500);
	// House Tiles
	public static final Tile ROOF_DL = new BasicSolidTile(29, 25, 0, new Colors(131, 321, 332, 422), 0xFF855637);
	public static final Tile ROOF_UL = new BasicSolidTile(30, 27, 0, new Colors(131, 321, 332, 422), 0xFF805335);
	public static final Tile ROOF = new BasicSolidTile(31, 26, 0, new Colors(-1, 321, 332, -1), 0xFF9e6641);
	public static final Tile BRICK = new BasicTile(32, 28, 0, new Colors(-1, -1, 310, 211), 0xFFbc5b3f);
	public static final Tile FLOOR = new BasicSolidTile(33, 0, 0, new Colors(432, -1, -1, -1), 0xFF9c7f59);
	public static final Tile FLOOR_BG = new BasicTile(34, 0, 0, new Colors(432, -1, -1, -1), 0xFFffcf90);
	public static final Tile HOUSE_WALL = new BasicTile(35, 0, 0, new Colors(322, -1, -1, -1), 0xFFc88565);
	public static final Tile FIRE = new AnimatedTile(37, new int[][] { { 29, 0 }, { 30, 0, }, { 31, 0 } },
			new Colors(000, 410, 530, 554), 0xFFff8f31, 500);
	public static final Tile COUCH_UL = new BasicTile(38, 0, 1, new Colors(322, 010, 131, 142), 0xFF8fc887);
	public static final Tile COUCH_UR = new BasicTile(39, 1, 1, new Colors(322, 010, 131, 142), 0xFF88be80);
	public static final Tile COUCH_DL = new BasicTile(40, 0, 2, new Colors(432, 010, 131, 142), 0xFF76a670);
	public static final Tile COUCH_DR = new BasicTile(41, 1, 2, new Colors(432, 010, 131, 142), 0xFF658d5f);
	public static final Tile DOOR = new BasicTile(42, 2, 1, new Colors(-1, 110, 221, 554), 0xFFff9e6f);
	public static final Tile BARBECUE = new BasicTile(43, 3, 1, new Colors(131, 222, 000, 333), 0xFF72625f);
	public static final Tile BARBECUE_LID = new BasicTile(44, 4, 1, new Colors(131, 222, -1, -1), 0xFF725048);
	public static final Tile STACK_OF_BOOKS = new BasicTile(61, 9, 1, new Colors(322, 113, 254, 555), 0xFFbac861);
	// Tootie's Shenanigans
	public static final Tile TOOTIE_TORSO_RIGHT = new AnimatedBouncyTile(46, new int[][] { { 14, 29 }, { 14, 28 } }, new Colors(000, 201, 423, 543), 0, 250);
	public static final Tile TOOTIE_HEAD_RIGHT = new AnimatedBouncyTile(47, new int[][] { { 15, 29 }, { 15, 28 } }, new Colors(000, 201, 423, 543), 0, 250);
	public static final Tile TOOTIE_TORSO_LEFT = new AnimatedBouncyTile(48, new int[][] { { 15, 27 }, { 15, 26 } }, new Colors(000, 201, 423, 543), 0, 250);
	public static final Tile TOOTIE_HEAD_LEFT = new AnimatedBouncyTile(49, new int[][] { { 14, 27 }, { 14, 26 } }, new Colors(000, 201, 423, 543), 0, 250);
	// Industrial Tiles
	public static final Tile SOLID_METAL = new BasicSolidTile(1, 2, 0, new Colors(-1, 111, 444, 555), 0xFF555555);
	public static final Tile BG_METAL = new BasicTile(2, 1, 0, new Colors(-1, 444, -1, -1), 0xFFFFFFFF);
	public static final Tile IRON_BARS = new BasicSolidTile(5, 3, 0, new Colors(444, 222, 333, -1), 0xFF8F8F8F);
	public static final Tile BUILDING = new BasicTile(6, 1, 0, new Colors(-1, 333, -1, -1), 0xFFA5A5A5);
	public static final Tile BUILDING_SIGN = new BasicTile(7, 1, 0, new Colors(-1, 555, -1, -1), 0xFFD4DAD9);
	public static final Tile CAGE_TOP = new BasicSolidTile(8, 4, 0, new Colors(111, -1, 333, -1), 0xFF696969);

	protected byte id;
	// Collision detection
	protected boolean solid;
	// Light
	protected boolean emitter;
	// Interactive
	protected boolean interactive;
	// Bouncy Tile
	protected boolean bouncy;
	// Level of Friction 0-1
	protected double friction;
	// This is the color ID for image to tile.
	private int levelImageColor;

	public Tile(int id, boolean isSolid, boolean isEmitter, boolean isInteractive, boolean isBouncy, double friction,
			int levelImageColor) {
		this.id = (byte) id;
		if (tiles[id] != null) {
			throw new RuntimeException("Duplicate tile id on " + id);
		}
		this.solid = isSolid;
		this.emitter = isEmitter;
		this.interactive = isInteractive;
		this.bouncy = isBouncy;
		this.levelImageColor = levelImageColor;
		this.friction = friction;
		tiles[id] = this;
	}

	public byte getId() {
		return id;
	}

	public boolean isSolid() {
		return solid;
	}

	public boolean isEmitter() {
		return emitter;
	}

	public boolean isInteractive() {
		return interactive;
	}

	public boolean isBouncy() {
		return bouncy;
	}

	public double getFriction() {
		return friction;
	}

	public int getLevelImageColor() {
		return levelImageColor;
	}

	public abstract void tick();

	public abstract void render(Screen screen, Level level, int x, int y, boolean flipX, boolean flipY);

}
