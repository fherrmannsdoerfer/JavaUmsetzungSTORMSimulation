package playground;

import gui.DataTypeDetector.DataType;

import java.util.ArrayList;
import java.util.List;

import model.DataSet;
import model.LineDataSet;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Point2D;

public class DrawManager {
	public List<Point2D> currentPoints = new ArrayList<Point2D>();
	public float ratio;
	
	public DrawManager() {
		ratio = 1.f;
	}
	
	public void addPoint(Point2D p) {
		if(currentPoints != null) {
			currentPoints.add(p);
		}
		else {
			System.out.println("Current points not allocated.");
		}
	}
	
	public void removeLastPoint() {
		if(currentPoints.size() > 0) {
			currentPoints.remove(currentPoints.size()-1);
		}
	}
	
	public LineDataSet addCurrentPointsToLineDataSet(LineDataSet s) {
		if (currentPoints.size() == 0 || currentPoints == null) return s;
		s.data.add(packCurrentPointsToLineList());
		return s;
	}
	
	public ArrayList<Coord3d> packCurrentPointsToLineList() {
		ArrayList<Coord3d> line = new ArrayList<Coord3d>();
		for(Point2D p : currentPoints) {
			line.add(new Coord3d(p.x * ratio, p.y * ratio, 0.f));
		}
		return line;
	}
}
