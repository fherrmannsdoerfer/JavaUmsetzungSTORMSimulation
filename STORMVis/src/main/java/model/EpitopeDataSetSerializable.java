package model;

import gui.DataTypeDetector.DataType;

import java.io.Serializable;

public class EpitopeDataSetSerializable extends EpitopeDataSet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EpitopeDataSetSerializable(ParameterSet parameterSet) {
		super(parameterSet);
		// TODO Auto-generated constructor stub
	}
	
	public EpitopeDataSetSerializable(ParameterSet parameterSet, EpitopeDataSet set) {
		super(parameterSet);
		this.name = set.getName();
		this.dataType = DataType.EPITOPES;
		this.color = set.getColor();
		
		this.stormData = set.stormData;
		this.antiBodyEndPoints = set.antiBodyEndPoints;
		this.antiBodyStartPoints = set.antiBodyStartPoints;
	}
	

}
