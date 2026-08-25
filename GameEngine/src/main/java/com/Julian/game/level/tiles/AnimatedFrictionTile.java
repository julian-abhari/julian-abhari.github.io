package com.Julian.game.level.tiles;

import com.Julian.game.gfx.Colors;

public class AnimatedFrictionTile extends AnimatedTile {
	public AnimatedFrictionTile(int id, int[][] animationTileCoords, Colors tileColor, int levelImageColor,
			int animationSwitchDelay, double friction) {
		super(id, animationTileCoords, tileColor, levelImageColor, animationSwitchDelay);
		this.friction = friction;
	}
}
