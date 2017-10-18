package com.ru.tgra.shapes;

import com.badlogic.gdx.utils.BufferUtils;

import java.nio.FloatBuffer;

public abstract class Camera {
	public Point3D eye;
	public Vector3D u, v, n;
	protected float left, right, top, bottom, near, far;
	FloatBuffer matrixBuffer;

	public void Look3D(Point3D ey, Point3D center, Vector3D up) {
		eye = ey;
		n = Vector3D.difference(eye, center);
		u = up.cross(n);
		n.normalize();
		u.normalize();
		v = n.cross(u);
	}

	public abstract FloatBuffer getProjectionMatrix();

	public FloatBuffer getViewMatrix() {
		Vector3D minusEye = new Vector3D(-eye.x, -eye.y, -eye.z);

		float[] pm = new float[16];

		pm[0] = u.x; pm[4] = u.y; pm[8] = u.z; pm[12] = minusEye.dot(u);
		pm[1] = v.x; pm[5] = v.y; pm[9] = v.z; pm[13] = minusEye.dot(v);
		pm[2] = n.x; pm[6] = n.y; pm[10] = n.z; pm[14] = minusEye.dot(n);
		pm[3] = 0.0f; pm[7] = 0.0f; pm[11] = 0.0f; pm[15] = 1.0f;

		matrixBuffer = BufferUtils.newFloatBuffer(16);
		matrixBuffer.put(pm);
		matrixBuffer.rewind();

		return  matrixBuffer;
	}

	public void slide(float delU, float delV, float delN) {
		eye.x += delU * u.x + delV * v.x + delN * n.x;
		eye.y += delU * u.y + delV * v.y + delN * n.y;
		eye.z += delU * u.z + delV * v.z + delN * n.z;
	}

	public void roll(float angle) {
		Vector3D[] vectors = rotate(angle, u, v);
		u = vectors[0];
		v = vectors[1];
	}

	public void yaw(float angle) {
		Vector3D[] vectors = rotate(angle, n, u);
		n = vectors[0];
		u = vectors[1];
	}

	public void pitch(float angle) {
		Vector3D[] vectors = rotate(angle, v, n);
		v = vectors[0];
		n = vectors[1];
	}

	private Vector3D[] rotate(float angle, Vector3D v1, Vector3D v2) {
		float cos = (float) Math.cos(Math.toRadians(angle));
		float sin = (float) Math.sin(Math.toRadians(angle));
		Vector3D uCos = v1.clone();
		Vector3D vCos = v2.clone();
		Vector3D uSin = v1.clone();
		Vector3D vSin = v2.clone();
		uCos.scale(cos);
		vCos.scale(cos);
		uSin.scale(-sin);
		vSin.scale(sin);

		uCos.add(vSin);
		uSin.add(vCos);

		v1 = uCos;
		v2 = uSin;

		return new Vector3D[]{v1, v2};
	}

	public Point3D getEye() {
		return eye;
	}
}
