package com.Julian.game.level.tiles;

import com.Julian.game.gfx.Colors;

public class AnimatedBouncyTile extends AnimatedTile {

	public AnimatedBouncyTile(int id, int[][] animationTileCoords, Colors tileColor, int levelImageColor,
			int animationSwitchDelay) {
		super(id, animationTileCoords, tileColor, levelImageColor, animationSwitchDelay);
		this.bouncy = true;
	}

}
