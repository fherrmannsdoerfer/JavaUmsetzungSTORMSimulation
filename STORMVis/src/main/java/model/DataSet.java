package model;

import gui.DataTypeDetector.DataType;

public class DataSet {
	public DataType dataType;
	public ParameterSet parameterSet;
	public String name;
	
	public DataSet(ParameterSet parameterSet) {
		super();
		this.parameterSet = parameterSet;
	}
	
	public DataSet(ParameterSet parameterSet, String name) {
		super();
		this.parameterSet = parameterSet;
		this.name = name;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
