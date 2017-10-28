package com.ru.tgra.shapes;

public class PointsIndicator {
	private int numPoints, opponentPoints;
	private Color playerColor, opponentColor;

	public PointsIndicator(Color playerColor, Color opponentColor) {
		this.numPoints = 1;
		this.opponentPoints = 1;
		this.playerColor = playerColor;
		this.opponentColor = opponentColor;
	}

	public void addPoint(boolean myPoint) {
		if (myPoint) {
			this.numPoints ++;
		} else {
			this.opponentPoints ++;
		}
	}

	public void draw(Shader3D shader) {
		float ratio = (float) (this.numPoints) / (float) (this.numPoints + this.opponentPoints);
		float playerCenter = -0.5f + 0.5f * ratio;
		float opponentCenter = 0.5f - 0.5f * (1 - ratio);

		shader.setMaterialDiffuse(this.playerColor);
		ModelMatrix.main.loadIdentityMatrix();
		ModelMatrix.main.addTranslation(playerCenter, 0, 0);
		ModelMatrix.main.addScale(ratio, 1, 1);
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		BoxGraphic.drawSolidCube();

		shader.setMaterialDiffuse(this.opponentColor);
		ModelMatrix.main.loadIdentityMatrix();
		ModelMatrix.main.addTranslation(opponentCenter, 0, 0);
		ModelMatrix.main.addScale(1.0f - ratio, 1, 1);
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		BoxGraphic.drawSolidCube();
	}
}
