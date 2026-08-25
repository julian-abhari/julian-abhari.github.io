package com.Julian.game.entities;

import com.Julian.game.gfx.Colors;
import com.Julian.game.gfx.Screen;
import com.Julian.game.level.Level;

public class Dad extends NPC {
	private int color = new Colors(-1, 320, 334, 544).getColor();

	public Dad(Level level, String name, float x, float y, int mass) {
		super(level, name, x, y, mass);
		message = "When you get back, I'll make chicken biscuits!";
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
	public void tick() {
		super.tick();
	}

	@Override
	public void render(Screen screen) {
		super.render(screen);
		int xTile = 12;
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
