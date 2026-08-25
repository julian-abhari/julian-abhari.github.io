package com.Julian.game.level.tiles;

import com.Julian.game.gfx.Colors;
import com.Julian.game.gfx.Screen;
import com.Julian.game.level.Level;

public class TreeTile extends Tile {

	protected int tileId;
	protected Colors tileColor;

	public TreeTile(int id, int x, int y, Colors tileColor, int levelColor) {
		super(id, true, false, false, false, 0, levelColor);
		this.tileColor = tileColor;
		this.tileId = x + y * 32;
		this.solid = true;
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Screen screen, Level level, int x, int y, boolean flipX, boolean flipY) {
		screen.render(x - 4, y - 8, tileId - 1, tileColor.getColor(), false, false, 1);
		screen.render(x + 4, y - 8, tileId - 1, tileColor.getColor(), true, false, 1);
		screen.render(x, y, tileId, tileColor.getColor(), false, false, 1);
	}
}
