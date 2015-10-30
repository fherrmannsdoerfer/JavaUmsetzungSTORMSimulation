
package model;

import java.io.Serializable;
import java.util.ArrayList;

import javax.vecmath.Point3d;

import org.jzy3d.maths.Coord3d;

import gui.DataTypeDetector.DataType;

public class EpitopeDataSet extends DataSet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EpitopeDataSet(ParameterSet parameterSet) {
		super(parameterSet);
		this.dataType = DataType.EPITOPES;
	}


	public EpitopeDataSet(ParameterSet parameterSet,
			EpitopeDataSetSerializable ser) {
		super(parameterSet);
		this.name = ser.getName();
		this.dataType = DataType.EPITOPES;
		this.color = ser.getColor();
		
		this.stormData = ser.stormData;
		this.antiBodyEndPoints = ser.antiBodyEndPoints;
		this.antiBodyStartPoints = ser.antiBodyStartPoints;
	}



}
