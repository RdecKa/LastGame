package com.ru.tgra.shapes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class ModelGraphics {
	private static FloatBuffer vertexBuffer;
	private static FloatBuffer normalBuffer;
	private static FloatBuffer uvBuffer;
	private static ShortBuffer indexBuffer;
	private static int vertexPointer;
	private static int normalPointer;
	private static int uvPointer;

	public static void create(int vertexPointer, int normalPointer, int uvPointer) {
		ModelGraphics.vertexPointer = vertexPointer;
		ModelGraphics.normalPointer = normalPointer;
		ModelGraphics.uvPointer = uvPointer;

		//VERTEX ARRAY IS FILLED HERE
		float[] v_center_top = {0, 1, 0};
		float[] v_center_bottom = {0, -1, 0};
		float[] v_center_right = {1, 0, 0};
		float[] v_center_left = {-1, 0, 0};
		float[] v_center_front = {0, 0, 1};
		float[] v_center_back = {0, 0, -1};
		float[] v_tlf = {-0.3333f, 0.3333f, 0.3333f}; // tlp = top, left, front
		float[] v_tlb = {-0.3333f, 0.3333f, -0.3333f};
		float[] v_trb = {0.3333f, 0.3333f, -0.3333f};
		float[] v_trf = {0.3333f, 0.3333f, 0.3333f};
		float[] v_brf = {0.3333f, -0.3333f, 0.3333f};
		float[] v_brb = {0.3333f, -0.3333f, -0.3333f};
		float[] v_blb = {-0.3333f, -0.3333f, -0.3333f};
		float[] v_blf = {-0.3333f, -0.3333f, 0.3333f};

		int numVert = 3 * 72;

		float[][] vertices = {
				v_tlf, v_trf, v_center_top,
				v_trf, v_trb, v_center_top,
				v_trb, v_tlb, v_center_top,
				v_tlb, v_tlf, v_center_top,

				v_blf, v_brf, v_center_bottom,
				v_brf, v_brb, v_center_bottom,
				v_brb, v_blb, v_center_bottom,
				v_blb, v_blf, v_center_bottom,

				v_tlf, v_blf, v_center_left,
				v_blf, v_blb, v_center_left,
				v_blb, v_tlb, v_center_left,
				v_tlb, v_tlf, v_center_left,

				v_trf, v_brf, v_center_right,
				v_brf, v_brb, v_center_right,
				v_brb, v_trb, v_center_right,
				v_trb, v_trf, v_center_right,

				v_tlf, v_blf, v_center_front,
				v_blf, v_brf, v_center_front,
				v_brf, v_trf, v_center_front,
				v_trf, v_tlf, v_center_front,

				v_tlb, v_blb, v_center_back,
				v_blb, v_brb, v_center_back,
				v_brb, v_trb, v_center_back,
				v_trb, v_tlb, v_center_back,
		};

		vertexBuffer = BufferUtils.newFloatBuffer(numVert);
		for (int i = 0; i < vertices.length; i++) {
			vertexBuffer.put(vertices[i],0, 3);
		}
		vertexBuffer.rewind();

		//NORMAL ARRAY IS FILLED HERE
		float[] n_tr = {2, 1, 0}; // tr = top, right
		float[] n_tl = {-2, 1, 0}; // tl = top, left
		float[] n_tf = {0, 1, 2}; // tf = top, front
		float[] n_tb = {0, 1, -2}; // th = top, back

		float[] n_hr = {2, -1, 0}; // ht = bottom (h), right
		float[] n_hl = {-2, -1, 0};
		float[] n_hf = {0, -1, 2};
		float[] n_hb = {0, -1, -2};

		float[] n_lt = {-1, 2, 0};
		float[] n_lh = {-1, -2, 0};
		float[] n_lf = {-1, 0, 2};
		float[] n_lb = {-1, 0, -2};

		float[] n_rt = {1, 2, 0};
		float[] n_rh = {1, -2, 0};
		float[] n_rf = {1, 0, 2};
		float[] n_rb = {1, 0, -2};

		float[] n_ft = {0, 2, 1};
		float[] n_fh = {0, -2, 1};
		float[] n_fl = {-2, 0, 1};
		float[] n_fr = {2, 0, 1};

		float[] n_bt = {0, 2, -1};
		float[] n_bh = {0, -2, -1};
		float[] n_bl = {-2, 0, -1};
		float[] n_br = {2, 0, -1};

		float[][] normals = {
				n_tf, n_tf, n_tf,
				n_tr, n_tr, n_tr,
				n_tb, n_tb, n_tb,
				n_tl, n_tl, n_tl,

				n_hf, n_hf, n_hf,
				n_hr, n_hr, n_hr,
				n_hb, n_hb, n_hb,
				n_hl, n_hl, n_hl,

				n_lf, n_lf, n_lf,
				n_lh, n_lh, n_lh,
				n_lb, n_lb, n_lb,
				n_lt, n_lt, n_lt,

				n_rf, n_rf, n_rf,
				n_rh, n_rh, n_rh,
				n_rb, n_rb, n_rb,
				n_rt, n_rt, n_rt,

				n_fl, n_fl, n_fl,
				n_fh, n_fh, n_fh,
				n_fr, n_fr, n_fr,
				n_ft, n_ft, n_ft,

				n_bl, n_bl, n_bl,
				n_bh, n_bh, n_bh,
				n_br, n_br, n_br,
				n_bt, n_bt, n_bt
		};

		normalBuffer = BufferUtils.newFloatBuffer(numVert);
		for (int i = 0; i < vertices.length; i++) {
			normalBuffer.put(normals[i],0, 3);
		}
		normalBuffer.rewind();

		//UV TEXTURE COORD ARRAY IS FILLED HERE
		/*float[] uvArray = {0, 0,
				0, 1,
				1, 0,
				1, 1,

				0, 0,
				0, 1,
				1, 0,
				1, 1,

				0, 0,
				0, 1,
				1, 0,
				1, 1,

				0, 0,
				0, 1,
				1, 0,
				1, 1,

				0, 0,
				0, 1,
				1, 0,
				1, 1,

				0, 0,
				0, 1,
				1, 0,
				1, 1};

		uvBuffer = BufferUtils.newFloatBuffer(48);
		uvBuffer.put(uvArray);
		uvBuffer.rewind();*/

		//INDEX ARRAY IS FILLED HERE
		short[] indexArray = new short[72];
		for (short i = 0; i < indexArray.length; i++) {
			indexArray[i] = i;
		}

		indexBuffer = BufferUtils.newShortBuffer(indexArray.length);
		indexBuffer.put(indexArray);
		indexBuffer.rewind();
	}

	public static void drawSolidModel() {
		Gdx.gl.glVertexAttribPointer(vertexPointer, 3, GL20.GL_FLOAT, false, 0, vertexBuffer);
		Gdx.gl.glVertexAttribPointer(normalPointer, 3, GL20.GL_FLOAT, false, 0, normalBuffer);
		//Gdx.gl.glVertexAttribPointer(uvPointer, 2, GL20.GL_FLOAT, false, 0, uvBuffer);

		Gdx.gl.glDrawElements(GL20.GL_TRIANGLES, 72, GL20.GL_UNSIGNED_SHORT, indexBuffer);
	}
}
