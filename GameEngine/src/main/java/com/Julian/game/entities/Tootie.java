package com.Julian.game.entities;

import com.Julian.game.Game;
import com.Julian.game.gfx.Colors;
import com.Julian.game.gfx.Screen;
import com.Julian.game.level.Level;
import com.Julian.game.level.tiles.AnimatedTile;
import com.Julian.game.level.tiles.BasicTile;
import com.Julian.game.level.tiles.Tile;

public class Tootie extends Mob {
	private int defaultColor = new Colors(-1, 201, 423, 543).getColor();
	private int negativeColor = new Colors(-1, -1, -1, -1).getColor();
	private int currentColor = defaultColor;
	private int orientation = 1;

	private Tile prevLeftTile;
	private int prevLeftTileX;
	private int prevLeftTileY;
	private Tile prevRightTile;
	private int prevRightTileX;
	private int prevRightTileY;

	private long lastTimeLaunched;
	private boolean launched = false;
	private int launchDelay = 500;

	public Tootie(Level level, String name, float x, float y, int mass) {
		super(level, name, x, y, mass);
	}

	public void launchMe() {
		if (!launched) {
			prevLeftTileX = (int) (this.x + (48 * orientation)) >> 3;
			prevLeftTileY = (int) (this.y) >> 3;
			prevLeftTile = level.getTile(prevLeftTileX, prevLeftTileY);
			prevRightTileX = (int) (this.x + (56 * orientation)) >> 3;
			prevRightTileY = (int) (this.y) >> 3;
			prevRightTile = level.getTile(prevRightTileX, prevRightTileY);

			if (orientation > 0) {
				level.alterTile(prevLeftTileX, prevLeftTileY, Tile.TOOTIE_TORSO_RIGHT);
				level.alterTile(prevRightTileX, prevRightTileY, Tile.TOOTIE_HEAD_RIGHT);
			} else if (orientation < 0) {
				level.alterTile(prevLeftTileX, prevLeftTileY, Tile.TOOTIE_TORSO_LEFT);
				level.alterTile(prevRightTileX, prevRightTileY, Tile.TOOTIE_HEAD_LEFT);

			}
		}

		launched = true;
		lastTimeLaunched = System.currentTimeMillis();
		negativeColor = getTileColor(((BasicTile) prevLeftTile));
		currentColor = negativeColor;
		this.x = (prevLeftTileX << 3) + (4 * orientation);
		this.y = prevLeftTileY << 3;
	}

	public int getTileColor(BasicTile tile) {
		if (tile.tileColor.getColor1() > 0) {
			return new Colors(-1, -1, tile.tileColor.getColor1(), -1).getColor();
		} else if (tile.tileColor.getColor2() > 0) {
			return new Colors(-1, -1, tile.tileColor.getColor2(), -1).getColor();
		} else if (tile.tileColor.getColor3() > 0) {
			return new Colors(-1, -1, tile.tileColor.getColor3(), -1).getColor();
		} else if (tile.tileColor.getColor4() > 0) {
			return new Colors(-1, -1, tile.tileColor.getColor4(), -1).getColor();
		} else {
			return 0;
		}
	}

	public void callMeBack() {
		launched = false;
		level.alterTile(prevLeftTileX, prevLeftTileY, prevLeftTile);
		level.alterTile(prevRightTileX, prevRightTileY, prevRightTile);
		currentColor = defaultColor;
	}

	@Override
	public void render(Screen screen) {
		int xTile = 14;
		if (launched) {
			xTile = 16 + 2 * ((AnimatedTile) (Tile.TOOTIE_HEAD_LEFT)).currentAnimationIndex;
		}
		int yTile = 28;
		int flip = 0;

		if (orientation > -1) {
			flip = 0;
		} else {
			flip = 1;
		}

		int modifier = 8 * scale;
		int xOffset = (int) (x - modifier / 2);
		int yOffset = (int) (y - modifier / 2 - 4);

		screen.render(xOffset + (modifier * flip), yOffset + modifier, xTile + (yTile + 1) * 32, currentColor,
				flip == 1, false, scale);
		screen.render(xOffset + modifier - (modifier * flip), yOffset + modifier, (xTile + 1) + (yTile + 1) * 32,
				currentColor, flip == 1, false, scale);
	}

	@Override
	public void tick() {
		if (!launched) {
			movingDir = Game.game.player.getMovingDir();
			// Change the orientation
			// Pointing to the left: Orientation = -1
			if (movingDir == 2) {
				orientation = -1;
			}
			// Pointing to the right: Orientation = 1
			else if (movingDir == 3) {
				orientation = 1;
			}

			x = Game.game.player.x - 16 * orientation;
			y = Game.game.player.y;
		} else if (System.currentTimeMillis() - launchDelay > lastTimeLaunched) {
			callMeBack();
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
		int xMin = -7;
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
		int xMin = -7;
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

}
