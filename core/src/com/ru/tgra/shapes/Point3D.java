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

	public Point3D getHalfPoint(Point3D p) {
		float x = (this.x + p.x) / 2;
		float y = (this.y + p.y) / 2;
		float z = (this.z + p.z) / 2;
		return new Point3D(x, y, z);
	}

	public void addPoint(Point3D p) {
		this.x += p.x;
		this.y += p.y;
		this.z += p.z;
	}

	public void multiply(float f) {
		this.x *= f;
		this.y *= f;
		this.z *= f;
	}

	public Point3D returnMultiplied(float f) {
		Point3D p = this.clone();
		p.multiply(f);
		return p;
	}

	public Point3D clone() { return new Point3D(x, y, z); }

	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}

	public String toStringToSend(String delimiter) {
		return this.x + delimiter + this.y + delimiter + this.z;
	}

	public static Point3D stringToPoint(String str, String delimiter) {
		String[] comp = str.split(delimiter);
		return new Point3D(Float.parseFloat(comp[0]), Float.parseFloat(comp[1]), Float.parseFloat(comp[2]));
	}
}
