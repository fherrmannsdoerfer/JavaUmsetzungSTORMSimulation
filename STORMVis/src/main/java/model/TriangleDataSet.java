package model;

import gui.DataTypeDetector.DataType;

import java.io.Serializable;

public class TriangleDataSet extends DataSet implements Serializable{

	public TriangleDataSet(ParameterSet parameterSet) {
		super(parameterSet);
		this.dataType = DataType.TRIANGLES;
		// TODO Auto-generated constructor stub
	}

}
