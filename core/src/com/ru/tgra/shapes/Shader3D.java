package com.ru.tgra.shapes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import java.nio.FloatBuffer;

public class Shader3D {
	public static int renderingProgramID;
	public static int vertexShaderID;
	public static int fragmentShaderID;

	public static int positionLoc;
	public static int normalLoc;
	public static int uvLoc;

	public static int modelMatrixLoc;
	public static int viewMatrixLoc;
	public static int projectionMatrixLoc;

	public static int eyePosLoc;
	public static int globalAmbient;

	public static int lightPosDirLoc;
	public static int lightPosPosLoc;
	public static int lightColDirLoc;
	public static int lightColPosLoc;
	public static int matDiffLoc;
	public static int matSpecLoc;
	public static int matShinLoc;
	public static int matEmisLoc;

	public static boolean usesDiffuseTexture;
	public static int usesDiffuseTexLoc;
	public static int diffuseTextureLoc;

	public static boolean usesSpecularTexture;
	public static int usesSpecularTexLoc;
	public static int specularTextureLoc;

	public static int fogColorLoc;
	public static int fogStartLoc;
	public static int fogEndLoc;

	public Shader3D() {
		String vertexShaderString;
		String fragmentShaderString;

		vertexShaderString = Gdx.files.internal("shaders/simple3D.vert").readString();
		fragmentShaderString =  Gdx.files.internal("shaders/simple3D.frag").readString();

		vertexShaderID = Gdx.gl.glCreateShader(GL20.GL_VERTEX_SHADER);
		fragmentShaderID = Gdx.gl.glCreateShader(GL20.GL_FRAGMENT_SHADER);

		Gdx.gl.glShaderSource(vertexShaderID, vertexShaderString);
		Gdx.gl.glShaderSource(fragmentShaderID, fragmentShaderString);

		Gdx.gl.glCompileShader(vertexShaderID);
		Gdx.gl.glCompileShader(fragmentShaderID);

		System.out.println("Vertex shader compile messages:");
		System.out.println(Gdx.gl.glGetShaderInfoLog(vertexShaderID));
		System.out.println("Fragment shader compile messages:");
		System.out.println(Gdx.gl.glGetShaderInfoLog(fragmentShaderID));

		renderingProgramID = Gdx.gl.glCreateProgram();

		Gdx.gl.glAttachShader(renderingProgramID, vertexShaderID);
		Gdx.gl.glAttachShader(renderingProgramID, fragmentShaderID);

		Gdx.gl.glLinkProgram(renderingProgramID);

		positionLoc				= Gdx.gl.glGetAttribLocation(renderingProgramID, "a_position");
		Gdx.gl.glEnableVertexAttribArray(positionLoc);

		normalLoc				= Gdx.gl.glGetAttribLocation(renderingProgramID, "a_normal");
		Gdx.gl.glEnableVertexAttribArray(normalLoc);

		uvLoc					= Gdx.gl.glGetAttribLocation(renderingProgramID, "a_uv");
		Gdx.gl.glEnableVertexAttribArray(uvLoc);

		modelMatrixLoc			= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_modelMatrix");
		viewMatrixLoc			= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_viewMatrix");
		projectionMatrixLoc		= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_projectionMatrix");

		eyePosLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_eyePosition");
		globalAmbient 			= Gdx.gl.glGetUniformLocation(renderingProgramID, "globalAmbient");

		lightPosDirLoc			= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_lightPositionDir");
		lightPosPosLoc			= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_lightPositionPos");
		lightColDirLoc			= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_lightColorDir");
		lightColPosLoc			= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_lightColorPos");
		matDiffLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_materialDiffuse");
		matSpecLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_materialSpecular");
		matShinLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_materialShininess");
		matEmisLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_materialEmission");

		usesDiffuseTexLoc		= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_usesDiffuseTexture");
		usesSpecularTexLoc		= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_usesSpecularTexture");
		diffuseTextureLoc		= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_diffuseTexture");
		specularTextureLoc		= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_specularTexture");

		fogColorLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_fogColor");
		fogStartLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_fogStart");
		fogEndLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_fogEnd");

		Gdx.gl.glUseProgram(renderingProgramID);
	}


	public void setLightPosition(Point3D p) { Gdx.gl.glUniform4f(lightPosPosLoc, p.x, p.y, p.z, 1.0f); }
	public void setLightDirection(Vector3D v)  {
		Gdx.gl.glUniform4f(lightPosDirLoc, v.x, v.y, v.z, 0.0f);
	}
	public void setEyePosition(Point3D p) {
		Gdx.gl.glUniform4f(eyePosLoc, p.x, p.y, p.z, 1.0f);
	}

	public void setGlobalAmbient(Color c) {
		Gdx.gl.glUniform4f(globalAmbient, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}

	public void setLightDirColor(Color c) {
		Gdx.gl.glUniform4f(lightColDirLoc, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}
	public void setLightPosColor(Color c) {
		Gdx.gl.glUniform4f(lightColPosLoc, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}

	public void setMaterialDiffuse(Color c) {
		Gdx.gl.glUniform4f(matDiffLoc, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}

	public void setMaterialSpecular(Color c) {
		Gdx.gl.glUniform4f(matSpecLoc, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}

	public void setMaterialEmission(Color c) {
		Gdx.gl.glUniform4f(matEmisLoc, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}

	public void setShininess(float shine) {
		Gdx.gl.glUniform1f(matShinLoc, shine);
	}

	public int getVertexPointer() {
		return positionLoc;
	}

	public int getNormalPointer() {
		return normalLoc;
	}

	public int getUVPointer() { return uvLoc; }

	public void setModelMatrix(FloatBuffer matrix) {
		Gdx.gl.glUniformMatrix4fv(modelMatrixLoc, 1, false, matrix);
	}

	public void setViewMatrix(FloatBuffer matrix) {
		Gdx.gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, matrix);
	}

	public void setProjectionMatrix(FloatBuffer matrix) {
		Gdx.gl.glUniformMatrix4fv(projectionMatrixLoc, 1, false, matrix);
	}

	public void setDiffuseTexture(Texture tex) {
		if (tex == null) {
			Gdx.gl.glUniform1f(usesDiffuseTexLoc, 0.0f);
			usesDiffuseTexture = false;
		} else {
			tex.bind(0);
			Gdx.gl.glUniform1i(diffuseTextureLoc, 0);
			Gdx.gl.glUniform1f(usesDiffuseTexLoc, 1.0f);
			usesDiffuseTexture = true;

			Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_REPEAT);
			Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_REPEAT);
		}
	}

	public void setSpecularTexture(Texture tex) {
		if (tex == null) {
			Gdx.gl.glUniform1f(usesSpecularTexLoc, 0.0f);
			usesSpecularTexture = false;
		} else {
			tex.bind(0);
			Gdx.gl.glUniform1i(specularTextureLoc, 0);
			Gdx.gl.glUniform1f(usesSpecularTexLoc, 1.0f);
			usesSpecularTexture = true;

			Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_REPEAT);
			Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_REPEAT);
		}
	}

	public void setFog(Color c, float fogStart, float fogEnd) {
		Gdx.gl.glUniform4f(fogColorLoc, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		Gdx.gl.glUniform1f(fogStartLoc, fogStart);
		Gdx.gl.glUniform1f(fogEndLoc, fogEnd);
	}
}
