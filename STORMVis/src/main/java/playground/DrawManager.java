package playground;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.maths.Point2D;

public class DrawManager {
	public List<Point2D> currentPoints = new ArrayList<Point2D>();
	
	public DrawManager() {
		
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
	
}
