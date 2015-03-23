package gui;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory.Toolkit;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import gui.DataTypeDetector.DataType;

public class Plotter {
	
	public boolean SHOWLINES;
	
	public DataType dataType;
	public Object data;
	public Quality chartQuality;
	public int linePointNumber;
	
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
	
	@SuppressWarnings("unchecked")
	public Chart createChart() {
		Chart chart = AWTChartComponentFactory.chart(chartQuality, Toolkit.awt.name());
		
		if(dataType.equals(DataType.WIMP)) {
			List<ArrayList<Coord3d>> data2 = (List<ArrayList<Coord3d>>) data;
			List<LineStrip> lineList = new ArrayList<LineStrip>();
			Coord3d[] points = new Coord3d[lineParser.pointNumber];
	        Color[] colors = new Color[lineParser.pointNumber];
			int i = 0;
	        for(ArrayList<Coord3d> obj : data2) {
	        	LineStrip strip = new LineStrip();
	    		strip.setWidth(2.f);
	    		strip.setWireframeColor(Color.BLACK);
	        	for(Coord3d coord : obj) {
	        		points[i] = coord;
	        		float a = 1.f;
	        		colors[i] = new Color(coord.x/255.f,coord.y/255.f,coord.z/255.f,a);
	        		if(SHOWLINES) {
	        			strip.add(new Point(coord));
	        		}
	        		i++;
	        	}
	        	lineList.add(strip);
	        }
		}
		else if (dataType.equals(DataType.NFF)) {
			
		}
		
		
		return chart;
	}
	
	
	
}
