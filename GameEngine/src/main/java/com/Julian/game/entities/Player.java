package com.Julian.game.entities;

import com.Julian.game.InputHandler;
import com.Julian.game.gfx.Colors;
import com.Julian.game.gfx.Screen;
import com.Julian.game.level.Level;

public class Player extends Mob {

	private InputHandler input;
	private int defaultColor = new Colors(-1, 142, 441, 544).getColor(); // Light blonde: 441, Reddish Blonde: 431
	private int playerColor = defaultColor;
	private int scale = 1;
	private boolean nearInteractive = false;

	private long lastTimeJumped = 0;
	private int jumpDelay = 750;

	public Tootie tootie;

	public Player(Level level, int x, int y, InputHandler input) {
		super(level, "Cailin", x, y, 1);
		this.input = input;
		tootie = new Tootie(level, "Tootie", x - 8, y, 1);
		level.addEntity(tootie);
	}

	public void render(Screen screen) {
		int xTile = 2;
		int yTile = 28;
		int flipTop = 0;
		int flipBottom = 0;

		// When the player is facing towards the camera the x place for getting the Tile
		// pixels increases by 2 (because the player is 2 tiles wide)
		if (movingDir == 1) {
			xTile = 2;
			flipTop = (movingDir - 1) % 2;
		} else if (movingDir > 1) {
			xTile = 4;
			flipTop = (movingDir - 1) % 2;
			flipBottom = (movingDir - 1) % 2;
		}
		if (isInMidair) {
			xTile = 8;
		}

		int modifier = 8 * scale;
		int xOffset = (int) (x - modifier / 2);
		int yOffset = (int) (y - modifier / 2 - 4);

		screen.render(xOffset + (modifier * flipTop), yOffset, xTile + yTile * 32, playerColor, flipTop == 1, false,
				scale);
		screen.render(xOffset + modifier - (modifier * flipTop), yOffset, (xTile + 1) + yTile * 32, playerColor,
				flipTop == 1, false, scale);

		screen.render(xOffset + (modifier * flipBottom), yOffset + modifier, xTile + (yTile + 1) * 32, playerColor,
				flipBottom == 1, false, scale);
		screen.render(xOffset + modifier - (modifier * flipBottom), yOffset + modifier, (xTile + 1) + (yTile + 1) * 32,
				playerColor, flipBottom == 1, false, scale);

		if (nearInteractive) {
			screen.render(xOffset - 8, yOffset, 1 + 27 * 32, new Colors(-1, 323, 455, 555).getColor(), 1);
		}

	}

	// This updates the game, it updates the internal variables and the logic of the
	// game
	public void tick() {
		int yDir = 0;
		int xDir = 0;

		if (input.up.isPressed() && !isInMidair && System.currentTimeMillis() - jumpDelay > lastTimeJumped) {
			yDir = -1;
			applyForce(0, (float) -4.5);
			lastTimeJumped = System.currentTimeMillis();
		}
		if (input.down.isPressed()) {
			yDir = 1;
		}
		if (input.left.isPressed()) {
			xDir = -1;
			if (!isInMidair) {
				applyForce((float) -0.25, 0);
			} else {
				applyForce((float) -0.04, 0);
			}
		}
		if (input.right.isPressed()) {
			xDir = 1;
			if (!isInMidair) {
				applyForce((float) 0.25, 0);
			} else {
				applyForce((float) 0.04, 0);
			}
		}
		if (input.spacebar.isPressed()) {
			tootie.launchMe();
		}

		applyForce(0, (float) 0.2);

		if (xVelocity != 0 || yVelocity != 0) {
			move(xDir, yDir);
			isMoving = true;
		} else {
			isMoving = false;
		}

		// Check if the player is near an interactive entity (NPC), if so, then pull
		// up the interacting prompt.
		Entity nearestEntity = level.getNearestEntity(this);
		if (nearestEntity != null) {
			double distance = Math.sqrt(Math.pow((nearestEntity.x - this.x), 2) + Math.pow((nearestEntity.y - this.y), 2));
			if (distance < 20 && nearestEntity.isInteractive) {
				nearInteractive = true;
				if (input.F.isPressed()) {
					((NPC) nearestEntity).sayMessage();
				}
			} else {
				nearInteractive = false;
			}
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

	public int getMovingDir() {
		return movingDir;
	}

	// Read by WebMain each frame to drive the "press F to interact" HUD via
	// WebBridge.setNearInteractive(...).
	public boolean isNearInteractive() {
		return nearInteractive;
	}
}
