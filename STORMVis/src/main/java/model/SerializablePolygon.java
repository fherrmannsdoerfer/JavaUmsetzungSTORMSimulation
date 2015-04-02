package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;

public class SerializablePolygon implements Serializable {
	public List<Point3d> points = new ArrayList<Point3d>();

	public List<Point3d> getPoints() {
		return points;
	}

	public void setPoints(List<Point3d> points) {
		this.points = points;
	}
	
	public SerializablePolygon(Polygon p) {
		for(Point point : p.getPoints()) {
			this.points.add(new Point3d(point.xyz.x, point.xyz.y, point.xyz.z));
		}
	}
	
	public Polygon toPolygon() {
		Polygon p = new Polygon();
		for(Point3d point : this.points) {
			p.add(new Point(new Coord3d(point.x,point.y,point.z)));
		}
		return p;
	}
}
