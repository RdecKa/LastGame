package com.ru.tgra.shapes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import java.util.Random;
import java.util.Vector;

public class Maze {
	private int mazeWidth, mazeDepth;
	private Cell[][] maze;
	private int unit;
	private Color wallColor;
	private Vector<Wall> innerWalls, innerWallsToBe;
	private static Random rand = new Random(), randWall = new Random(557);
	private float wallWidth;
	private float goalBoxAngle;
	private float goalBoxSize;
	private Color goalColor;
	private Vector<Obstacle> obstacles;
	private Texture texGoal;
	private boolean increasingTransparency;

	public Maze(int mazeWidth, int mazeDepth) {
		this.mazeWidth = mazeWidth;
		this.mazeDepth = mazeDepth;
		this.maze = new Cell[mazeDepth][mazeWidth];
		this.unit = 1;
		for (int z = 0; z < mazeDepth; z++) {
			for (int x = 0; x < mazeWidth; x++) {
				this.maze[z][x] = new Cell(x, z, this.unit, z == mazeDepth - 1, x == mazeWidth - 1);
			}
		}
		this.wallColor = new Color(10f / 256, 156f / 256, 204f / 256, 1);
		this.innerWalls = new Vector<Wall>();
		this.innerWallsToBe = new Vector<Wall>();
		this.wallWidth = 0.1f;
		this.goalBoxAngle = 0;
		this.goalBoxSize = 0.4f;
		this.increasingTransparency = true;
		this.goalColor = new Color(1, 39f / 256, 96f / 256, 0.4f);
		this.obstacles = new Vector<Obstacle>();
		for (int i = 0; i < this.mazeWidth - 1; i++) {
			Point3D newPosition = new Point3D( i + 0.5f, 0, i + 0.5f);
			obstacles.add(new Obstacle(newPosition, randWall.nextFloat() / 4f + 0.1f,
					new Color(0.5f, 0.8f, 0.7f, 1)));
		}
		this.texGoal = new Texture(Gdx.files.internal("textures/drops.jpg"));
	}

	public void draw(boolean drawWalls, Shader3D shader) {
		// Draw cells
		//shader.setShininess(10);
		shader.setMaterialSpecular(new Color(0, 0, 0, 1));
		for (int z = 0; z < mazeDepth; z++) {
			ModelMatrix.main.loadIdentityMatrix();
			ModelMatrix.main.addTranslation(0.5f * this.unit, 0, (z + 0.5f) * this.unit);
			ModelMatrix.main.addScale(this.unit, this.wallWidth * this.unit, this.unit);
			for (int x = 0; x < mazeWidth; x++) {
				this.maze[z][x].draw(shader);
				ModelMatrix.main.addTranslation(1, 0, 0);
			}
		}

		if (drawWalls) {
			// Draw outer walls
			shader.setMaterialDiffuse(this.wallColor);

			for (int i = 0; i < 4; i++) {
				ModelMatrix.main.loadIdentityMatrix();
				switch (i) {
					case 0:
						// (0, 0, 0) - (0, 0, 1)
						ModelMatrix.main.addTranslation(0, 0.55f * this.unit, 0.5f * this.mazeDepth * this.unit);
						ModelMatrix.main.addScale(this.wallWidth * this.unit, this.unit, this.mazeDepth * this.unit);
						break;
					case 1:
						// (1, 0, 0) - (1, 0, 1)
						ModelMatrix.main.addTranslation(this.mazeWidth * this.unit, 0.55f * this.unit, 0.5f * this.mazeDepth * this.unit);
						ModelMatrix.main.addScale(this.wallWidth * this.unit, this.unit, this.mazeDepth * this.unit);
						break;
					case 2:
						// (0, 0, 0) - (1, 0, 0)
						ModelMatrix.main.addTranslation(0.5f * this.mazeWidth * this.unit, 0.55f * this.unit, 0);
						ModelMatrix.main.addScale(this.mazeWidth * this.unit, this.unit, this.wallWidth * this.unit);
						break;
					case 3:
						// (0, 0, 1) - (1, 0, 1)
						ModelMatrix.main.addTranslation(0.5f * this.mazeWidth * this.unit, 0.55f * this.unit, this.mazeDepth * this.unit);
						ModelMatrix.main.addScale(this.mazeWidth * this.unit, this.unit, this.wallWidth * this.unit);
						break;

				}
				shader.setModelMatrix(ModelMatrix.main.getMatrix());
				BoxGraphic.drawSolidCube();
			}

			// Draw inner walls
			Vector<Wall> allWalls = new Vector<Wall>();
			allWalls.addAll(this.innerWalls);
			allWalls.addAll(this.innerWallsToBe);
			for (Wall wall : allWalls) {
				shader.setSpecularTexture(wall.getTexture());
				wall.draw(this.unit, shader);
			}
			shader.setSpecularTexture(null);
		}

		// Draw goal

		// Draw inner star
		shader.setMaterialDiffuse(this.goalColor);
		shader.setMaterialSpecular(new Color(0.8f, 0.8f, 0.8f, 1));
		ModelMatrix.main.loadIdentityMatrix();
		ModelMatrix.main.addTranslation(this.unit * (this.mazeWidth - 0.5f), this.unit * 0.5f, this.unit * (this.mazeDepth - 0.5f));
		ModelMatrix.main.addScale(this.goalBoxSize * 0.8f, this.goalBoxSize * 0.8f, this.goalBoxSize * 0.8f);
		ModelMatrix.main.addRotationZ(this.goalBoxAngle);
		ModelMatrix.main.addRotationX(this.goalBoxAngle);
		ModelMatrix.main.addRotationY(this.goalBoxAngle);
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		shader.setDiffuseTexture(this.texGoal);
		ModelGraphics.drawSolidModel();

		// Draw outer, transparent star
		shader.setShininess(30);
		shader.setDiffuseTexture(null);
		shader.setMaterialEmission(this.goalColor);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		Gdx.gl.glEnable(GL20.GL_BLEND);

		ModelMatrix.main.loadIdentityMatrix();
		ModelMatrix.main.addTranslation(this.unit * (this.mazeWidth - 0.5f), this.unit * 0.5f, this.unit * (this.mazeDepth - 0.5f));
		ModelMatrix.main.addScale(this.goalBoxSize, this.goalBoxSize, this.goalBoxSize);
		ModelMatrix.main.addRotationZ(this.goalBoxAngle);
		ModelMatrix.main.addRotationX(this.goalBoxAngle);
		ModelMatrix.main.addRotationY(this.goalBoxAngle);
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		ModelGraphics.drawSolidModel();

		shader.setMaterialEmission(new Color(0, 0, 0, 1));
		Gdx.gl.glDisable(GL20.GL_BLEND);

		// Draw obstacles
		for (Obstacle obst: this.obstacles) {
			obst.draw(shader);
		}
	}

	private void addRandomWall(Player player, Player opponent) {
		int curX = (int) player.position.x;
		int curZ = (int) player.position.z;
		int opX = (int) opponent.position.x;
		int opZ = (int) opponent.position.z;
		Wall newWall = null;
		int safetyCounter = this.mazeDepth * this.mazeWidth * 10;
		int forbidden[][] = {
				{this.mazeWidth - 2, this.mazeDepth - 1},
				{this.mazeWidth - 1, this.mazeDepth - 2},
				{curX - 1, curZ - 1},
				{curX - 1, curZ},
				{curX - 1, curZ + 1},
				{curX, curZ - 1},
				{curX, curZ},
				{curX, curZ + 1},
				{curX + 1, curZ - 1},
				{curX + 1, curZ},
				{curX + 1, curZ + 1},
				{opX - 1, opZ - 1},
				{opX - 1, opZ},
				{opX - 1, opZ + 1},
				{opX, opZ - 1},
				{opX, opZ},
				{opX, opZ + 1},
				{opX + 1, opZ - 1},
				{opX + 1, opZ},
				{opX + 1, opZ + 1}
		};
		while (newWall == null && safetyCounter > 0) {
			int z = rand.nextInt(this.mazeDepth);
			int x = rand.nextInt(this.mazeWidth);
			if (!isAllowed(x, z, forbidden))
				continue;

			newWall = maze[z][x].addWall();
			safetyCounter--;
		}
		if (newWall != null) {
			this.innerWallsToBe.add(newWall);
			LabFirst3DGame.client.sendToServer(newWall);
		}
	}

	private boolean isAllowed(int x, int z, int[][] forbidden) {
		for (int i = 0; i < forbidden.length; i++) {
			if (x == forbidden[i][0] && z == forbidden[i][1])
				return false;
		}
		return true;
	}

	public void addRandomWalls(int n, Player player, Player opponent) {
		for (int i = 0; i < n; i++)
			this.addRandomWall(player, opponent);
	}

	public void addWall(Wall wall) {
		this.innerWallsToBe.add(wall);
		if (wall.isParallelToX())
			this.maze[wall.getZ() - 1][wall.getX()].addNorthWall();
		else
			this.maze[wall.getZ()][wall.getX() - 1].addEastWall();
	}

	public void raiseWalls(float raiseFor) {
		Vector<Wall> newFullyRaised = new Vector<Wall>();
		for (Wall wall : this.innerWallsToBe) {
			if (wall.raiseWall(raiseFor)) {
				this.innerWalls.add(wall);
				newFullyRaised.add(wall);
			}
		}
		for (Wall wall : newFullyRaised) {
			this.innerWallsToBe.remove(wall);
		}
	}

	public void changeObstacles(float deltaTime) {
		for (Obstacle obst : this.obstacles) {
			obst.changeSize(deltaTime);
		}
	}

	public Vector<Obstacle> getObstacles() {
		return this.obstacles;
	}

	public void incrementAngle(float angle) {
		this.goalBoxAngle += angle;
		this.goalBoxAngle %= 360;
	}

	public void changeTransparencyOfGoal(float delta) {
		if (this.increasingTransparency) {
			this.goalColor.changeTransparency(delta);
			if (this.goalColor.getAlpha() > 0.5f) {
				this.increasingTransparency = false;
			}
		} else {
			this.goalColor.changeTransparency(-delta);
			if (this.goalColor.getAlpha() < 0.2f) {
				this.increasingTransparency = true;
			}
		}
	}

	// Returns false when box has disappeared
	public boolean decreaseGoalSize(float sub) {
		this.goalBoxSize -= sub;
		if (this.goalBoxSize <= 0)
			return false;
		return true;
	}

	public boolean hasNorthWall(int x, int z) {
		if (x < 0 || x >= this.mazeWidth || z < 0 || z >= this.mazeDepth) {
			return true;
		}
		return this.maze[z][x].hasNorthWall();
	}

	public  boolean hasEastWall(int x, int z) {
		if (x < 0 || x >= this.mazeWidth || z < 0 || z >= this.mazeDepth) {
			return true;
		}
		return this.maze[z][x].hasEastWall();
	}

	public float getWallWidth() {
		return wallWidth;
	}
	public int getMazeDepth() { return mazeDepth; }
	public int getMazeWidth() { return mazeWidth; }
}

class Cell {
	// northWall parallel to X, eastWall parallel to Z
	private boolean northWall, eastWall;
	private Color floorColor;
	private static Random rand = new Random();
	private int posX, posZ;
	private int unit;
	private float wallWidth;
	private static Texture[] possibleTextures = {
			new Texture(Gdx.files.internal("textures/runes/day2.jpg")),
			new Texture(Gdx.files.internal("textures/runes/journey2.jpg")),
			new Texture(Gdx.files.internal("textures/runes/joy2.jpg")),
			new Texture(Gdx.files.internal("textures/runes/sun2.jpg")),
			new Texture(Gdx.files.internal("textures/runes/wealth2.jpg"))
	};

	public Cell(int x, int z, int unit, boolean northRow, boolean eastRow) {
		this.floorColor = new Color();
		this.northWall = northRow;
		this.eastWall = eastRow;
		this.posX = x;
		this.posZ = z;
		this.unit = unit;
		this.wallWidth = 0.1f;
	}

	public void draw(Shader3D shader) {
		shader.setMaterialDiffuse(this.floorColor);
		//shader.setShininess(5);
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		BoxGraphic.drawSolidCube();
	}

	public Wall addNorthWall() {
		this.northWall = true;
		return new Wall(this.unit * this.wallWidth, this.unit, true,
				(this.posX + 0.5f) * this.unit, (this.posZ + 1) * this.unit,
				(rand.nextFloat() < 0.40f ? possibleTextures[rand.nextInt(possibleTextures.length)] : null));
	}
	public Wall addEastWall() {
		this.eastWall = true;
		return new Wall(this.unit * this.wallWidth, this.unit, false,
				(this.posX + 1) * this.unit, (this.posZ + 0.5f) * this.unit,
				(rand.nextFloat() < 0.40f ? possibleTextures[rand.nextInt(possibleTextures.length)] : null));
	}

	// Returns true if wall was added, false otherwise
	public Wall addWall() {
		if (this.northWall && this.eastWall) {
			return null;
		}
		Wall newWall;
		if (!this.northWall && !this.eastWall) {
			if (rand.nextFloat() > 0.5) {
				newWall = addNorthWall();
			} else {
				newWall = addEastWall();
			}
		} else if (!this.northWall) {
			newWall = addNorthWall();
		} else {
			newWall = addEastWall();
		}
		return newWall;
	}

	public boolean hasNorthWall() {
		return northWall;
	}

	public boolean hasEastWall() {
		return eastWall;
	}
}
