package com.ru.tgra.shapes;

public class Wall {
	private float wallWidth, wallLength, wallHeight;
	private boolean parallelToX;
	private float centerX, centerZ; // Central point of the wall

	public Wall(float width, float length, boolean parallelToX, float centerX, float centerZ) {
		this.wallWidth = width;
		this.wallLength = length;
		this.wallHeight = 0;
		this.parallelToX = parallelToX;
		this.centerX = centerX;
		this.centerZ = centerZ;
	}

	public void draw(int unit, Shader3D shader) {
		ModelMatrix.main.loadIdentityMatrix();
		ModelMatrix.main.addTranslation(this.centerX * unit, (this.wallHeight / 2 + 0.05f) * unit, this.centerZ * unit);
		if (this.parallelToX) {
			ModelMatrix.main.addScale(this.wallLength * unit, this.wallHeight * unit, this.wallWidth);
		} else {
			ModelMatrix.main.addScale(this.wallWidth * unit, this.wallHeight * unit, this.wallLength);
		}
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		BoxGraphic.drawSolidCube();
	}

	// Returns true if the wall has height 1 (it cannot be higher)
	public boolean raiseWall(float raiseFor) {
		this.wallHeight += raiseFor;
		if (this.wallHeight > 1) {
			this.wallHeight = 1;
			return true;
		}
		return false;
	}

	public String toStringToSend(String delimiter) {
		return this.centerX + delimiter + this.centerZ + delimiter + this.parallelToX;
	}

	public static Wall stringToWall(String str, String delimiter) {
		String[] comp = str.split(delimiter);
		return new Wall(0.1f, 1, Boolean.parseBoolean(comp[2]), Float.parseFloat(comp[0]), Float.parseFloat(comp[1]));
	}

	public void reflect() {
		float x = this.centerX;
		this.centerX = this.centerZ;
		this.centerZ = x;
		this.parallelToX = !this.parallelToX;
	}
}