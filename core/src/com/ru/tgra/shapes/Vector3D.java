package com.ru.tgra.shapes;

public class Vector3D {

	public float x;
	public float y;
	public float z;

	public Vector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vector3D returnScaled(float S) {
		Vector3D v = this.clone();
		v.scale(S);
		return v;
	}

	public void scale(float S) {
		x *= S;
		y *= S;
		z *= S;
	}

	public void add(Vector3D v2) {
		x += v2.x;
		y += v2.y;
		z += v2.z;
	}

	public Vector3D returnAdded(Vector3D vector) {
		Vector3D v = this.clone();
		v.add(vector);
		return v;
	}

	public float dot(Vector3D v2)
	{
		return x*v2.x + y*v2.y + z*v2.z;
	}

	public float dotSelf()
	{
		return x*x + y*y + z*z;
	}

	public float length()
	{
		return (float)Math.sqrt(dotSelf());
	}

	public void normalize() {
		float len = length();
		x = x / len;
		y = y / len;
		z = z / len;
	}

	public Vector3D returnNormalized() {
		Vector3D v = this.clone();
		v.normalize();
		return v;
	}

	public Vector3D cross(Vector3D v2)
	{
		return new Vector3D(y*v2.z - z*v2.y, z*v2.x - x*v2.z, x*v2.y - y*v2.x);
	}

	public static Vector3D difference(Point3D P2, Point3D P1)
	{
		return new Vector3D(P2.x-P1.x, P2.y-P1.y, P2.z-P1.z);
	}

	public Vector3D clone() {
		return new Vector3D(x, y, z);
	}

	public void rotateXZ(float angleDegrees) {
		float angle = (float) Math.toRadians(angleDegrees);
		float ca = (float) Math.cos(angle);
		float sa = (float) Math.sin(angle);
		float newX = ca * x - sa * z;
		float newZ = sa * x + ca * z;
		x = newX;
		z = newZ;
	}

	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}
}
