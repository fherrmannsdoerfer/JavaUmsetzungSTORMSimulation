package model;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
*
* Wraps both DataSets and a project name, including an image if the project was created
* with the editor.
*/

public class Project implements Serializable {
	public String projectName;
	public List<DataSet> dataSets;
	public Float pxPerNm;
	
	public SerializableImage originalImage;
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public List<DataSet> getDataSets() {
		return dataSets;
	}
	public void setDataSets(List<DataSet> dataSets) {
		this.dataSets = dataSets;
	}
	
	public void addDataSet(DataSet set) {
		this.dataSets.add(set);
	}
	
	public void setPxPerNm(Float pxPerNm){
		this.pxPerNm = pxPerNm;
	}
	
	public Float getPxPerNm(){
		return pxPerNm;
	}
	
	public Project(String projectName, List<DataSet> dataSets, Float pxPerNm) {
		super();
		this.projectName = projectName;
		this.dataSets = dataSets;
		this.pxPerNm = pxPerNm;
	}
	
	public Project() {
		super();
		this.projectName = "new project";
		this.dataSets = new ArrayList<DataSet>();
		this.pxPerNm = 1.f;
	}
	
	public SerializableImage getOriginalImage() {
		return originalImage;
	}
	public void setOriginalImage(SerializableImage originalImage) {
		this.originalImage = originalImage;
	}
	
	
}
