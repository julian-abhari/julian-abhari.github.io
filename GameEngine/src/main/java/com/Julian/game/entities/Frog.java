package com.Julian.game.entities;

import com.Julian.game.gfx.Colors;
import com.Julian.game.gfx.Screen;
import com.Julian.game.level.Level;

public class Frog extends NPC {
	private int color = new Colors(-1, 142, 152, 555).getColor();
	private long lastHopTime = 0;
	private int hopDelay = 1000;
	private int direction = 1;

	public Frog(Level level, String name, float x, float y, int mass, int hopDelay) {
		super(level, name, x, y, mass);
		this.message = null;
		this.isInteractive = false;
		this.hopDelay = hopDelay;
	}

	public Frog(Level level, String name, float x, float y, int mass, Colors color, int hopDelay) {
		super(level, name, x, y, mass);
		this.color = color.getColor();
		this.message = null;
		this.isInteractive = false;
		this.hopDelay = hopDelay;
	}

	public Frog(Level level, String name, float x, float y, int mass, Colors color, String message, int messageDelay, int hopDelay) {
		super(level, name, x, y, mass);
		this.color = color.getColor();
		this.message = message;
		this.messageDelay = messageDelay;
		this.hopDelay = hopDelay;
	}

	@Override
	public void render(Screen screen) {
		if (this.message != null) {
			super.render(screen);
		}
		int xTile = 26;
		int yTile = 29;
		int flip = direction;

		int xOffset = (int) (x);
		int yOffset = (int) (y);

		screen.render(xOffset, yOffset, xTile + yTile * 32, color, flip == 1, false, scale);
	}

	@Override
	public void tick() {
		super.tick();
		if (!sayMessage && System.currentTimeMillis() - lastHopTime >= hopDelay) {
			applyForce((float) ((direction *= -1) * -1.0), (float) -3.5);
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

}
