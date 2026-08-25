package com.Julian.game.level.tiles;

import com.Julian.game.gfx.Colors;

public class BasicBouncyTile extends BasicTile {

	public BasicBouncyTile(int id, int x, int y, Colors tileColor, int levelImageColor) {
		super(id, x, y, tileColor, levelImageColor);
		this.bouncy = true;
		this.friction = 0;
	}

}
