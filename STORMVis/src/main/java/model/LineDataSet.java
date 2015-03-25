package model;

import java.io.Serializable;

import gui.DataTypeDetector.DataType;

public class LineDataSet extends DataSet implements Serializable{

	public LineDataSet(ParameterSet parameterSet) {
		super(parameterSet);
		this.dataType = DataType.LINES;
		// TODO Auto-generated constructor stub
	}

}
