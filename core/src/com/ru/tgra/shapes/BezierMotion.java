package com.ru.tgra.shapes;

import java.util.Random;

public class BezierMotion {
	private Point3D[] controlPoints;
	private Random rand;

	public BezierMotion(Point3D startPoint, Point3D endPoint) {
		this.rand = new Random();
		this.controlPoints = new Point3D[4];
		this.controlPoints[0] = startPoint;
		this.controlPoints[3] = endPoint;
		Point3D center = startPoint.getHalfPoint(endPoint);
		this.controlPoints[1] = startPoint.getHalfPoint(center);
		this.controlPoints[2] = center.getHalfPoint(endPoint);
		Vector3D moveAwayFirst, moveAwaySecond;
		if (rand.nextFloat() < 0.5) {
			moveAwayFirst = Vector3D.difference(startPoint, this.controlPoints[1]).cross(new Vector3D(0, 1, 0));
			moveAwaySecond = Vector3D.difference(center, this.controlPoints[2]).cross(new Vector3D(0, -1, 0));
		} else {
			moveAwayFirst = Vector3D.difference(startPoint, this.controlPoints[1]).cross(new Vector3D(0, -1, 0));
			moveAwaySecond = Vector3D.difference(center, this.controlPoints[2]).cross(new Vector3D(0, 1, 0));
		}
		this.controlPoints[1].add(moveAwayFirst);
		this.controlPoints[2].add(moveAwaySecond);
	}

	public Point3D getPosition(float t) {
		Point3D p1 = this.controlPoints[0].returnMultiplied((float) Math.pow(1 - t, 3));
		Point3D p2 = this.controlPoints[1].returnMultiplied(3 * (float) Math.pow(1 - t, 2) * t);
		Point3D p3 = this.controlPoints[2].returnMultiplied(3 * (1 - t) * (float) Math.pow(t, 2));
		Point3D p4 = this.controlPoints[3].returnMultiplied((float) Math.pow(t, 3));
		p1.addPoint(p2);
		p1.addPoint(p3);
		p1.addPoint(p4);
		return p1;
	}
}
