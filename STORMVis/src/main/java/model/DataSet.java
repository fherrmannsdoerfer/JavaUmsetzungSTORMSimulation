package model;

import gui.DataTypeDetector.DataType;

public class DataSet {
	public DataType dataType;
	public ParameterSet parameterSet;
	
	public DataSet(ParameterSet parameterSet) {
		super();
		this.parameterSet = parameterSet;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public ParameterSet getParameterSet() {
		return parameterSet;
	}

	public void setParameterSet(ParameterSet parameterSet) {
		this.parameterSet = parameterSet;
	}
	
}
