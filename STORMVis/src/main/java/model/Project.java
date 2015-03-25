package model;

import java.util.ArrayList;
import java.util.List;

public class Project {
	public String projectName;
	public List<DataSet> dataSets;
	
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
	
	public Project(String projectName, List<DataSet> dataSets) {
		super();
		this.projectName = projectName;
		this.dataSets = dataSets;
	}
	
	public Project() {
		super();
		this.projectName = "new project";
		this.dataSets = new ArrayList<DataSet>();
	}
}
