package model;

import gui.DataTypeDetector.DataType;

import org.jzy3d.maths.Coord3d;
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
}
