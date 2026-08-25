package com.Julian.game.entities;

import com.Julian.game.gfx.Colors;
import com.Julian.game.gfx.Screen;
import com.Julian.game.level.Level;

public class FrogQueen extends NPC {
	private int color = new Colors(-1, 141, 541, 555).getColor();

	public FrogQueen(Level level, String name, float x, float y, int mass) {
		super(level, name, x, y, mass);
		message = "Ribbit.. You're not a failure if you never give up. ..Ribbit";
		messageDelay = 3500;
	}

	@Override
	public boolean hasCollided(float xAmount, float yAmount) {
		return false;
	}

	@Override
	public boolean hasBounced(float xAmount, float yAmount) {
		return false;
	}

	@Override
	public void render(Screen screen) {
		super.render(screen);
		int xTile = 24;
		int yTile = 28;

		int modifier = 8 * scale;
		int xOffset = (int) (x - modifier / 2);
		int yOffset = (int) (y - modifier / 2 - 4);

		screen.render(xOffset, yOffset, xTile + yTile * 32, color, false, false, scale);
		screen.render(xOffset + modifier, yOffset, (xTile + 1) + yTile * 32, color, false, false, scale);

		screen.render(xOffset, yOffset + modifier, xTile + (yTile + 1) * 32, color, false, false, scale);
		screen.render(xOffset + modifier, yOffset + modifier, (xTile + 1) + (yTile + 1) * 32, color, false, false, scale);
	}

}
