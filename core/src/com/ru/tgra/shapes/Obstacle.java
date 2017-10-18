package com.ru.tgra.shapes;

public class Obstacle {
	private Point3D position;
	private float radius;
	private Color color;
	private boolean growing;

	public Obstacle(Point3D position, float radius, Color color) {
		this.position = position;
		this.radius = radius;
		this.color = color;
		this.growing = true;
	}

	public void draw(Shader3D shader) {
		shader.setMaterialDiffuse(this.color);
		shader.setMaterialSpecular(new Color(1, 1, 1, 1));
		shader.setShininess(10);
		ModelMatrix.main.loadIdentityMatrix();
		ModelMatrix.main.addTranslation(this.position.x , this.position.y - 0.8f, this.position.z);
		ModelMatrix.main.addScale(this.radius, this.radius, this.radius);
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		SphereGraphic.drawSolidSphere();
	}

	public void changeSize(float deltaTime) {
		if (this.growing)
			if (this.radius > 0.3f)
				this.growing = false;
			else
				this.radius += deltaTime;
		else
			if (this.radius < 0.1f)
				this.growing = true;
			else
				this.radius -= deltaTime;
	}

	public Point3D getPosition() {
		return this.position;
	}

	public float getRadius() {
		return radius;
	}
}
