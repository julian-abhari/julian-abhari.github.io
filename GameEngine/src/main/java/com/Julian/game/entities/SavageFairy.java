package com.Julian.game.entities;

import com.Julian.game.gfx.Colors;
import com.Julian.game.level.Level;

public class SavageFairy extends Fairy {
	private long lastFlight = 0;
	private int flightDelay = 500;
	private int direction = 1;

	public SavageFairy(Level level, String name, float x, float y, int mass) {
		super(level, name, x, y, mass, new Colors(-1, 302, 502, 555).getColor());
		message = "Have you ever killed a bunny? It feels so good to rip out their happiness and bathe in their sorrow";
		messageDelay = 3500;
		lastFlight = System.currentTimeMillis();
	}

	public SavageFairy(Level level, String name, float x, float y, int mass, String message, int messageDelay) {
		super(level, name, x, y, mass, new Colors(-1, 302, 502, 555).getColor());
		this.message = message;
		this.messageDelay = messageDelay;
		lastFlight = System.currentTimeMillis();
	}

	@Override
	public void tick() {
		super.tick();
		if (!sayMessage && System.currentTimeMillis() - lastFlight >= flightDelay) {
			if (direction > 0) {
				applyForce(0, (float) 3.0);
			} else {
				applyForce(0, (float) -1.0);
			}
			direction *= -1;
			lastFlight = System.currentTimeMillis();
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
