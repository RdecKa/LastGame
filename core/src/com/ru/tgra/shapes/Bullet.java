package com.ru.tgra.shapes;

public class Bullet {
	private float radius, existTime;
	private Color color;
	private BezierMotion motion;

	public Bullet(float radius, Color color, Point3D startPoint, Point3D endPoint) {
		this.radius = radius;
		this.existTime = 0;
		this.color = color;
		this.motion = new BezierMotion(startPoint, endPoint);
	}

	public boolean move(float moveFor) {
		this.existTime += moveFor;
		return this.existTime >= 1;
	}

	public Point3D getPosition() {
		return motion.getPosition(this.existTime);
	}

	public void draw(Shader3D shader) {
		Point3D position = this.getPosition();
		shader.setMaterialDiffuse(this.color);
		//shader.setMaterialSpecular(new Color(1, 1, 1, 1));
		//shader.setShininess(20);
		ModelMatrix.main.loadIdentityMatrix();
		ModelMatrix.main.addTranslation(position.x, position.y, position.z);
		ModelMatrix.main.addScale(this.radius, this.radius, this.radius);
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		SphereGraphic.drawSolidSphere();
	}
}
