package com.ru.tgra.shapes;


import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.ru.tgra.network.*;

import java.util.Random;
import java.util.Vector;

public class LabFirst3DGame extends ApplicationAdapter implements InputProcessor {
	OrtographicCamera ortCamera;
	PerspectiveCamera perspCamera;
	Shader3D shader;
	Point3D lightPos1;
	Color lightCol1;

	Maze maze;
	int mazeWidth, mazeDepth;
	Point3D mapCenter;

	Vector3D moveLeft, moveForward, up, lookDown, moveFor;

	boolean firstPersonView;
	float fovProjection;

	Player player, thirdPerson, opponent;
	Color playerColor, opponentColor;

	boolean win, winAnimation, defeat, killed, killAnimation, victory, victoryAnimation;
	public static int level, numWallsAtOnce;

	Random rand;

	public static GameClient client;

	PointsIndicator pointsInd;
	private Vector<Bullet> bullets = new Vector<Bullet>();
	private Bullet opponentBullet;

	@Override
	public void create () {
		shader = new Shader3D();
		Gdx.input.setInputProcessor(this);
		GameEnv.init(shader);

		try {
			client = new GameClient();
		} catch (Exception e) {
			System.err.println("Cannot establish a network connection");
		}

		playerColor = new Color(0.8f, 0.8f, 0.2f, 1);
		opponentColor = new Color(0.3f, 0.9f, 0.3f, 1);
		pointsInd = new PointsIndicator(playerColor, opponentColor);

		rand = new Random();

		level = 1;
		initLevel(level);
	}

	private void initLevel(int level) {
		switch (level) {
			case 1: {
				mazeWidth = 5;
				break;
			}
			case 2: {
				mazeWidth = 7;
				break;
			}
			case 3: {
				mazeWidth = 10;
				break;
			}
			default: {
				mazeWidth = 13;
			}
		}
		mazeDepth = mazeWidth;
		numWallsAtOnce = 1;

		maze = new Maze(mazeWidth, mazeDepth);

		player = new Player(new Point3D(mazeWidth - 0.5f, 0, 0.5f), new Vector3D(-1, 0, 1), playerColor);

		win = false;
		winAnimation = false;
		killed = false;
		killAnimation = false;
		victory = false;
		victoryAnimation = false;

		lookDown = new Vector3D(0, -0.5f, 0);
		moveLeft = new Vector3D(1, 0, 0);
		moveForward = new Vector3D(0, 0, 1);
		up = new Vector3D(0,1,0);
		moveFor = new Vector3D(0, 0, 0);

		firstPersonView = true;

		perspCamera = new PerspectiveCamera();
		fovProjection = 130;

		Point3D mapCameraEye = new Point3D((float)(mazeWidth/2.0), 10, (float)(mazeDepth/2.0));
		mapCenter = mapCameraEye.clone().returnAddedVector(new Vector3D(0, -5, 0));
		Point3D mapCameraCenter = mapCenter;
		ortCamera = new OrtographicCamera();
		ortCamera.Look3D(new Point3D(0, 0, -1), new Point3D(0, 0, 0), new Vector3D(0,1, 0));

		thirdPerson = ThirdPerson.createThirdPerson(mapCameraCenter.returnAddedVector(new Vector3D(-10, 10, 0)), mapCameraCenter);

		lightPos1 = new Point3D(mazeWidth - 0.5f, 3f, mazeDepth - 0.5f);
		lightCol1 = new Color(0.5f, 0.5f, 0.5f, 1);

		opponentBullet = null;
	}

	private void input(float deltaTime)
	{
		if (firstPersonView && !winAnimation && !victoryAnimation && !killAnimation) {
			if (opponent != null) {
				moveFor = new Vector3D(0, 0, 0);
				if (Gdx.input.isKeyPressed(Input.Keys.A)) {
					moveFor.add(moveLeft);
				}
				if (Gdx.input.isKeyPressed(Input.Keys.D)) {
					moveFor.add(moveLeft.returnScaled(-1));
				}
				if (Gdx.input.isKeyPressed(Input.Keys.W)) {
					moveFor.add(moveForward);
				}
				if (Gdx.input.isKeyPressed(Input.Keys.S)) {
					moveFor.add(moveForward.returnScaled(-1));
				}
				moveFor.scale(deltaTime);
			}
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				player.direction.rotateXZ(-100 * deltaTime);
			}
			if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				player.direction.rotateXZ(100 * deltaTime);
			}
			if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
				lookDown.y += deltaTime * 2;
				if (lookDown.y > 0.2f) {
					lookDown.y = 0.2f;
				}
			}
			if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
				lookDown.y -= deltaTime * 2;
				if (lookDown.y < -4) {
					lookDown.y = -4;
				}
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
				if (this.bullets.size() < 1)
					bullets.add(new Bullet(0.04f, new Color(0.5f, 0.5f, 0.5f, 1), player.position.returnAddedVector(new Vector3D(0, 0.4f, 0)), player.getAim(lookDown), maze));
			}
		} else if (!firstPersonView) {
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				thirdPerson.position.rotateAroundPoint(mapCenter, 100 * deltaTime);
			}
			if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				thirdPerson.position.rotateAroundPoint(mapCenter, -100 * deltaTime);
			}
			if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
				fovProjection -= 100 * deltaTime;
				if (fovProjection < 10)
					fovProjection = 10;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
				fovProjection += 100 * deltaTime;
				if (fovProjection > 130)
					fovProjection = 130;
			}
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
			firstPersonView = !firstPersonView;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
			killAnimation = true;
			killed = true;
			pointsInd.addPoint(false);
			client.announceVictory(false);
		}
	}
	
	private void update(float deltaTime)
	{
		if (win && winAnimation) {
			winAnimation = maze.decreaseGoalSize(deltaTime / 2);
			maze.incrementAngle(deltaTime * 50);
			return;
		} else if (win) {
			pointsInd.addPoint(true);
			initLevel(++level);
			client.announceNewStart();
		} else if (victory && victoryAnimation) {
			victoryAnimation = player.rotateVictory(deltaTime * 400);
		} else if (victory) {
			client.announceNewStart();
			initLevel(level);
		} else if (killed && killAnimation) {
			lookDown.y -= deltaTime;
			if (client.isNewStart())
				initLevel(level);
		} else if (killed) {
			pointsInd.addPoint(false);
			initLevel(level);
		}

		shader.setShininess(5);

		if (firstPersonView) {
			if (!victoryAnimation) {
				win = player.move(moveFor, maze, opponent);
				if (win && !winAnimation && !killAnimation)
					client.announceVictory(true);
				winAnimation = win;
				fovProjection = 60;
				moveForward = player.direction;
				moveLeft = up.cross(moveForward);
			}
			Point3D playerImaginaryPosition = player.position.returnAddedVector(new Vector3D(0, 0.8f, 0));
			Point3D center = playerImaginaryPosition.returnAddedVector(player.direction).returnAddedVector(lookDown);
			perspCamera.Look3D(playerImaginaryPosition, center, up);
		} else {
			player.move(new Vector3D(0, 0, 0), maze, opponent);
			Point3D mapCenter = new Point3D(mazeWidth / 2, 1, mazeDepth / 2);
			perspCamera.Look3D(thirdPerson.position, mapCenter, new Vector3D(0, 1, 0));
		}
		maze.raiseWalls(deltaTime);
		maze.incrementAngle(deltaTime * 50);
		maze.changeTransparencyOfGoal(deltaTime / 17 * 5);
		if (opponent != null)
			maze.changeObstacles(deltaTime / 30.0f);

		Vector<Bullet> bulletsToBeRemoved = new Vector<Bullet>();
		for (Bullet bullet: this.bullets) {
			boolean end = bullet.move(deltaTime / 1.5f);
			if (end)
				bulletsToBeRemoved.add(bullet);
		}
		this.bullets.removeAll(bulletsToBeRemoved);

		if (victoryAnimation || winAnimation || killAnimation) {
			return;
		}

		/**************** Server communication ****************/
		client.sendToServer(player.position, player.direction);
		if (this.bullets.size() > 0) {
			client.sendToServer(this.bullets.firstElement().getPosition());
		}

		PackageState p = client.getLastPackageState();
		if (p != null) {
			if (opponent == null) {
				opponent = new Player(p.getPlayerPosition(), p.getPlayerDirection(), opponentColor);
			} else {
				opponent.position = p.getPlayerPosition();
				opponent.direction = p.getPlayerDirection();
			}
		}
		PackageState pWall = client.getWallPackage();
		if (pWall != null) {
			this.maze.addWall(pWall.getWall());
		}
		PackageState pDefeat = client.getDefeat();
		if (pDefeat != null) {
			if (pDefeat.isDefeated()) {
				pointsInd.addPoint(false);
				level++;
				killed = true;
				killAnimation = true;
			} else {
				pointsInd.addPoint(true);
				victoryAnimation = true;
				victory = true;
			}
			defeat = true;
			return;
		}
		PackageState pBullet = client.getBullet();
		if (pBullet != null && !killed) {
			opponentBullet = new Bullet(pBullet.getBulletPosition());
			float diff = opponentBullet.getPosition().getDistanceTo(player.position) - opponentBullet.getRadius() - player.getRadius();
			if (diff < 0) {
				killed = true;
				killAnimation = true;
				pointsInd.addPoint(false);
				client.announceVictory(false);
			}
		}
		else
			opponentBullet = null;
	}
	
	private void display()
	{
		if (win && !winAnimation || victory && !victoryAnimation || defeat || killed && !killAnimation) {
			defeat = false;
			return;
		}

		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// Set lights and cameras
		shader.setLightDirection(player.direction.returnScaled(-1));
		shader.setLightDirColor(new Color(0.4f, 0.4f, 0.4f, 1));

		shader.setLightPosition(lightPos1);
		shader.setLightPosColor(new Color(1, 0.3f, 0.3f, 1));

		perspCamera.setPerspectiveProjection(fovProjection, 1, 0.1f, 30);
		shader.setViewMatrix(perspCamera.getViewMatrix());
		shader.setProjectionMatrix(perspCamera.getProjectionMatrix());
		shader.setEyePosition(perspCamera.getEye());

		shader.setGlobalAmbient(new Color(0.3f, 0.3f, 0.3f, 1));
		if (firstPersonView)
			shader.setFog(new Color(0, 0, 0, 1), 2, 7);
		else
			shader.setFog(new Color(0.5f, 0.5f, 0.5f, 1), 10, 100);

		maze.draw(true, shader);

		player.draw(shader, !firstPersonView);

		if (opponent != null)
			opponent.draw(shader, true);

		for (Bullet bullet: this.bullets) {
			bullet.draw(shader);
		}

		if (opponentBullet != null) {
			opponentBullet.draw(shader);
		}

		// Draw indicator
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		int mapWidth = screenWidth;
		int mapHeight = screenHeight / 30;
		int margin = 10;
		Gdx.gl20.glViewport(0, screenHeight - mapHeight - margin, mapWidth, mapHeight);

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

		shader.setLightDirColor(new Color(0, 0, 0, 1));
		shader.setLightPosColor(new Color(0, 0, 0, 1));

		ortCamera.setOrtographicProjection(-0.6f, 0.6f, -1, 1, 0.1f, 10);
		shader.setViewMatrix(ortCamera.getViewMatrix());
		shader.setProjectionMatrix(ortCamera.getProjectionMatrix());
		shader.setEyePosition(ortCamera.getEye());

		shader.setGlobalAmbient(new Color(1, 1, 1, 1));

		pointsInd.draw(shader);
	}

	@Override
	public void render () {

		float deltaTime = Gdx.graphics.getDeltaTime();
		
		input(deltaTime);
		//put the code inside the update and display methods, depending on the nature of the code
		update(deltaTime);
		display();

	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}


}
