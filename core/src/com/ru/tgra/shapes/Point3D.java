package com.ru.tgra.shapes;

public class Point3D {

	public float x;
	public float y;
	public float z;

	public Point3D() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public Point3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void add(Vector3D v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}

	public Point3D returnAddedVector(Vector3D vector) {
		Point3D p = this.clone();
		p.add(vector);
		return p;
	}

	public void rotateAroundPoint(Point3D center, float angleDegrees) {
		Vector3D diff = Vector3D.difference(this, center);
		diff.rotateXZ(angleDegrees);
		this.x = center.x + diff.x;
		this.z = center.z + diff.z;
	}

	public float getXZDistanceTo(Point3D p) {
		return (float) Math.sqrt(Math.pow(p.x - this.x, 2) + Math.pow(p.z - this.z, 2));
	}

	public float getDistanceTo(Point3D p) {
		return (float) Math.sqrt(Math.pow(p.x - this.x, 2) + Math.pow(p.y - this.y, 2) + Math.pow(p.z - this.z, 2));
	}

	public Point3D clone() { return new Point3D(x, y, z); }

	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}
}
