package playground;

import gui.DataTypeDetector.DataType;

import java.util.ArrayList;
import java.util.List;

import model.DataSet;
import model.LineDataSet;
import model.TriangleDataSet;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory.Toolkit;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.CompileableComposite;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class Plot3D {

	public List<DataSet> dataSets = new ArrayList<DataSet>();
	public Quality chartQuality;
	
	/**
	 * select whether lines should be displayed
	 */
	public boolean showLines = true;
	
	public Plot3D() {
		
	}
	
	public void addDataSet(DataSet s) {
		if(dataSets != null) {
			dataSets.add(s);
		}
	}
	
	public void addAllDataSets(List<DataSet> list) {
		if(dataSets != null) {
			dataSets.addAll(list);
		}
	}
	
	
	public Chart createChart() {
		chartQuality = Quality.Advanced;
		Chart chart = AWTChartComponentFactory.chart(chartQuality, Toolkit.awt.name());
		
		for(DataSet set : dataSets) {
			if(set.dataType == DataType.LINES) {
				LineDataSet lines = (LineDataSet) set;
				
				int pointNumber = 0;
				// TODO: count point number in editor!
				if(lines.pointNumber == null || lines.pointNumber.intValue() == 0) {
					for(ArrayList<Coord3d> list : lines.data) {
						pointNumber += list.size();
					}
				}
				else {
					pointNumber = lines.pointNumber.intValue();
				}
				Coord3d[] points = new Coord3d[pointNumber];
		        Color[] colors = new Color[pointNumber];
		        
		        List<LineStrip> lineList = new ArrayList<LineStrip>();
				int i = 0;
		        for(ArrayList<Coord3d> obj : lines.data) {
		        	LineStrip strip = new LineStrip();
		    		strip.setWidth(2.f);
		    		strip.setWireframeColor(Color.WHITE);
		        	for(Coord3d coord : obj) {
		        		points[i] = coord;
		        		float a = 1.f;
		        		colors[i] = new Color(coord.x/255.f,coord.y/255.f,coord.z/255.f,a);
		        		if(showLines) {
		        			strip.add(new Point(coord));
		        		}
		        		i++;
		        	}
		        	lineList.add(strip);
		        }
		        CompileableComposite comp = new CompileableComposite();
		        Scatter scatter = new Scatter(points, colors, 2.f);
		        for (LineStrip line : lineList) {
		        	if(line.getPoints().size() != 0) {
		        		chart.getScene().getGraph().add(line);
		        	}
		        }
		        comp.add(scatter);
		        chart.getScene().getGraph().add(comp);
			}
			else if(set.dataType == DataType.TRIANGLES) {
				TriangleDataSet triangles = (TriangleDataSet) set;
				CompileableComposite comp = new CompileableComposite();
				comp.add(triangles.drawableTriangles);
				chart.getScene().getGraph().add(comp);
			}
		}
		
		chart.getView().setBackgroundColor(Color.BLACK);
        chart.getAxeLayout().setMainColor(Color.WHITE);
		chart.addMouseController();
		return chart;
	}
	
}
