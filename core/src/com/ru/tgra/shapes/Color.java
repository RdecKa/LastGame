package com.ru.tgra.shapes;

import java.util.Random;

public class Color {
	private float red, green, blue, alpha;

	public Color(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public Color() {
		Random rand = new Random();
		this.red = rand.nextFloat();
		this.green = rand.nextFloat();
		this.blue = rand.nextFloat();
		this.alpha = rand.nextFloat();
	}

	public Color returnScaled(float s) {
		Color c = this.clone();
		c.red = c.red * s;
		c.green = c.green * s;
		c.blue = c.blue * s;
		c.alpha = c.alpha * s;
		return c;
	}

	public float getRed() { return red; }
	public float getGreen() { return green; }
	public float getBlue() { return blue; }
	public float getAlpha() { return alpha; }

	public Color clone() {
		return new Color(red, green, blue, alpha);
	}
}
