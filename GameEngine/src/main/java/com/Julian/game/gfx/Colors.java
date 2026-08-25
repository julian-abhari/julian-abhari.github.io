package com.Julian.game.gfx;

public class Colors {
	private int color1;
	private int color2;
	private int color3;
	private int color4;

	public Colors(int color1, int color2, int color3, int color4) {
		this.color1 = color1;
		this.color2 = color2;
		this.color3 = color3;
		this.color4 = color4;
	}

	public int getColor() { // color1 is darker, then it gets lighter from there
		return (getColor(color4) << 24) + (getColor(color3) << 16) + (getColor(color2) << 8) + (getColor(color1));
	}

	private int getColor(int color) {
		if (0 > color) {
			return 255;
		}
		// This is getting the red value of the color int.
		// ex: Say you give it the color "340". The 100's place is the R value,
		// the 10's place is the B value, and the 1's place is the G value.
		int r = color / 100 % 10;
		int b = color / 10 % 10;
		int g = color % 10;
		return r * 36 + b * 6 + g;
	}

	public int getColor1() {
		return color1;
	}

	public int getColor2() {
		return color2;
	}

	public int getColor3() {
		return color3;
	}

	public int getColor4() {
		return color4;
	}
}
