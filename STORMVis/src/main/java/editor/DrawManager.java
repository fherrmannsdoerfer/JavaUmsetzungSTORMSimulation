package editor;

import java.util.ArrayList;
import java.util.List;

import model.LineDataSet;
import model.TriangleDataSet;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Point2D;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;

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

    public TriangleDataSet addCurrentPointsToTriangleDataSet(TriangleDataSet s) {
        if (currentPoints.size() == 0 || currentPoints == null) return s;
        s.drawableTriangles.addAll(packCurrentPointsToTriangleList());
        s.primitives.addAll(createPrimivesFromList(s.drawableTriangles));
        return s;
    }
	
	public ArrayList<Coord3d> packCurrentPointsToLineList() {
		ArrayList<Coord3d> line = new ArrayList<Coord3d>();
		for(Point2D p : currentPoints) {
			line.add(new Coord3d(p.x * ratio, p.y * ratio, 0.f));
		}
		return line;
	}
	
	public List<Polygon> packCurrentPointsToTriangleList() {
		List<Polygon> triangles = new ArrayList<Polygon>();
		float zComponent = 1.0f; // 1nm
		for(int i = 0; i < currentPoints.size(); i++) {
			if(i < currentPoints.size()-1) {
				Point2D p1 = currentPoints.get(i);
				Point2D p2 = currentPoints.get(i+1);
				Polygon tr1 = new Polygon();
				Polygon tr2 = new Polygon();
				tr1.add(new Point(new Coord3d(p1.x * ratio, p1.y * ratio, 0)));
				tr1.add(new Point(new Coord3d(p1.x * ratio, p1.y * ratio, zComponent)));
				tr1.add(new Point(new Coord3d(p2.x * ratio, p2.y * ratio, 0)));
				
				tr2.add(new Point(new Coord3d(p2.x * ratio, p2.y * ratio, 0)));
				tr2.add(new Point(new Coord3d(p1.x * ratio, p1.y * ratio, zComponent)));
				tr2.add(new Point(new Coord3d(p2.x * ratio, p2.y * ratio, zComponent)));
				triangles.add(tr1);
				triangles.add(tr2);
			}
			else if(i == (currentPoints.size()-1)) {
				Point2D p1 = currentPoints.get(i);
				Point2D p2 = currentPoints.get(0);
				Polygon tr1 = new Polygon();
				Polygon tr2 = new Polygon();
				tr1.add(new Point(new Coord3d(p1.x * ratio, p1.y * ratio, 0)));
				tr1.add(new Point(new Coord3d(p1.x * ratio, p1.y * ratio, zComponent)));
				tr1.add(new Point(new Coord3d(p2.x * ratio, p2.y * ratio, 0)));
				
				tr2.add(new Point(new Coord3d(p2.x * ratio, p2.y * ratio, 0)));
				tr2.add(new Point(new Coord3d(p1.x * ratio, p1.y * ratio, zComponent)));
				tr2.add(new Point(new Coord3d(p2.x * ratio, p2.y * ratio, zComponent)));
				triangles.add(tr1);
				triangles.add(tr2);
			}
		}
		return triangles;
	}
	
	public List<float[][]> createPrimivesFromList(List<Polygon> list) {
		List<float[][]> prim = new ArrayList<float[][]>();
		for(Polygon p : list) {
			float[][] tr = new float[3][3];
			for(int i = 0; i < p.getPoints().size(); i++) {
				tr[i][0] = p.getPoints().get(i).xyz.x;
				tr[i][1] = p.getPoints().get(i).xyz.y;
				tr[i][2] = p.getPoints().get(i).xyz.z;
			}
			prim.add(tr);
		}
		return prim;
	}
}
