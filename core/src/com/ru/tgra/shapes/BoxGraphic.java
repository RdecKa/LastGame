package com.ru.tgra.shapes;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;

public class BoxGraphic {

	private static FloatBuffer vertexBuffer;
	private static FloatBuffer normalBuffer;
	private static FloatBuffer uvBuffer;
	private static ShortBuffer indexBuffer;
	private static int vertexPointer;
	private static int normalPointer;
	private static int uvPointer;

	public static void create(int vertexPointer, int normalPointer, int uvPointer) {
		BoxGraphic.vertexPointer = vertexPointer;
		BoxGraphic.normalPointer = normalPointer;
		BoxGraphic.uvPointer= uvPointer;

		//VERTEX ARRAY IS FILLED HERE
		float[] vertexArray = {
				-0.5f, -0.5f, -0.5f,
				-0.5f, 0.5f, -0.5f,
				0.5f, 0.5f, -0.5f,
				0.5f, -0.5f, -0.5f,
				-0.5f, -0.5f, 0.5f,
				-0.5f, 0.5f, 0.5f,
				0.5f, 0.5f, 0.5f,
				0.5f, -0.5f, 0.5f,
				-0.5f, -0.5f, -0.5f,
				0.5f, -0.5f, -0.5f,
				0.5f, -0.5f, 0.5f,
				-0.5f, -0.5f, 0.5f,
				-0.5f, 0.5f, -0.5f,
				0.5f, 0.5f, -0.5f,
				0.5f, 0.5f, 0.5f,
				-0.5f, 0.5f, 0.5f,
				-0.5f, -0.5f, -0.5f,
				-0.5f, -0.5f, 0.5f,
				-0.5f, 0.5f, 0.5f,
				-0.5f, 0.5f, -0.5f,
				0.5f, -0.5f, -0.5f,
				0.5f, -0.5f, 0.5f,
				0.5f, 0.5f, 0.5f,
				0.5f, 0.5f, -0.5f
		};

		vertexBuffer = BufferUtils.newFloatBuffer(72);
		vertexBuffer.put(vertexArray);
		vertexBuffer.rewind();

		//NORMAL ARRAY IS FILLED HERE
		float[] normalArray = {
				0.0f, 0.0f, -1.0f,
				0.0f, 0.0f, -1.0f,
				0.0f, 0.0f, -1.0f,
				0.0f, 0.0f, -1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, -1.0f, 0.0f,
				0.0f, -1.0f, 0.0f,
				0.0f, -1.0f, 0.0f,
				0.0f, -1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f
		};

		normalBuffer = BufferUtils.newFloatBuffer(72);
		normalBuffer.put(normalArray);
		normalBuffer.rewind();

		//UV TEXTURE COORD ARRAY IS FILLED HERE
		float[] uvArray = {
				1, 1,
				1, 0,
				0, 0,
				0, 1,

				0, 1,
				0, 0,
				1, 0,
				1, 1,

				0, 0,
				0, 0,
				0, 0,
				0, 0,

				0, 0,
				0, 0,
				0, 0,
				0, 0,

				0, 1,
				1, 1,
				1, 0,
				0, 0,

				1, 1,
				0, 1,
				0, 0,
				1, 0,
		};

		uvBuffer = BufferUtils.newFloatBuffer(48);
		uvBuffer.put(uvArray);
		uvBuffer.rewind();

		//INDEX ARRAY IS FILLED HERE
		short[] indexArray = {
				0, 1, 2, 0, 2, 3,
				4, 5, 6, 4, 6, 7,
				8, 9, 10, 8, 10, 11,
				12, 13, 14, 12, 14, 15,
				16, 17, 18, 16, 18, 19,
				20, 21, 22, 20, 22, 23
		};

		indexBuffer = BufferUtils.newShortBuffer(36);
		indexBuffer.put(indexArray);
		indexBuffer.rewind();
	}

	public static void drawSolidCube() {
		Gdx.gl.glVertexAttribPointer(vertexPointer, 3, GL20.GL_FLOAT, false, 0, vertexBuffer);
		Gdx.gl.glVertexAttribPointer(normalPointer, 3, GL20.GL_FLOAT, false, 0, normalBuffer);
		Gdx.gl.glVertexAttribPointer(uvPointer, 2, GL20.GL_FLOAT, false, 0, uvBuffer);

		Gdx.gl.glDrawElements(GL20.GL_TRIANGLES, 36, GL20.GL_UNSIGNED_SHORT, indexBuffer);
	}

	public static void drawOutlineCube() {

		Gdx.gl.glVertexAttribPointer(vertexPointer, 3, GL20.GL_FLOAT, false, 0, vertexBuffer);
		Gdx.gl.glVertexAttribPointer(normalPointer, 3, GL20.GL_FLOAT, false, 0, normalBuffer);
		
		Gdx.gl.glDrawArrays(GL20.GL_LINE_LOOP, 0, 4);
		Gdx.gl.glDrawArrays(GL20.GL_LINE_LOOP, 4, 4);
		Gdx.gl.glDrawArrays(GL20.GL_LINE_LOOP, 8, 4);
		Gdx.gl.glDrawArrays(GL20.GL_LINE_LOOP, 12, 4);
		Gdx.gl.glDrawArrays(GL20.GL_LINE_LOOP, 16, 4);
		Gdx.gl.glDrawArrays(GL20.GL_LINE_LOOP, 20, 4);
	}

}
