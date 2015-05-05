package model;

import gui.DataTypeDetector.DataType;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Point2D;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;

public class TriangleDataSet extends DataSet implements Serializable{

	public List<Polygon> drawableTriangles = new ArrayList<Polygon>();
	public List<float[][]> primitives;
	public TriangleDataSet(ParameterSet parameterSet) {
		super(parameterSet);
        this.drawableTriangles = new ArrayList<Polygon>();
        this.primitives = new ArrayList<float[][]>();
		this.dataType = DataType.TRIANGLES;
	}

    public TriangleDataSet(ParameterSet parameterSet, String name, List<Polygon> drawableTriangles, List<float[][]> primitives) {
        super(parameterSet, name);
        this.drawableTriangles = drawableTriangles;
        this.primitives = primitives;
        this.dataType = DataType.TRIANGLES;
    }

    public TriangleDataSet(ParameterSet parameterSet, List<Polygon> drawableTriangles, List<float[][]> primitives) {
        super(parameterSet);
        this.drawableTriangles = drawableTriangles;
        this.primitives = primitives;
        this.dataType = DataType.TRIANGLES;
    }
    
    public TriangleDataSet(ParameterSet parameterSet, TriangleDataSetSerializable ser) {
		super(parameterSet);
		this.name = ser.getName();
		this.dataType = DataType.TRIANGLES;
		this.color = ser.getColor();
		for(SerializablePolygon pol : ser.data) {
			this.drawableTriangles.add(pol.toPolygon());
		}
		this.primitives = ser.primitives;
		this.antiBodyEndPoints = ser.antiBodyEndPoints;
		this.antiBodyStartPoints = ser. antiBodyStartPoints;
		this.stormData = ser.stormData;
	}

    public List<Polygon> getDrawableTriangles() {
        return drawableTriangles;
    }

    public void setDrawableTriangles(List<Polygon> drawableTriangles) {
        this.drawableTriangles = drawableTriangles;
    }

    public List<float[][]> getPrimitives() {
        return primitives;
    }

    public void setPrimitives(List<float[][]> primitives) {
        this.primitives = primitives;
    }
    
    public void logPoints(){
    	for(Polygon p : drawableTriangles) {
    		Coord3d c1 = p.get(0).xyz;
    		Coord3d c2 = p.get(1).xyz;
    		Coord3d c3 = p.get(2).xyz;
    		System.out.println("pp 3");
    		System.out.println(c1.x +" " + c1.y + " " + c1.z + "  ");
    		System.out.println(c2.x +" " + c2.y + " " + c2.z + "  ");
    		System.out.println(c3.x +" " + c3.y + " " + c3.z + "  ");
    	}
    }
    
    public void rescaleData(Float factor){
    	List<Polygon> newList = new ArrayList<Polygon>();
    	for (Polygon p: drawableTriangles){
    		Polygon pNew = new Polygon();
    		pNew.add(new Point(new Coord3d(p.get(0).xyz.x*factor, p.get(0).xyz.y*factor, p.get(0).xyz.z*factor)));
    		pNew.add(new Point(new Coord3d(p.get(1).xyz.x*factor, p.get(1).xyz.y*factor, p.get(1).xyz.z*factor)));
    		pNew.add(new Point(new Coord3d(p.get(2).xyz.x*factor, p.get(2).xyz.y*factor, p.get(2).xyz.z*factor)));
    		newList.add(pNew);
    	}
    	drawableTriangles.clear();
    	drawableTriangles = newList;
    	
    	List<float[][]> prim = new ArrayList<float[][]>();
    	primitives.clear();
    	for(Polygon p : drawableTriangles) {
	    	float[][] tr = new float[3][3];
	    	for(int i = 0; i < p.getPoints().size(); i++) {
		    	tr[i][0] = p.getPoints().get(i).xyz.x;
		    	tr[i][1] = p.getPoints().get(i).xyz.y;
		    	tr[i][2] = p.getPoints().get(i).xyz.z;
	    	}
	    	prim.add(tr);
    	}
    	primitives = prim;
    }

	public void shiftData(float shiftX, float shiftY, float shiftZ) {
		List<Polygon> newList = new ArrayList<Polygon>();
    	for (Polygon p: drawableTriangles){
    		Polygon pNew = new Polygon();
    		pNew.add(new Point(new Coord3d(p.get(0).xyz.x+shiftX, p.get(0).xyz.y+shiftY, p.get(0).xyz.z+shiftZ)));
    		pNew.add(new Point(new Coord3d(p.get(1).xyz.x+shiftX, p.get(1).xyz.y+shiftY, p.get(1).xyz.z+shiftZ)));
    		pNew.add(new Point(new Coord3d(p.get(2).xyz.x+shiftX, p.get(2).xyz.y+shiftY, p.get(2).xyz.z+shiftZ)));
    		newList.add(pNew);
    	}
    	drawableTriangles.clear();
    	drawableTriangles = newList;
    	
    	List<float[][]> prim = new ArrayList<float[][]>();
    	primitives.clear();
    	for(Polygon p : drawableTriangles) {
	    	float[][] tr = new float[3][3];
	    	for(int i = 0; i < p.getPoints().size(); i++) {
		    	tr[i][0] = p.getPoints().get(i).xyz.x;
		    	tr[i][1] = p.getPoints().get(i).xyz.y;
		    	tr[i][2] = p.getPoints().get(i).xyz.z;
	    	}
	    	prim.add(tr);
    	}
    	primitives = prim;
	}
}

