package com.Julian.game.level.tiles;

import com.Julian.game.gfx.Colors;
import com.Julian.game.gfx.Screen;
import com.Julian.game.level.Level;

public class BasicTile extends Tile {

	protected int tileId;
	public Colors tileColor;

	public BasicTile(int id, int x, int y, Colors tileColor, int levelImageColor) {
		super(id, false, false, false, false, 1, levelImageColor);
		this.tileId = x + y * 32;
		this.tileColor = tileColor;
	}

	public void tick() {

	}

	public void render(Screen screen, Level level, int x, int y) {
		screen.render(x, y, tileId, tileColor.getColor(), false, false, 1);
	}

	public void render(Screen screen, Level level, int x, int y, boolean flipX, boolean flipY) {
		screen.render(x, y, tileId, tileColor.getColor(), flipX, flipY, 1);
	}

}
