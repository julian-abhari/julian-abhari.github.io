package com.Julian.game.level.tiles;

import com.Julian.game.gfx.Colors;

public abstract class BasicInteractiveTile extends BasicTile {

	public BasicInteractiveTile(int id, int x, int y, Colors tileColor, int levelImageColor) {
		super(id, x, y, tileColor, levelImageColor);
		this.interactive = true;
	}

	public abstract void doAction();

}
