package model;

import gui.DataTypeDetector.DataType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;

import org.jzy3d.maths.Coord3d;

public class LineDataSet extends DataSet implements Serializable{
	
	public List<ArrayList<Coord3d>> data = new ArrayList<ArrayList<Coord3d>>();
	public Integer pointNumber;
	public Integer objectNumber;
	
	public LineDataSet(ParameterSet parameterSet) {
		super(parameterSet);
		this.dataType = DataType.LINES;
		// TODO Auto-generated constructor stub
	}
	
	public LineDataSet(ParameterSet parameterSet, LineDataSetSerializable ser) {
		super(parameterSet);
		this.name = ser.getName();
		this.dataType = DataType.LINES;
		this.color = ser.getColor();
		for(ArrayList<Point3d> list : ser.data) {
			ArrayList<Coord3d> serList = new ArrayList<Coord3d>();
			for(Point3d coord : list) {
				serList.add(new Coord3d(coord.x, coord.y, coord.z));
			}
			data.add(serList);
		}
	}

}
