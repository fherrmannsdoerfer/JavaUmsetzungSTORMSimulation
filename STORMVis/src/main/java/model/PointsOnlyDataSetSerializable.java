package model;

import gui.DataTypeDetector.DataType;

import java.io.Serializable;

public class PointsOnlyDataSetSerializable extends PointsOnlyDataSet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PointsOnlyDataSetSerializable(ParameterSet parameterSet) {
		super(parameterSet);
		// TODO Auto-generated constructor stub
	}
	
	public PointsOnlyDataSetSerializable(ParameterSet parameterSet, PointsOnlyDataSet set) {
		super(parameterSet);
		this.name = set.getName();
		this.dataType = DataType.POINTS;
		this.color = set.getColor();
		
		this.stormData = set.stormData;
		this.antiBodyEndPoints = set.antiBodyEndPoints;
		this.antiBodyStartPoints = set.antiBodyStartPoints;
	}
	

}
