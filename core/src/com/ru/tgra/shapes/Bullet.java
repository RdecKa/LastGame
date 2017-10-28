package com.ru.tgra.shapes;

import java.util.Vector;

public class Bullet {
	private float radius, existTime;
	private Color color;
	private BezierMotion motion;
	private Maze maze;
	private Point3D position;

	public Bullet(float radius, Color color, Point3D startPoint, Point3D endPoint, Maze maze) {
		this.radius = radius;
		this.existTime = 0;
		this.color = color;
		this.motion = new BezierMotion(startPoint, endPoint);
		this.maze = maze;
		this.position = null;
	}

	public Bullet(Point3D position) {
		this.radius = 0.05f;
		this.color = new Color(0.5f, 0.5f, 0.5f, 1);
		this.position = position;
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

		// Check collisions with walls
		if (x > 0.95f && this.maze.hasEastWall(indX, indZ) ||
			x < 0.05f && this.maze.hasEastWall(indX - 1, indZ) ||
			z > 0.95f && this.maze.hasNorthWall(indX, indZ) ||
			z < 0.05f && this.maze.hasNorthWall(indX, indZ - 1)) {
			return true;
		}

		// Check collisions with balls
		Vector<Obstacle> obstacles = maze.getObstacles();
		for (Obstacle obst : obstacles) {
			float diff = obst.getPosition().getDistanceTo(pos) - obst.getRadius() - this.radius;
			if (diff < -this.radius) {
				return true;
			}
		}
		return false;
	}

	public Point3D getPosition() {
		if (this.motion != null)
			return this.motion.getPosition(this.existTime);
		else
			return this.position;
	}

	public float getRadius() {
		return this.radius;
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
