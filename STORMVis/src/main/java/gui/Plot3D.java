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
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import calc.Calc;

public class Plot3D {

	public List<DataSet> dataSets = new ArrayList<DataSet>();
	public Quality chartQuality;
	public boolean squared = false;
	public boolean showBox = true;
	public boolean showTicks = true;
	public Chart currentChart = null;
	public Color backgroundColor = Color.BLACK;
	public Color mainColor = Color.WHITE;
	
	public Coord3d viewPoint;
	public BoundingBox3d viewBounds;
	
	public ArrayList<Float> borders;
	
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
				if(lines.getParameterSet().getEmVisibility() == true) {
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
					
			        for(ArrayList<Coord3d> obj : lines.data) {
			        	int i = 0;
			        	for(Coord3d coord : obj){
			        		if (coord.x<borders.get(0)||coord.x>borders.get(1)||coord.y<borders.get(2)||coord.y>borders.get(3)||coord.z<borders.get(4)||coord.z>borders.get(5)){
			        			
			        		}
			        		else{
			        			LineStrip strip = new LineStrip();
			        			strip.setWidth(lines.getParameterSet().getLineWidth());
			        			strip.setWireframeColor(new Color(lines.getParameterSet().getEmColor().getRed()/255.f, lines.getParameterSet().getEmColor().getGreen()/255.f, lines.getParameterSet().getEmColor().getBlue()/255.f,1.f));
			        			points[i] = coord;
				        		float a = 1.f;
				        		colors[i] = new Color(lines.getParameterSet().getEmColor().getRed()/255.f, lines.getParameterSet().getEmColor().getGreen()/255.f, lines.getParameterSet().getEmColor().getBlue()/255.f,a);
				        		if (i>0){
				        			strip.add(new Point(points[i-1]));
				        			strip.add(new Point(points[i]));
				        		}
				        		i++;
				        		lineList.add(strip);
			        		}
			        	}
			        	
			       
			        	
			        }

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
				float a = 0.5f;//1.f;
				TriangleDataSet triangles = (TriangleDataSet) set;
				if(triangles.getParameterSet().getEmVisibility() == true) {
					//System.out.println("Show triangles EM");
					List<Polygon> croppedTri = triangles.cropTriangle(borders);
					if (croppedTri.size()!= 0 ){
						CompileableComposite comp = new CompileableComposite();
						comp.add(croppedTri);
						comp.setColor(new Color(triangles.getParameterSet().getEmColor().getRed()/255.f, triangles.getParameterSet().getEmColor().getGreen()/255.f, triangles.getParameterSet().getEmColor().getBlue()/255.f,a));
						comp.setWireframeDisplayed(false);
						comp.setWireframeColor(Color.BLACK);
				        comp.setWireframeWidth(0.00001f);
				        comp.setColorMapper(null);
						chart.getScene().getGraph().add(comp);
					}
				}
			}
			
			// check if ABs should be displayed
			if(set.getParameterSet().getAntibodyVisibility() == true && set.antiBodyEndPoints != null && set.antiBodyStartPoints != null) {
				//System.out.println("show ABs");
				CompileableComposite comp = new CompileableComposite();
		        for(int i = 0; i < set.antiBodyEndPoints.length; i++) {
		        	float[] currentRowStart = set.antiBodyStartPoints[i];
		    		float[] currentRowEnd = set.antiBodyEndPoints[i];
		        	boolean showAB = true;
		    		if (currentRowStart[0] <borders.get(0)||currentRowStart[0]>borders.get(1)||currentRowStart[1]<borders.get(2)||currentRowStart[1]>borders.get(3)||currentRowStart[2]<borders.get(4)||currentRowStart[2]>borders.get(5)){
		    			showAB = false;
		    		}
		    		if (currentRowEnd[0] <borders.get(0)||currentRowEnd[0]>borders.get(1)||currentRowEnd[1]<borders.get(2)||currentRowEnd[1]>borders.get(3)||currentRowEnd[2]<borders.get(4)||currentRowEnd[2]>borders.get(5)){
		    			showAB = false;
		    		}
		    		if (showAB){
			        	LineStrip strip = new LineStrip();
			    		strip.setWidth(set.getParameterSet().getLineWidth());
			    		strip.setWireframeColor(new Color(set.getParameterSet().getAntibodyColor().getRed()/255.f, set.getParameterSet().getAntibodyColor().getGreen()/255.f, set.getParameterSet().getAntibodyColor().getBlue()/255.f, 1.f));
			        	
			    		
			    		strip.add(new Point(new Coord3d(currentRowStart[0],currentRowStart[1],currentRowStart[2])));
			    		strip.add(new Point(new Coord3d(currentRowEnd[0],currentRowEnd[1],currentRowEnd[2])));
			    		comp.add(strip);
		    		}
		        }
		        if (comp.size()!=0){
		        	chart.getScene().getGraph().add(comp);
		        }
			}
			
			// Check if STORM should be displayed
			if(set.getParameterSet().getStormVisibility() == true && set.stormData != null) {
				//System.out.println("show storm");
				float[][] result = Calc.findStormDataInRange(set.stormData, borders);
				Coord3d[] points = new Coord3d[result.length];;
				Color[] colors = new Color[result.length];
				for (int i = 0; i < result.length; i++) {
					Coord3d coord = new Coord3d(result[i][0], result[i][1], result[i][2]);
					points[i] = coord;
					colors[i] = new Color(set.getParameterSet().getStormColor().getRed()/255.f, set.getParameterSet().getStormColor().getGreen()/255.f, set.getParameterSet().getStormColor().getBlue()/255.f, result[i][3]);
				}
				if (result.length != 0){
					CompileableComposite comp = new CompileableComposite();
			        float pointSize = set.getParameterSet().getPointSize();
			        Scatter scatter = new Scatter(points, colors, pointSize);
			        comp.add(scatter);
			        chart.getScene().getGraph().add(comp);
				}
			}
			
		}
		if(viewPoint != null && viewBounds != null) {
			chart.getView().setBoundManual(viewBounds);
			chart.setViewPoint(viewPoint);
		}
		chart.setAxeDisplayed(showBox);
		chart.getView().setSquared(squared);
		chart.getView().setBackgroundColor(backgroundColor);
        chart.getAxeLayout().setMainColor(mainColor);
		chart.addController(new ZoomController());
		
		chart.getAxeLayout().setXAxeLabelDisplayed(showTicks);
		chart.getAxeLayout().setYAxeLabelDisplayed(showTicks);
		chart.getAxeLayout().setZAxeLabelDisplayed(showTicks);
		chart.getAxeLayout().setXTickLabelDisplayed(showTicks);
		chart.getAxeLayout().setYTickLabelDisplayed(showTicks);
		chart.getAxeLayout().setZTickLabelDisplayed(showTicks);
		
		currentChart = chart;
		return chart;
	}
	
}
