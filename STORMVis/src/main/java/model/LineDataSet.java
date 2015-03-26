package model;

import gui.DataTypeDetector.DataType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jzy3d.maths.Coord3d;

public class LineDataSet extends DataSet implements Serializable{
	
	public List<ArrayList<Coord3d>> data = new ArrayList<ArrayList<Coord3d>>();
	
	public LineDataSet(ParameterSet parameterSet) {
		super(parameterSet);
		this.dataType = DataType.LINES;
		// TODO Auto-generated constructor stub
	}

}
