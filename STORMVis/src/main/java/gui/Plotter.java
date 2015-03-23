package gui;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Polygon;

import gui.DataTypeDetector.DataType;

public class Plotter {
	
	public DataType dataType;
	public Object data;
	
	public Plotter(Object data, DataType dataType) {
		this.dataType = dataType;
		this.data = data;
		
		if (data == null || dataType == null) {
			throw new IllegalArgumentException("Null Pointer exception. A team of highly trained monkeys has been sent to solve this incident.");
		}
		
		System.out.println("class: " + ((List<?>) data).get(0).getClass().toString());
		
		if((data instanceof List<?>)) {
			if(((List<?>) data).get(0) instanceof ArrayList && dataType.equals(DataType.WIMP)) {
				System.out.println("line data");
				@SuppressWarnings("unchecked")
				List<ArrayList<Coord3d>> data2 = (List<ArrayList<Coord3d>>) data;
				data = data2;
			}
			else if(((List<?>) data).get(0) instanceof Polygon && dataType.equals(DataType.NFF)){
				System.out.println("triangle data");
				@SuppressWarnings("unchecked")
				List<Polygon> data2 = (List<Polygon>) data;
				data = data2;
			}
		}
	}
}
