package gui;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory.Toolkit;
import org.jzy3d.chart.factories.SwingChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Scale;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.CompileableComposite;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Sphere;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;

import com.jogamp.newt.swt.NewtCanvasSWT;

import parsing.LineObjectParser;
import parsing.STORMObjectParser;
import parsing.TriangleObjectParser;

public class ScatterSwing {
	
	static String FILE2 = "Microtubules.wimp";
	static String FILE3 = "Microtubules_large.wimp";
	
	public static boolean SHOWLINES = false;
	public boolean lighton = false;	
	public static boolean TRIANGLES = true;
	public static boolean FRAMES = true;
	public boolean STORM = false;
	
	public Coord3d[] stormPoints;
	public Color[] stormColors;
	
	public ScatterSwing() {
		
	}
	
	public Chart getSwingChart() {
		System.out.println("Light: " + lighton);
		long startTime = System.nanoTime();
		
		
		LineObjectParser lineParser = new LineObjectParser(FILE2);
		try {
			lineParser.parse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TriangleObjectParser trParser = new TriangleObjectParser(null);
		trParser.limit = 20000;
		
		try {
			trParser.parse();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		List<LineStrip> lineList = new ArrayList<LineStrip>();
		Coord3d[] points = new Coord3d[lineParser.pointNumber];
        Color[] colors = new Color[lineParser.pointNumber];
		int i = 0;
        for(ArrayList<Coord3d> obj : lineParser.allObjects) {
        	boolean ALLOBJECTS = (lineParser.allObjects.indexOf(obj) == 0) || true;
        	LineStrip strip = new LineStrip();
    		strip.setWidth(2.f);
    		strip.setWireframeColor(Color.BLACK);
        	for(Coord3d coord : obj) {
        		points[i] = coord;
        		float a = 1.f;
        		colors[i] = new Color(coord.x/255.f,coord.y/255.f,coord.z/255.f,a);
        		if(SHOWLINES && ALLOBJECTS) {
        			strip.add(new Point(coord));
        		}
        		i++;
        	}
        	lineList.add(strip);
        }
        
        Scatter scatter = new Scatter(points, colors, 2.f);
        if(STORM) {
        	scatter = new Scatter(stormPoints, stormColors, 2.f);
        }

        CompileableComposite compPoints1 = new CompileableComposite();
        
        /*for(Coord3d p : points) {
        	compPoints1.add(new Point(p, new Color(p.x/255.f,p.y/255.f,p.z/255.f,1.f)));
        }*/
        
        compPoints1.add(scatter);
        
        
        Chart chart2 = AWTChartComponentFactory.chart(Quality.Nicest, Toolkit.awt.name());
        chart2.getView().setBackgroundColor(Color.BLACK);
        chart2.getAxeLayout().setMainColor(Color.WHITE);
        
        
        /*
         * Points from STORM Simulation 
         * 
         */
        STORMObjectParser stParser = new STORMObjectParser();
//        try {
//			stParser.parse();
//		} catch (NumberFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        System.out.println("Storm point number: "+stParser.allPoints.size());
//        CompileableComposite compPoints = new CompileableComposite();
//        for(Point p : stParser.allPoints) {
//        	compPoints.add(p);
//        }
        
        /*
         * 
         */
        
        if(!TRIANGLES){
        	//chart.getScene().getGraph().add(scatter,false);
        	chart2.getScene().getGraph().add(compPoints1,false);
        }
        
        
        // jzy3d does not throw an exception when there is a line with 0 points, but does not load the coordinate system
        for (LineStrip line : lineList) {
        	if(line.getPoints().size() != 0) {
        		chart2.getScene().getGraph().add(line);
        	}
        }
        
        int parts = 1;
        CompileableComposite comp = new CompileableComposite();
        for (int part = 0; part < parts; part++) {
            for (int c = (int) (part * trParser.allTriangles.size()/parts); c < (int) ((part* trParser.allTriangles.size() + trParser.allTriangles.size())/parts); c++) {
            	comp.add(trParser.allTriangles.get(c));
            }
        }
        
        if(FRAMES) {
        	Color factor = new Color(1, 1, 0, 0.0f);
        	comp.setColor(factor);
            comp.setWireframeDisplayed(true);
        }
        else {
        	Color factor = new Color(1, 1, 0, 0.65f);
//            comp.setColor(factor);
        	comp.setWireframeDisplayed(false);
        }
        comp.setWireframeColor(Color.WHITE);
        comp.setColorMapper(null);
        if(TRIANGLES) chart2.getScene().getGraph().add(comp,false);
        
        
        if(lighton) {
        	Light light = chart2.addLight(new Coord3d(500f, 500f, 2500f));
        	light.setRepresentationRadius(100);
        	light.setAmbiantColor(new Color(1f, 0,0));
        }        
        System.out.println("Line list elements: " + lineList.size());
        
        ZoomController cont = new ZoomController();
        chart2.addController(cont);
        System.out.println("Drawing " + lineParser.pointNumber + " points.");
        long stop = System.nanoTime();
        System.out.println("Total startup time: " + (stop-startTime)/1e9 +" s");
		return chart2;
    }
	
	
	public class ZoomController extends AWTCameraMouseController {
		public ZoomController() {
			super();
		}
		
		public void mouseWheelMoved(MouseWheelEvent e) { 
            if(threadController!=null) 
                    threadController.stop(); 

            float factor = 1 + (e.getWheelRotation()/100.f); 
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
                    if(factor<1) 
                            scale = new org.jzy3d.maths.Scale(center, center); 
            } 
            BoundingBox3d bounds = View.current().getBounds(); 
            bounds.setZmin((float)scale.getMin()); 
            bounds.setZmax((float)scale.getMax()); 
            bounds.setYmin((float)scale.getMin()); 
            bounds.setYmax((float)scale.getMax()); 
            bounds.setXmin((float)scale.getMin()); 
            bounds.setXmax((float)scale.getMax()); 
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
		return s;
	}

}