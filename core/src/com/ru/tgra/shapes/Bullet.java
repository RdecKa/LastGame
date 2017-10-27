package com.ru.tgra.shapes;

public class Bullet {
	private float radius, existTime;
	private Color color;
	private BezierMotion motion;
	private Maze maze;

	public Bullet(float radius, Color color, Point3D startPoint, Point3D endPoint, Maze maze) {
		this.radius = radius;
		this.existTime = 0;
		this.color = color;
		this.motion = new BezierMotion(startPoint, endPoint);
		this.maze = maze;
	}

	public boolean move(float moveFor) {
		this.existTime += moveFor;
		if (this.existTime >= 1) {
			return true;
		}
		Point3D pos = this.getPosition();
		float x = pos.x;
		float z = pos.z;
		int indX = (int) x;
		int indZ = (int) z;
		// Get position inside the current cell
		x = x - indX;
		z = z - indZ;
		if (x > 0.95f && this.maze.hasEastWall(indX, indZ) ||
			x < 0.05f && this.maze.hasEastWall(indX - 1, indZ) ||
			z > 0.95f && this.maze.hasNorthWall(indX, indZ) ||
			z < 0.05f && this.maze.hasNorthWall(indX, indZ - 1)) {
			return true;
		}
		return false;
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
