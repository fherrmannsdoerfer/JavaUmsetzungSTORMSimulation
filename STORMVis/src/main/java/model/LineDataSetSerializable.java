package model;

import gui.DataTypeDetector.DataType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;

import org.jzy3d.maths.Coord3d;

public class LineDataSetSerializable extends LineDataSet implements Serializable {

	public List<ArrayList<Point3d>> data = new ArrayList<ArrayList<Point3d>>();
	
	public LineDataSetSerializable(ParameterSet parameterSet) {
		super(parameterSet);
		// TODO Auto-generated constructor stub
	}
	
	public LineDataSetSerializable(ParameterSet parameterSet, LineDataSet set) {
		super(parameterSet);
		for(ArrayList<Coord3d> list : set.data) {
			ArrayList<Point3d> serList = new ArrayList<Point3d>();
			for(Coord3d coord : list) {
				serList.add(new Point3d(coord.x, coord.y, coord.z));
			}
			data.add(serList);
		}
		this.name = set.getName();
		this.dataType = DataType.LINES;
		this.color = set.getColor();
	}
	
	

}
