package com.Julian.game.entities;

import com.Julian.game.gfx.Colors;
import com.Julian.game.gfx.Screen;
import com.Julian.game.level.Level;

public class BasicBunny extends Mob {
	private int color = new Colors(-1, 000, 421, 555).getColor();
	private long lastHopTime = 0;
	private int hopDelay = 1500;
	private int direction = 1;

	public BasicBunny(Level level, String name, float x, float y, int mass) {
		super(level, name, x, y, mass);
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

	@Override
	public void tick() {
		if (System.currentTimeMillis() - lastHopTime >= hopDelay) {
			applyForce((float) ((direction *= -1) * -1.0), (float) -2.4);
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

	@Override
	public void render(Screen screen) {
		int xTile = 16;
		int yTile = 28;
		int flip = direction;

		int xOffset = (int) (x);
		int yOffset = (int) (y);

		screen.render(xOffset, yOffset, xTile + yTile * 32, color, flip == 1, false, scale);
	}

}
