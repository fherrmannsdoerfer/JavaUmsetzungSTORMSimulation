package playground;

import java.awt.event.MouseWheelEvent;

import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Scale;
import org.jzy3d.plot3d.rendering.view.View;

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

//        double center = (currScale.getMax() + currScale.getMin())/2; 
//        double min   = center + (currScale.getMin()-center) * (factor); 
//        double max   = center + (currScale.getMax()-center) * (factor); 
//        Scale scale = null; 
//        if(min<max) { 
//                scale = new org.jzy3d.maths.Scale(min, max); 
//        } 
//        else{ 
//                if(factor<1) 
//                        scale = new org.jzy3d.maths.Scale(center, center); 
//        } 
        BoundingBox3d bounds = View.current().getBounds(); 
//        System.out.println(bounds);
        BoundingBox3d newBounds = new BoundingBox3d(bounds.getXmin()*factor, bounds.getXmax()*factor, bounds.getYmin()*factor, bounds.getYmax()*factor, bounds.getZmin()*factor, bounds.getZmax()*factor);
//        bounds.setZmin((float)scale.getMin()); 
//        bounds.setZmax((float)scale.getMax()); 
//        bounds.setYmin((float)scale.getMin()); 
//        bounds.setYmax((float)scale.getMax()); 
//        bounds.setXmin((float)scale.getMin()); 
//        bounds.setXmax((float)scale.getMax()); 
        View.current().setBoundManual(newBounds);
        View.current().shoot(); 
	}
}