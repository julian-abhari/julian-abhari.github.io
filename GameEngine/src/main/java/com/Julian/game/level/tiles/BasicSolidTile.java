package com.Julian.game.level.tiles;

import com.Julian.game.gfx.Colors;

public class BasicSolidTile extends BasicTile {

	public BasicSolidTile(int id, int x, int y, Colors tileColor, int levelImageColor) {
		super(id, x, y, tileColor, levelImageColor);
		this.solid = true;
		this.friction = 0;
	}

}
