package gui;

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
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.CompileableComposite;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import calc.Calc;

public class Plot3D {

	public List<DataSet> dataSets = new ArrayList<DataSet>();
	public Quality chartQuality;
	public boolean squared = false;
	public boolean showBox = true;
	public Chart currentChart = null;
	
	public Coord3d viewPoint;
	public BoundingBox3d viewBounds;
	
	/**
	 * select whether lines should be displayed
	 */
	public boolean showLines = true;
	
	public Plot3D() {
		chartQuality = Quality.Nicest;
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
		
		Chart chart =AWTChartComponentFactory.chart(chartQuality, Toolkit.awt.name());
		for(DataSet set : dataSets) {
			
			if(set.dataType == DataType.LINES) {
				LineDataSet lines = (LineDataSet) set;
				
				// Check if EM should be shown
				if(lines.getParameterSet().emVisibility == true) {
					//System.out.println("Show lines EM");
					int pointNumber = 0;
					// TODO: count point number in editor?!
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
			    		strip.setWidth(lines.getParameterSet().lineWidth);
			    		strip.setWireframeColor(new Color(lines.getParameterSet().getEmColor().getRed()/255.f, lines.getParameterSet().getEmColor().getGreen()/255.f, lines.getParameterSet().getEmColor().getBlue()/255.f,1.f));
			        	for(Coord3d coord : obj) {
			        		points[i] = coord;
			        		float a = 1.f;
			        		// TODO: use for multiple colors
//			        		colors[i] = new Color(coord.x/255.f,coord.y/255.f,coord.z/255.f,a);
			        		colors[i] = new Color(lines.getParameterSet().getEmColor().getRed()/255.f, lines.getParameterSet().getEmColor().getGreen()/255.f, lines.getParameterSet().getEmColor().getBlue()/255.f,a);
			        		if(showLines) {
			        			strip.add(new Point(coord));
			        		}
			        		i++;
			        	}
			        	lineList.add(strip);
			        }
			        //CompileableComposite comp = new CompileableComposite();
			        float pointSize = 0;// lines.getParameterSet().pointSize;
			        //Scatter scatter = new Scatter(points, colors, pointSize);
			        for (LineStrip line : lineList) {
			        	if(line.getPoints().size() != 0) {
			        		chart.getScene().getGraph().add(line);
			        	}
			        }
			        //comp.add(scatter);
			        //chart.getScene().getGraph().add(comp);
				}
			}
			else if(set.dataType == DataType.TRIANGLES) {
				float a = 1.f;
				TriangleDataSet triangles = (TriangleDataSet) set;
				if(triangles.getParameterSet().emVisibility == true) {
					//System.out.println("Show triangles EM");
					CompileableComposite comp = new CompileableComposite();
					comp.add(triangles.drawableTriangles);
					comp.setColor(new Color(triangles.getParameterSet().getEmColor().getRed()/255.f, triangles.getParameterSet().getEmColor().getGreen()/255.f, triangles.getParameterSet().getEmColor().getBlue()/255.f,a));
					comp.setWireframeDisplayed(false);
					comp.setWireframeColor(Color.BLACK);
			        comp.setWireframeWidth(0.00001f);
			        comp.setColorMapper(null);
					chart.getScene().getGraph().add(comp);
				}
			}
			
			// check if ABs should be displayed
			if(set.getParameterSet().antibodyVisibility == true && set.antiBodyEndPoints != null && set.antiBodyStartPoints != null) {
				//System.out.println("show ABs");
				CompileableComposite comp = new CompileableComposite();
		        for(int i = 0; i < set.antiBodyEndPoints.length; i++) {
		        	LineStrip strip = new LineStrip();
		    		strip.setWidth(set.getParameterSet().lineWidth);
		    		strip.setWireframeColor(new Color(set.getParameterSet().getAntibodyColor().getRed()/255.f, set.getParameterSet().getAntibodyColor().getGreen()/255.f, set.getParameterSet().getAntibodyColor().getBlue()/255.f, 1.f));
		        	
		    		float[] currentRowStart = set.antiBodyStartPoints[i];
		    		float[] currentRowEnd = set.antiBodyEndPoints[i];
		    		strip.add(new Point(new Coord3d(currentRowStart[0],currentRowStart[1],currentRowStart[2])));
		    		strip.add(new Point(new Coord3d(currentRowEnd[0],currentRowEnd[1],currentRowEnd[2])));
		    		comp.add(strip);
		        }
		        chart.getScene().getGraph().add(comp);
			}
			
			// Check if STORM should be displayed
			if(set.getParameterSet().stormVisibility == true && set.stormData != null) {
				//System.out.println("show storm");
				float[][] result = set.stormData;
				Coord3d[] points = new Coord3d[result.length];;
				Color[] colors = new Color[result.length];
				for (int i = 0; i < result.length; i++) {
					Coord3d coord = new Coord3d(result[i][0], result[i][1], result[i][2]);
					points[i] = coord;
					colors[i] = new Color(set.getParameterSet().getStormColor().getRed()/255.f, set.getParameterSet().getStormColor().getGreen()/255.f, set.getParameterSet().getStormColor().getBlue()/255.f, result[i][3]);
				}
				CompileableComposite comp = new CompileableComposite();
		        float pointSize = set.getParameterSet().pointSize;
		        Scatter scatter = new Scatter(points, colors, pointSize);
		        comp.add(scatter);
		        chart.getScene().getGraph().add(comp);
			}
			
		}
		
		if(viewPoint != null && viewBounds != null) {
			chart.getView().setBoundManual(viewBounds);
			chart.setViewPoint(viewPoint);
		}
		chart.setAxeDisplayed(showBox);
		chart.getView().setSquared(squared);
		chart.getView().setBackgroundColor(Color.BLACK);
        chart.getAxeLayout().setMainColor(Color.WHITE);
		chart.addController(new ZoomController());
		currentChart = chart;
		return chart;
	}
	
}
