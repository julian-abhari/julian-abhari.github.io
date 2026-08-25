package com.Julian.game.entities;

import com.Julian.game.gfx.Colors;
import com.Julian.game.gfx.Font;
import com.Julian.game.gfx.Screen;
import com.Julian.game.level.Level;

public abstract class NPC extends Mob {
	protected boolean sayMessage = false;
	protected long lastTimeSaidMessage = 0;
	protected int messageDelay = 2000;
	protected int wordsPerLine = 3;
	protected String message = "";

	public NPC(Level level, String name, float x, float y, int mass) {
		super(level, name, x, y, mass);
		isInteractive = true;
	}

	public void render(Screen screen) {
		int modifier = 8 * scale;
		int xOffset = (int) (x - modifier / 2);
		int yOffset = (int) (y - modifier / 2 - 4);

		if (sayMessage) {
			String[] words = message.split(" ");
			String message = "";
			for (int i = 0; i < words.length; i += 1) {
				message += words[i] + " ";
				if (i % wordsPerLine == 0) {
					Font.render(message, screen, xOffset - ((message.length() / 2) * 8), yOffset - (14 + 3 * (words.length - i)),
							new Colors(000, -1, -1, 555).getColor(), 1);
					message = "";
				}
				if (i + 1 == words.length) {
					Font.render(message, screen, xOffset - ((message.length() / 2) * 8), yOffset - 11,
							new Colors(000, -1, -1, 555).getColor(), 1);
					message = "";
				}
			}
		}
	}

	public void tick() {
		if (lastTimeSaidMessage + messageDelay < System.currentTimeMillis()) {
			sayMessage = false;
		}
	}

	public void sayMessage() {
		sayMessage = true;
		lastTimeSaidMessage = System.currentTimeMillis();
	}

}
