package com.ru.tgra.network;

import com.ru.tgra.shapes.*;

public class PackageState {
	private static String delimiter = "kkk";
	private static String delimArg = "jjj";
	private String type;
	private Point3D playerPosition;
	private Vector3D playerDirection;
	private Wall wall;

	public PackageState(Point3D playerPosition, Vector3D playerDirection) {
		this.playerPosition = playerPosition;
		this.playerDirection = playerDirection;
		this.type = "position";
	}

	public PackageState(Wall wall) {
		this.wall = wall;
		this.type = "newwall";
	}

	public PackageState() {
		this.type = "defeat";
	}

	public String getType() { return type; }

	public Point3D getPlayerPosition() {
		return playerPosition;
	}

	public Vector3D getPlayerDirection() {
		return playerDirection;
	}

	public Wall getWall() { return wall; }

	public String toStringToSend() {
		if (this.type.equals("position"))
			return this.type + delimiter + this.playerPosition.toStringToSend(delimArg) + delimiter + this.playerDirection.toStringToSend(delimArg);
		else if (this.type.equals("newwall"))
			return this.type + delimiter + this.wall.toStringToSend(delimArg);
		else if (this.type.equals("defeat"))
			return this.type;
		else
			return null;
	}

	public static PackageState stringToPackage(String str) {
		String[] components = str.split(delimiter);
		if (components[0].equals("position"))
			return new PackageState(Point3D.stringToPoint(components[1], delimArg), Vector3D.stringToVector(components[2], delimArg));
		else if (components[0].equals("newwall"))
			return new PackageState(Wall.stringToWall(components[1], delimArg));
		else if (components[0].equals("defeat"))
			return new PackageState();
		else
			return null;
	}

	public void reflectView() {
		if (this.type.equals("position")) {
			float playerX = this.playerPosition.x;
			float playerZ = this.playerPosition.z;
			this.playerPosition.x = playerZ;
			this.playerPosition.z = playerX;
			float directionX = this.playerDirection.x;
			float directionZ = this.playerDirection.z;
			this.playerDirection.x = directionZ;
			this.playerDirection.z = directionX;
		} else if (this.type.equals("newwall")) {
			this.wall.reflect();
		}
	}
}
