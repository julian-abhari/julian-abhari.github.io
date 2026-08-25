package com.Julian.game.entities;

import com.Julian.game.gfx.Colors;
import com.Julian.game.gfx.Screen;
import com.Julian.game.level.Level;

public class BunnyKing extends NPC {
	private int color = new Colors(-1, 000, 421, 555).getColor();
	private long lastHopTime = 0;
	private int hopDelay = 1000;
	private int direction = 1;

	public BunnyKing(Level level, String name, float x, float y, int mass, String currentMessage) {
		super(level, name, x, y, mass);
		messageDelay = 4000;
		message = currentMessage;
		lastHopTime = System.currentTimeMillis();
	}

	@Override
	public void render(Screen screen) {
		super.render(screen);
		int xTile = 20;
		int yTile = 28;

		int modifier = 8 * scale;
		int xOffset = (int) (x - modifier / 2);
		int yOffset = (int) (y - modifier / 2 - 4);

		screen.render(xOffset, yOffset, xTile + yTile * 32, color, false, false, scale);
		screen.render(xOffset + modifier, yOffset, (xTile + 1) + yTile * 32, color, false, false, scale);

		screen.render(xOffset, yOffset + modifier, xTile + (yTile + 1) * 32, color, false, false, scale);
		screen.render(xOffset + modifier, yOffset + modifier, (xTile + 1) + (yTile + 1) * 32, color, false, false,
				scale);
	}

	@Override
	public void tick() {
		super.tick();
		if (!sayMessage && System.currentTimeMillis() - lastHopTime >= hopDelay) {
			applyForce((float) ((direction *= -1) * 1.3), (float) -2.0);
			lastHopTime = System.currentTimeMillis();
		}

		applyForce(0, (float) 0.2);

		if (xVelocity != 0 || yVelocity != 0) {
			move(0, 0);
			isMoving = true;
		} else {
			isMoving = false;
		}
	}

	public void applyForce(float xForce, float yForce) {
		xAcceleration *= 0;
		yAcceleration *= 0;

		xAcceleration += xForce / mass;
		yAcceleration += yForce / mass;

		xVelocity += xAcceleration;
		yVelocity += yAcceleration;

		if (xVelocity > 5) {
			xVelocity = 5;
		}
		if (yVelocity > 5) {
			yVelocity = 5;
		}
		if (xVelocity < -5) {
			xVelocity = -5;
		}
		if (yVelocity < -5) {
			yVelocity = -5;
		}
	}

	public boolean hasCollided(float xVelDir, float yVelDir) {
		int xMin = 0;
		int xMax = 7;
		int yMin = -7;
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
		int yMin = -7;
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

}
