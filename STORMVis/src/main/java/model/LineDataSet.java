package model;

import gui.DataTypeDetector.DataType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;

import org.jzy3d.maths.Coord3d;

public class LineDataSet extends DataSet implements Serializable{
	
	public List<ArrayList<Coord3d>> data = new ArrayList<ArrayList<Coord3d>>();
	public Integer pointNumber;
	public Integer objectNumber;
	
	public LineDataSet(ParameterSet parameterSet) {
		super(parameterSet);
		this.dataType = DataType.LINES;
		// TODO Auto-generated constructor stub
	}
	
	public LineDataSet(ParameterSet parameterSet, LineDataSetSerializable ser) {
		super(parameterSet);
		this.name = ser.getName();
		this.dataType = DataType.LINES;
		this.color = ser.getColor();
		for(ArrayList<Point3d> list : ser.data) {
			ArrayList<Coord3d> serList = new ArrayList<Coord3d>();
			for(Point3d coord : list) {
				serList.add(new Coord3d(coord.x, coord.y, coord.z));
			}
			data.add(serList);
		}
		this.stormData = ser.stormData;
		this.antiBodyEndPoints = ser.antiBodyEndPoints;
		this.antiBodyStartPoints = ser.antiBodyStartPoints;
	}
	
	 public void rescaleData(Float factor){
		 for (int i = 0; i<data.size(); i++){
			 ArrayList<Coord3d> tmp = new ArrayList<Coord3d>();
			 for (int j = 0; j<data.get(i).size(); j++){
				 Coord3d tmpCoord = new Coord3d();
				 tmpCoord.x = data.get(i).get(j).x*factor;
				 tmpCoord.y = data.get(i).get(j).y*factor;
				 tmpCoord.z = data.get(i).get(j).z*factor;
				 tmp.add(tmpCoord);
			 }
			 data.set(i, tmp);
		 }
	 }
}
