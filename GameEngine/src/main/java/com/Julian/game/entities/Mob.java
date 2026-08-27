package com.Julian.game.entities;

import com.Julian.game.level.Level;
import com.Julian.game.level.tiles.Tile;

public abstract class Mob extends Entity {

	protected String name;
	protected boolean isMoving;
	protected int movingDir = 1;
	protected int scale = 1;
	protected int numSteps = 0;
	public int mass;

	public float xVelocity = 0;
	public float yVelocity = 0;

	public float xAcceleration = 0;
	public float yAcceleration = 0;

	public boolean isInMidair = true;

	public Mob(Level level, String name, float x, float y, int mass) {
		super(level);
		this.name = name;
		this.x = x;
		this.y = y;
		this.mass = mass;
	}

	// xDir = How much the Mob is moving in a certain direction
	// yDir = How much the Mob is moving in a certain direction
	/*
	 * Imagine a standard cartesion coordinate plane, place positive (1) xDir in the
	 * whole positive side of xCoord and place negative (1) xDir in the whole
	 * negative side, and do the same for the yDir. Then you'll understand what
	 * these variables are (The range is an integer between -1 and 1). They're
	 * basically the equivalent of N,E,S,W
	 */
	public void move(int xDir, int yDir) {
		// This checks if they're not 0
		// They should only move in 1 direction at a time because if they move
		// diagonally they'll move 2 blocks at a time
		if (xDir != 0 && yDir != 0) {
			move(xDir, 0);
			move(0, yDir);
			numSteps -= 1;
			return;
		}
		numSteps += 1;

		int xVelDir = 0;
		int yVelDir = 0;

		if (xVelocity > 0) {
			xVelDir = 1;
		}
		if (xVelocity < 0) {
			xVelDir = -1;
		}
		if (yVelocity > 0) {
			yVelDir = 1;
		}
		if (yVelocity < 0) {
			yVelDir = -1;
		}

		if (this.name != "Fireball") {
			// Friction only damps horizontal ground movement now - applying it to
			// yVelocity too used to eat into fall/jump speed on every tick, which
			// felt wrong for a platformer (gravity shouldn't be "sticky").
			xVelocity *= level.getTile(((int) (this.x) >> 3), ((int) (this.y) >> 3)).getFriction();
		}

		if (!hasCollided((int) xVelDir, 0)) {
			// When the player is moving to the left the movingDir is set to 2
			if (xDir < 0)
				movingDir = 2;
			// When the player is moving to the right the movingDir is set to 3
			if (xDir > 0)
				movingDir = 3;

			// This is moving the players position by whatever the direction is when the
			// acceleration and velocity is added.
			x += xVelocity;
		} else {
			// No longer dampens yVelocity here - bumping into a wall sideways used to
			// cut jump momentum short, which felt bad.
			xVelocity = 0;
		}

		if (hasBounced((int) xVelDir, 0)) {
			xVelocity *= -1.5;
		}

		if (!hasCollided(0, (int) yVelDir)) {
			// When the player is going up the movingDir is set to 0
			if (yDir < 0)
				movingDir = 0;
			// When the player is going down the movingDir is set to 1
			if (yDir > 0)
				movingDir = 1;
			// This is moving the players position by whatever the direction is when the
			// acceleration and velocity is added.
			y += yVelocity;
			isInMidair = true;
		} else {
			isInMidair = false;
			yVelocity = 0;
			xVelocity *= 0.9;
		}

		if (hasBounced(0, (int) yVelDir)) {
			yVelocity *= -1.5;
			xVelocity *= 1.5;
		}
	}

	public abstract boolean hasCollided(float xAmount, float yAmount);

	public abstract boolean hasBounced(float xAmount, float yAmount);

	// This is going to get the last tile that the player was standing on and the
	// current tile and compare them, if the tile hasn't changed then nothing will
	// happen, but if it has changed and the tile is solid then it will return true.
	protected boolean isSolidTile(int xAmount, int yAmount, int x, int y) {
		if (level == null) {
			return false;
		}

		Tile lastTile = level.getTile(((int) (this.x + x)) >> 3, ((int) (this.y + y)) >> 3);
		Tile newTile = level.getTile(((int) (this.x + x + xAmount)) / 8, ((int) (this.y + y + yAmount)) / 8);
		// If they have collided with a solid tile
		if (!lastTile.equals(newTile) && newTile.isSolid()) {
			// If they are moving diagnally into a solid tile
			if (yAmount > 0 && xAmount > 0) {
				isSolidTile(xAmount, 0, x, 0);
				isSolidTile(0, yAmount, 0, y);
			} else if (yAmount > 0 && xAmount < 0) {
				isSolidTile(xAmount, 0, x, 0);
				isSolidTile(0, yAmount, 0, y);
			} else if (yAmount < 0 && xAmount < 0) {
				isSolidTile(xAmount, 0, x, 0);
				isSolidTile(0, yAmount, 0, y);
			} else if (yAmount < 0 && xAmount > 0) {
				isSolidTile(xAmount, 0, x, 0);
				isSolidTile(0, yAmount, 0, y);
			}
			// If they are not moving diagnally and have collided into the wall
			else {
				if (yAmount != 0) {
					// The difference between the yMax (the max y from the collision) and the y
					// (current y value being intercepted) gives the amount to offset by. The
					// direction of offset is the yAmount's sign.
					this.y += (y - (yAmount * 7));
				}
				if (xAmount < 0) {
					this.x += x;
				}
				if (xAmount > 0) {
					this.x -= (-x + 7);
				}
			}

			return true;
		}
		return false;
	}

	// This is going to get the last tile that the player was standing on and the
	// current tile and compare them, if the tile hasn't changed then nothing will
	// happen, but if it has changed and the tile is bouncy then it will return
	// true.
	protected boolean isBouncyTile(int xAmount, int yAmount, int x, int y) {
		if (level == null) {
			return false;
		}

		Tile lastTile = level.getTile(((int) (this.x + x)) >> 3, ((int) (this.y + y)) >> 3);
		Tile newTile = level.getTile(((int) (this.x + x + xAmount)) / 8, ((int) (this.y + y + yAmount)) / 8);
		// If they have collided with a solid tile
		if (!lastTile.equals(newTile) && newTile.isBouncy()) {
			// If they are moving diagnally into a solid tile
			if (yAmount > 0 && xAmount > 0) {
				isBouncyTile(xAmount, 0, x, 0);
				isBouncyTile(0, yAmount, 0, y);
			} else if (yAmount > 0 && xAmount < 0) {
				isBouncyTile(xAmount, 0, x, 0);
				isBouncyTile(0, yAmount, 0, y);
			} else if (yAmount < 0 && xAmount < 0) {
				isBouncyTile(xAmount, 0, x, 0);
				isBouncyTile(0, yAmount, 0, y);
			} else if (yAmount < 0 && xAmount > 0) {
				isBouncyTile(xAmount, 0, x, 0);
				isBouncyTile(0, yAmount, 0, y);
			}
			// If they are not moving diagnally and have collided into the wall
			else {
				if (yAmount != 0) {
					// The difference between the yMax (the max y from the collision) and the y
					// (current y value being intercepted) gives the amount to offset by. The
					// direction of offset is the yAmount's sign.
					this.y += (y - (yAmount * 7));
				}
				if (xAmount < 0) {
					this.x += x;
				}
				if (xAmount > 0) {
					this.x -= (-x + 7);
				}
			}

			return true;
		}
		return false;
	}

	public String getName() {
		return name;
	}

}
