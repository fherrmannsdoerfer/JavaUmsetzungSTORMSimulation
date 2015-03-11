import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Scale;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Sphere;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.plot3d.primitives.Point;

public class ScatterDemo extends AbstractAnalysis{
	
	static boolean SHOWSPHERES = false;
	static boolean LIGHTON = false;
	
		
	public void init() throws IOException{
		LineObjectParser lineParser = new LineObjectParser(null);
		lineParser.parse();
        
		List<AbstractDrawable> sphereList = new ArrayList<AbstractDrawable>();
		List<AbstractDrawable> lineList = new ArrayList<AbstractDrawable>();
		List<List<AbstractDrawable>> separateLines = new ArrayList<List<AbstractDrawable>>();
		
		Coord3d[] points = new Coord3d[lineParser.pointNumber];
        Color[] colors = new Color[lineParser.pointNumber];
		int i = 0;
		boolean first = true;
		Point point1 = null, point2 = null;
        for(ArrayList<Coord3d> obj : lineParser.allObjects) {
        	boolean ALLOBJECTS = (lineParser.allObjects.indexOf(obj) == 0 || (lineParser.allObjects.indexOf(obj)) == 1 ) && true;
        	for(Coord3d coord : obj) {
        		points[i] = coord;
        		float a = 1.f;
        		colors[i] = new Color(coord.x/255.f,coord.y/255.f,coord.z/255.f,a);
        		if(SHOWSPHERES) {
        			sphereList.add(addSphere(points[i], colors[i], 3.f, 5));
        		}
        		/*
        		if(first && ALLOBJECTS) {
        			lineList = new ArrayList<AbstractDrawable>();
        			point1 = new Point(points[i]);
        			first = false;
        		}
        		else if (!first && ALLOBJECTS){
        			point2 = new Point(points[i]);
        			first = true;
        			LineStrip strip = new LineStrip(point1,point2);
        			strip.setWireframeColor(Color.BLACK);
        			strip.setWidth(4.f);
        			lineList.add(strip);
        		}
        		*/
        		i++;
        	}//        	separateLines.add(lineList);
        	//lineList.clear();
        	int c = 0;
        	for(Coord3d coord : obj) {
        		points[c] = coord;
        		if(!obj.get(obj.size()-1).equals(coord) && ALLOBJECTS) {
        			lineList = new ArrayList<AbstractDrawable>();
        			point1 = new Point(points[c]);
            		point2 = new Point(points[c+1]);
            		LineStrip strip = new LineStrip(point1,point2);
        			strip.setWireframeColor(Color.BLACK);
        			strip.setWidth(1.f);
        			lineList.add(strip);
        		}    
        		c++;
        	}
//        	List<AbstractDrawable> copy = new ArrayList<AbstractDrawable>();
//        	copy.addAll(lineList);
//        	separateLines.add(copy);
//        	lineList.clear();
        }
                
        Scatter scatter = new Scatter(points, colors, 5.f);
        chart = AWTChartComponentFactory.chart(Quality.Nicest, "awt");
        
        if(SHOWSPHERES) {
        	chart.getScene().add(sphereList);
        }
        else {
        	chart.getScene().add(scatter);
        }
        
        
        for(List<AbstractDrawable> linesOfObject : separateLines) {
        	chart.getScene().add(linesOfObject);
        }
        
        //chart.getScene().add(lineList);
        
        System.out.println("Line list elements: " + lineList.size());
        
        
        if(LIGHTON) {
        	Light light = chart.addLight(new Coord3d(500f, 500f, 2500f));
        	light.setRepresentationRadius(100);
        	light.setAmbiantColor(new Color(1f, 0,0));
        	//light.setDiffuseColor(new Color((255.f/255.f), 0, 0));
        }
        
        ZoomController cont = new ZoomController();
        chart.addController(cont);
        System.out.println(chart.getControllers());
        System.out.println("Drawing " + lineParser.pointNumber + " points.");
        
    }
	
	
	public class ZoomController extends AWTCameraMouseController {
		public ZoomController() {
			super();
		}
		
		public void mouseWheelMoved(MouseWheelEvent e) { 
            if(threadController!=null) 
                    threadController.stop(); 

            float factor = 1 + (e.getWheelRotation()/100.f); 
            //zoom(factor); 

            Scale currScale = View.current().getScale(); 
            double range = currScale.getMax() - currScale.getMin(); 

            if(range<=0) 
                    return; 

            double center = (currScale.getMax() + currScale.getMin())/2; 
            double min   = center + (currScale.getMin()-center) * (factor); 
            double max   = center + (currScale.getMax()-center) * (factor); 
            Scale scale = null; 
            if(min<max) { 
                    scale = new org.jzy3d.maths.Scale(min, max); 
            } 
            else{ 
                    if(factor<1) // forbid to have min = max if we zoom in 
                            scale = new org.jzy3d.maths.Scale(center, center); 
            } 
            BoundingBox3d bounds = View.current().getBounds(); 
            bounds.setZmin((float)scale.getMin()); 
            bounds.setZmax((float)scale.getMax()); 
            bounds.setYmin((float)scale.getMin()); 
            bounds.setYmax((float)scale.getMax()); 
            bounds.setXmin((float)scale.getMin()); 
            bounds.setXmax((float)scale.getMax()); 
            //View.current().setBoundManual(bounds); can't use this 
            //View.current().updateBounds(); 
            View.current().shoot(); 
		}
	}
	
	
	
	protected Sphere addSphere(Coord3d c, Color color, float radius,
			int slicing) {
		c = c.set(c.x, c.y, c.z*10);
		Sphere s = new Sphere(c, radius, slicing, color);
		s.setWireframeColor(color);
		s.setWireframeDisplayed(true);
		s.setWireframeWidth(1);
		s.setFaceDisplayed(true);
		// s.setMaterialAmbiantReflection(materialAmbiantReflection)
		return s;
	}
	
	
	
	
}