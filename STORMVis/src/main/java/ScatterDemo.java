import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Scale;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;

public class ScatterDemo extends AbstractAnalysis{
	
		
	public void init() throws IOException{
		LineObjectParser lineParser = new LineObjectParser(null);
		lineParser.parse();
        
//		int size = 1000000;
//        float x;
//        float y;
//        float z;
//        float a;
//        
//        Coord3d[] points = new Coord3d[size];
//        Color[]   colors = new Color[size];
//        
//        Random r = new Random();
//        r.setSeed(0);
//        
//        
//        for(int i=0; i<size; i++){
//            x = r.nextFloat() - 0.5f;
//            y = r.nextFloat() - 0.5f;
//            z = r.nextFloat() - 0.5f;
//            points[i] = new Coord3d(x, y, z);
//            a = 0.25f;
//            colors[i] = new Color(x, y, z, a);
//        }
//      
		Coord3d[] points = new Coord3d[lineParser.pointNumber];
        Color[] colors = new Color[lineParser.pointNumber];
		int i = 0;
        for(ArrayList<Coord3d> obj : lineParser.allObjects) {
        	for(Coord3d coord : obj) {
        		points[i] = coord;
        		float a = 1.f;
        		colors[i] = new Color(coord.x/255.f,coord.y/255.f,coord.z/255.f,a);
        		i++;
        	}
        }
        
        
        Scatter scatter = new Scatter(points, colors, 5.f);
        chart = AWTChartComponentFactory.chart(Quality.Nicest, "awt");
        chart.getScene().add(scatter);
       
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
}