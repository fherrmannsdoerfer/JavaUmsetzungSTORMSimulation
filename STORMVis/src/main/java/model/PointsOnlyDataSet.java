package model;

import model.PointsOnlyDataSetSerializable;

import java.io.Serializable;
import java.util.ArrayList;

import javax.vecmath.Point3d;

import org.jzy3d.maths.Coord3d;

import gui.DataTypeDetector.DataType;

public class PointsOnlyDataSet extends DataSet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PointsOnlyDataSet(ParameterSet parameterSet) {
		super(parameterSet);
		this.dataType = DataType.POINTS;
	}


	public PointsOnlyDataSet(ParameterSet parameterSet,
			PointsOnlyDataSetSerializable ser) {
		super(parameterSet);
		this.name = ser.getName();
		this.dataType = DataType.POINTS;
		this.color = ser.getColor();
		
		this.stormData = ser.stormData;
		this.antiBodyEndPoints = ser.antiBodyEndPoints;
		this.antiBodyStartPoints = ser.antiBodyStartPoints;
	}



}
