package com.ru.tgra.shapes;

import java.util.Random;
import java.util.Vector;

public class Maze {
	private int mazeWidth, mazeDepth;
	private Cell[][] maze;
	private int unit;
	private Color wallColor;
	private Vector<Wall> innerWalls, innerWallsToBe;
	private static Random rand = new Random();
	private float wallWidth;
	private float goalBoxAngle;
	private float goalBoxSize;
	private Color goalColor;
	private Vector<Obstacle> obstacles;

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
		this.wallColor = new Color(0.5f, 0.5f, 0.5f, 1);
		this.innerWalls = new Vector<Wall>();
		this.innerWallsToBe = new Vector<Wall>();
		this.wallWidth = 0.1f;
		this.goalBoxAngle = 0;
		this.goalBoxSize = 0.4f;
		this.goalColor = new Color(1, 0, 0, 1);
		this.obstacles = new Vector<Obstacle>();
		for (int i = 0; i < this.mazeWidth; i++) {
			int x = rand.nextInt(this.mazeWidth);
			int y = rand.nextInt(this.mazeDepth);
			if (x == 0 && y == 0) {
				x = 1;
			} else if (x == mazeWidth - 1 && y == mazeDepth - 1) {
				y = 1;
			}
			Point3D newPosition = new Point3D( x + 0.5f, 0.8f, y + 0.5f);
			obstacles.add(new Obstacle(newPosition, rand.nextFloat() / 4f + 0.1f,
					new Color(0.5f, 0.8f, 0.7f, 1)));
		}
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
			for (Wall wall : this.innerWalls) {
				wall.draw(this.unit, shader);
			}
			for (Wall wall : this.innerWallsToBe) {
				wall.draw(this.unit, shader);
			}
		}

		// Draw goal
		shader.setMaterialDiffuse(this.goalColor);
		ModelMatrix.main.loadIdentityMatrix();
		ModelMatrix.main.addTranslation(this.unit * (this.mazeWidth - 0.5f), this.unit * 0.5f, this.unit * (this.mazeDepth - 0.5f));
		ModelMatrix.main.addScale(this.goalBoxSize, this.goalBoxSize, this.goalBoxSize);
		ModelMatrix.main.addRotationZ(this.goalBoxAngle);
		ModelMatrix.main.addRotationX(this.goalBoxAngle);
		ModelMatrix.main.addRotationY(this.goalBoxAngle);
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		BoxGraphic.drawSolidCube();

		// Draw obstacles
		for (Obstacle obst: this.obstacles) {
			obst.draw(shader);
		}
	}

	private void addRandomWall(int curX, int curZ) {
		Wall newWall = null;
		int safetyCounter = this.mazeDepth * this.mazeWidth * 10;
		int forbidden[][] = new int[7][2];
		forbidden[0][0] = this.mazeWidth - 2; forbidden[0][1] = this.mazeDepth - 1;
		forbidden[1][0] = this.mazeWidth - 1; forbidden[1][1] = this.mazeDepth - 2;
		forbidden[2][0] = curX; forbidden[2][1] = curZ;
		forbidden[3][0] = curX; forbidden[3][1] = curZ - 1;
		forbidden[4][0] = curX; forbidden[4][1] = curZ + 1;
		forbidden[5][0] = curX - 1; forbidden[5][1] = curZ;
		forbidden[6][0] = curX + 1; forbidden[6][1] = curZ;
		while (newWall == null && safetyCounter > 0) {
			int z = rand.nextInt(this.mazeDepth);
			int x = rand.nextInt(this.mazeWidth);
			if (!isAllowed(x, z, forbidden))
				continue;

			newWall = maze[z][x].addWall();
			safetyCounter--;
		}
		if (newWall != null)
			this.innerWallsToBe.add(newWall);
	}

	private boolean isAllowed(int x, int z, int[][] forbidden) {
		for (int i = 0; i < forbidden.length; i++) {
			if (x == forbidden[i][0] && z == forbidden[i][1])
				return false;
		}
		return true;
	}

	public void addRandomWalls(int n, int curX, int curZ) {
		for (int i = 0; i < n; i++)
			this.addRandomWall(curX, curZ);
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

	private Wall addNorthWall() {
		this.northWall = true;
		return new Wall(this.unit * this.wallWidth, this.unit, true,(this.posX + 0.5f) * this.unit, (this.posZ + 1) * this.unit);
	}
	private Wall addEastWall() {
		this.eastWall = true;
		return new Wall(this.unit * this.wallWidth, this.unit, false,(this.posX + 1) * this.unit, (this.posZ + 0.5f) * this.unit);
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

class Wall {
	private float wallWidth, wallLength, wallHeight;
	private boolean parallelToX;
	private float centerX, centerZ; // Central point of the wall

	public Wall(float width, float length, boolean parallelToX, float centerX, float centerZ) {
		this.wallWidth = width;
		this.wallLength = length;
		this.wallHeight = 0;
		this.parallelToX = parallelToX;
		this.centerX = centerX;
		this.centerZ = centerZ;
	}

	public void draw(int unit, Shader3D shader) {
		ModelMatrix.main.loadIdentityMatrix();
		ModelMatrix.main.addTranslation(this.centerX * unit, (this.wallHeight / 2 + 0.05f) * unit, this.centerZ * unit);
		if (this.parallelToX) {
			ModelMatrix.main.addScale(this.wallLength * unit, this.wallHeight * unit, this.wallWidth);
		} else {
			ModelMatrix.main.addScale(this.wallWidth * unit, this.wallHeight * unit, this.wallLength);
		}
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		BoxGraphic.drawSolidCube();
	}

	// Returns true if the wall has height 1 (it cannot be higher)
	public boolean raiseWall(float raiseFor) {
		this.wallHeight += raiseFor;
		if (this.wallHeight > 1) {
			this.wallHeight = 1;
			return true;
		}
		return false;
	}
}