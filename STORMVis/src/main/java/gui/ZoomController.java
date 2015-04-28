package gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.jzy3d.chart.controllers.mouse.AWTMouseUtilities;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Scale;
import org.jzy3d.plot3d.rendering.view.View;

public class ZoomController extends AWTCameraMouseController {
	public ZoomController() {
		super();
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		Coord2d mouse = new Coord2d(e.getX(), e.getY());

		// Rotate
		if (AWTMouseUtilities.isLeftDown(e)) {
			Coord2d move = mouse.sub(prevMouse).div(100);
			rotate(move);
		}
		// Shift
		else if (AWTMouseUtilities.isRightDown(e)) {
			Coord2d move = mouse.sub(prevMouse);
			BoundingBox3d bounds = View.current().getBounds(); 
			Coord3d vp = View.current().getViewPoint();
			float shiftx = (float) (move.x*Math.sin(vp.x));
			float shifty = (float) (-move.x*Math.cos(vp.x));
			float shiftz = (float) (move.y*Math.cos(vp.y));
			float fac = (float)((bounds.getXmax()-bounds.getXmin())/250.);///(bounds.getYmax()-bounds.getYmin())*Math.cos(vp.x));
	        System.out.println(fac);
			BoundingBox3d newBounds = bounds.shift(new Coord3d(fac*shiftx,fac*shifty,fac*shiftz));
	        View.current().setBoundManual(newBounds);
	        //System.out.println(vp.x*180/3.14+" "+vp.y*180/3.14+" "+vp.z);
	        View.current().shoot(); 
		}
		prevMouse = mouse;
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