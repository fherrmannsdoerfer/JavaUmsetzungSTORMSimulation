import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRBG;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Scale;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.CompileableComposite;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.Sphere;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.transform.Rotate;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.Translate;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.chart.factories.IChartComponentFactory.Toolkit;

import com.jogamp.graph.geom.Triangle;
import com.jogamp.graph.geom.Vertex;

public class ScatterDemo extends AbstractAnalysis {
	
	static String FILE2 = "Microtubules.wimp";
	static String FILE3 = "Microtubules_large.wimp";
	
	static boolean SHOWSPHERES = false;
	static boolean SHOWLINES = false;
	static boolean LIGHTON = false;	
	static boolean TRIANGLES = false;
	static boolean FRAMES = false;
		
	public void init() throws IOException{
	
	}
	
	public Chart getChart() {
		long startTime = System.nanoTime();
		LineObjectParser lineParser = new LineObjectParser(FILE2);
		try {
			lineParser.parse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TriangleObjectParser trParser = new TriangleObjectParser(null);
		trParser.limit = 10000;
		try {
			trParser.parse();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		List<AbstractDrawable> sphereList = new ArrayList<AbstractDrawable>();
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
        		if(SHOWSPHERES) {
        			sphereList.add(addSphere(points[i], colors[i], 3.f, 5));
        		}
        		if(SHOWLINES && ALLOBJECTS) {
        			strip.add(new Point(coord));
        		}
        		i++;
        	}
        	lineList.add(strip);
        }
        Scatter scatter = new Scatter(points, colors, 3.f);
        
        CompileableComposite compPoints1 = new CompileableComposite();
        /*for(Coord3d p : points) {
        	compPoints1.add(new Point(p, new Color(p.x/255.f,p.y/255.f,p.z/255.f,1.f)));
        }*/
        
        compPoints1.add(scatter);
        
        
        Chart chart;
        chart = AWTChartComponentFactory.chart(Quality.Nicest, Toolkit.awt.name());
        chart.getView().setBackgroundColor(Color.BLACK);
        chart.getAxeLayout().setMainColor(Color.WHITE);
        //chart.setAxeDisplayed(false);
        
        /*
         * Points from STORM Simulation 
         * 
         */
        STORMObjectParser stParser = new STORMObjectParser();
        try {
			stParser.parse();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("Storm point number: "+stParser.allPoints.size());
        CompileableComposite compPoints = new CompileableComposite();
        for(Point p : stParser.allPoints) {
        	compPoints.add(p);
        }
        
        /*
         * 
         */
        
        if(SHOWSPHERES) {
        	chart.getScene().getGraph().add(sphereList);
        }
        else if(!TRIANGLES){
        	//chart.getScene().getGraph().add(scatter,false);
        	chart.getScene().getGraph().add(compPoints,false);
        }
        chart.render();
                
        // jzy3d does not throw an exception when there is a line with 0 points, but does not load the coordinate system
        for (LineStrip line : lineList) {
        	if(line.getPoints().size() != 0) {
        		chart.getScene().getGraph().add(line);
        	}
        }
       
        int parts = 2;
        //List<CompileableComposite> list1 = new ArrayList<CompileableComposite>();
        CompileableComposite comp = new CompileableComposite();
        for (int part = 0; part < parts; part++) {
            for (int c = (int) (part * trParser.allTriangles.size()/parts); c < (int) ((part* trParser.allTriangles.size() + trParser.allTriangles.size())/parts); c++) {
            	comp.add(trParser.allTriangles.get(c));
            }
//            Shape surface = new Shape(comp);
//            Color factor = new Color(1, 0, 0, 0.0f);
//            surface.setWireframeDisplayed(true);
//            surface.setWireframeWidth(0.01f);
//            surface.setWireframeColor(org.jzy3d.colors.Color.BLACK);
//            surface.setColor(factor);
//            if(TRIANGLES) chart.getScene().getGraph().add(surface,false);
        }
        
        if(FRAMES) {
        	Color factor = new Color(1, 1, 0, 0.0f);
            comp.setColor(factor);
            comp.setWireframeDisplayed(true);
        }
        else {
        	Color factor = new Color(1, 1, 0, 0.65f);
            //comp.setColor(factor);
        	comp.setWireframeDisplayed(false);
        }
        comp.setWireframeColor(Color.BLACK);
        comp.setWireframeWidth(0.00001f);
        comp.setColorMapper(null);
        if(TRIANGLES) chart.getScene().getGraph().add(comp,false);
        chart.render();
        
        if(LIGHTON) {
        	Light light = chart.addLight(new Coord3d(500f, 500f, 2500f));
        	light.setRepresentationRadius(100);
        	light.setAmbiantColor(new Color(1f, 0,0));
        	//light.setDiffuseColor(new Color((255.f/255.f), 0, 0));
        }        
        
        System.out.println("Line list elements: " + lineList.size());
        
        ZoomController cont = new ZoomController();
        chart.addController(cont);
        System.out.println(chart.getControllers());
        System.out.println("Drawing " + lineParser.pointNumber + " points.");
        //View.current().rotate(new Coord2d(100,100), true);
        long stop = System.nanoTime();
        System.out.println("Total startup time: " + (stop-startTime)/1e9 +" s");
		return chart;
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