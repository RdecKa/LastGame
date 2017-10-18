package com.ru.tgra.shapes;

import com.badlogic.gdx.utils.BufferUtils;

import java.nio.FloatBuffer;

public class PerspectiveCamera extends Camera {
	public FloatBuffer getProjectionMatrix() {
		float[] pm = new float[16];

		pm[0] = 2 * near / (right - left); pm[4] = 0.0f; pm[8] = (right + left) / (right - left); pm[12] = 0.0f;
		pm[1] = 0.0f; pm[5] = 2 * near / (top - bottom); pm[9] = (top + bottom) / (top - bottom); pm[13] = 0.0f;
		pm[2] = 0.0f; pm[6] = 0.0f; pm[10] = -(far + near) / (far - near); pm[14] = -2 * far * near / (far - near);
		pm[3] = 0.0f; pm[7] = 0.0f; pm[11] = -1.0f; pm[15] = 0.0f;

		matrixBuffer = BufferUtils.newFloatBuffer(16);
		matrixBuffer.put(pm);
		matrixBuffer.rewind();

		return matrixBuffer;
	}

	public void setPerspectiveProjection(float fov, float ratio, float near, float far) {
		//fov (field of view) is the angle of the camera lens
		this.top = (float) (near * Math.tan((fov / 2.0) * Math.PI / 180.0));
		this.bottom = -top;
		//ratio is the aspect ratio of the screen
		this.right = top * ratio;
		this.left = -right;
		this.near = near;
		this.far = far;
	}
}
