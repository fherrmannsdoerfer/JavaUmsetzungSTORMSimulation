package model;

import gui.DataTypeDetector.DataType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jzy3d.plot3d.primitives.Polygon;

public class TriangleDataSetSerializable extends TriangleDataSet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<SerializablePolygon> data = new ArrayList<SerializablePolygon>();
	
	/**
	 * @param parameterSet - parameters
	 * 
	 */
	public TriangleDataSetSerializable(ParameterSet parameterSet) {
		super(parameterSet);
		this.dataType = DataType.TRIANGLES;
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param parameterSet - parameters
	 * @param set - non-serializable TriangleDataSet
	 * @return serializable dataset with triangles
	 */
	public TriangleDataSetSerializable(ParameterSet parameterSet, TriangleDataSet set) {
		super(parameterSet);
		this.dataType = DataType.TRIANGLES;
		for(Polygon pol : set.drawableTriangles) {
			data.add(new SerializablePolygon(pol));
		}
		this.name = set.getName();
		this.dataType = DataType.LINES;
		this.color = set.getColor();
		this.primitives = set.primitives;
		this.antiBodyEndPoints = set.antiBodyEndPoints;
		this.antiBodyStartPoints = set.antiBodyStartPoints;
		this.stormData = set.stormData;
	}
	
}
