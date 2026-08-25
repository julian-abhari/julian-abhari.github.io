package com.Julian.game.entities;

import com.Julian.game.gfx.Screen;
import com.Julian.game.level.Level;

public class Fairy extends NPC {
	protected int color;
	protected boolean floating;

	public Fairy(Level level, String name, float x, float y, int mass, int color) {
		super(level, name, x, y, mass);
		this.color = color;
		floating = false;
	}

	public Fairy(Level level, String name, float x, float y, int mass, int color, String message, int messageDelay) {
		super(level, name, x, y, mass);
		this.color = color;
		floating = false;
		this.message = message;
		this.messageDelay = messageDelay;
	}

	public boolean hasCollided(float xVelDir, float yVelDir) {
		int xMin = 0;
		int xMax = 7;
		int yMin = 0;
		int yMax = 7;

		for (int x = xMin; x < xMax; x += 1) {
			if (isSolidTile((int) (xVelDir), (int) (yVelDir), x, yMin)) {
				return true;
			}
		}
		for (int x = xMin; x < xMax; x += 1) {
			if (isSolidTile((int) (xVelDir), (int) (yVelDir), x, yMax)) {
				return true;
			}
		}
		for (int y = yMin; y < yMax; y += 1) {
			if (isSolidTile((int) (xVelDir), (int) (yVelDir), xMin, y)) {
				return true;
			}
		}
		for (int y = yMin; y < yMax; y += 1) {
			if (isSolidTile((int) (xVelDir), (int) (yVelDir), xMax, y)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasBounced(float xVelDir, float yVelDir) {
		int xMin = 0;
		int xMax = 7;
		int yMin = 0;
		int yMax = 7;

		for (int x = xMin; x < xMax; x += 1) {
			if (isBouncyTile((int) (xVelDir), (int) (yVelDir), x, yMin)) {
				return true;
			}
		}
		for (int x = xMin; x < xMax; x += 1) {
			if (isBouncyTile((int) (xVelDir), (int) (yVelDir), x, yMax)) {
				return true;
			}
		}
		for (int y = yMin; y < yMax; y += 1) {
			if (isBouncyTile((int) (xVelDir), (int) (yVelDir), xMin, y)) {
				return true;
			}
		}
		for (int y = yMin; y < yMax; y += 1) {
			if (isBouncyTile((int) (xVelDir), (int) (yVelDir), xMax, y)) {
				return true;
			}
		}
		return false;
	}

	public void render(Screen screen) {
		super.render(screen);
		int xTile = 18;
		int yTile = 28;

		int xOffset = (int) (x);
		int yOffset = (int) (y);
		if (floating) {
			yOffset = (int) (y) - 8;
		}

		screen.render(xOffset, yOffset, xTile + yTile * 32, color, false, false, scale);
	}

}
