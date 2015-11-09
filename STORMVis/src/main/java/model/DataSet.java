package model;

import gui.DataTypeDetector.DataType;

import java.awt.Color;
import java.io.Serializable;

import javax.swing.JProgressBar;
/**
 * 
 * @author maxscheurer
 * @brief Superclass for all DataSets
 * All other dataSets inherit from DataSets
 */

public class DataSet implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public DataType dataType;
	public ParameterSet parameterSet;
	public String name;
	public Color color;
	
	public float[][] stormData;
	public float[][] antiBodyStartPoints;
	public float[][] antiBodyEndPoints;
	
	public boolean isCalculating;
	
	public JProgressBar progressBar;
	
	public DataSet(ParameterSet parameterSet) {
		super();
		this.parameterSet = parameterSet;
		this.color = Color.red;
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

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setProgressBar(JProgressBar progressBar){
		this.progressBar = progressBar;
	}
	
	public JProgressBar getProgressBar(){
		return this.progressBar;
	}
	
}
