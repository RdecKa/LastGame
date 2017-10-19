package com.ru.tgra.network;

import com.ru.tgra.shapes.*;

public class PackageState {
	private Point3D playerPosition;
	private Vector3D playerDirection;
	private static String delimiter = "kkk";
	private static String delimArg = "jjj";

	public PackageState(Point3D playerPosition, Vector3D playerDirection) {
		this.playerPosition = playerPosition;
		this.playerDirection = playerDirection;
	}

	public Point3D getPlayerPosition() {
		return playerPosition;
	}

	public Vector3D getPlayerDirection() {
		return playerDirection;
	}

	public String toStringToSend() {
		return this.playerPosition.toStringToSend(delimArg) + delimiter + this.playerDirection.toStringToSend(delimArg);
	}

	public static PackageState stringToPackage(String str) {
		String[] components = str.split(delimiter);
		return new PackageState(Point3D.stringToPoint(components[0], delimArg), Vector3D.stringToVector(components[1], delimArg));
	}
}
