package gui;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory.Toolkit;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.CompileableComposite;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import gui.DataTypeDetector.DataType;

public class Plotter {
	
	public static boolean FRAMES = false;
	public boolean SHOWLINES = false;
	
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
		if((data instanceof List<?>)) {
			if(((List<?>) data).get(0) instanceof ArrayList && dataType.equals(DataType.LINES)) {
				System.out.println("line data");
//				List<ArrayList<Coord3d>> data2 = (List<ArrayList<Coord3d>>) data;
//				data = data2;
			}
			else if(((List<?>) data).get(0) instanceof Polygon && dataType.equals(DataType.TRIANGLES)){
				System.out.println("triangle data");
//				List<Polygon> data2 = (List<Polygon>) data;
//				data = data2;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public Chart createChart() {
		chartQuality = Quality.Advanced;
		Chart chart = AWTChartComponentFactory.chart(chartQuality, Toolkit.awt.name());
		
		if(dataType.equals(DataType.LINES)) {
			List<ArrayList<Coord3d>> data2 = (List<ArrayList<Coord3d>>) data;
			List<LineStrip> lineList = new ArrayList<LineStrip>();
			int pointNumber = 0;
			Coord3d[] points = new Coord3d[pointNumber];
	        Color[] colors = new Color[pointNumber];
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
		else if (dataType.equals(DataType.TRIANGLES)) {
			List<Polygon> triangles = (List<Polygon>) data;
	        CompileableComposite comp = new CompileableComposite();
	        for (int part = 0; part < triangles.size(); part++) {
	        	comp.add(triangles.get(part));
	        }
	        
	        if(FRAMES) {
	        	Color factor = new Color(1, 1, 0, 0.0f);
	        	comp.setColor(factor);
	            comp.setWireframeDisplayed(true);
	        }
	        else {
	        	Color factor = new Color(1, 1, 0, 0.65f);
//	            comp.setColor(factor);
	        	comp.setWireframeDisplayed(false);
	        }
	        comp.setWireframeColor(Color.WHITE);
	        comp.setColorMapper(null);
	        chart.getScene().getGraph().add(comp,false);
		}
		chart.getView().setBackgroundColor(Color.BLACK);
        chart.getAxeLayout().setMainColor(Color.WHITE);
		chart.addMouseController();
		return chart;
	}
	
	
	
	
	
	
}
