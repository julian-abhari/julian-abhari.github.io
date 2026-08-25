package com.Julian.game.entities;

import com.Julian.game.gfx.Colors;
import com.Julian.game.level.Level;

public class NiceFairy extends Fairy {
	public long lastMoveTime;
	public long moveDelay = 700;
	public int direction = 1;

	public NiceFairy(Level level, String name, float x, float y, int mass, String currentMessage) {
		super(level, name, x, y, mass, new Colors(-1, 005, 035, 555).getColor());
		message = currentMessage;
		messageDelay = 4000;
		lastMoveTime = System.currentTimeMillis();
		floating = true;
	}

	@Override
	public void tick() {
		super.tick();
		if (!sayMessage && System.currentTimeMillis() - lastMoveTime >= moveDelay) {
			applyForce((float) (0.3 * direction), (float) (-0.8));
			lastMoveTime = System.currentTimeMillis();
			direction *= -1;
		}

		applyForce(0, (float) 0.05);

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
}
