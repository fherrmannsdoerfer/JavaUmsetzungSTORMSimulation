package common;
import javax.media.opengl.GL;

import org.jzy3d.plot3d.primitives.AbstractComposite;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;


public class Triangle extends AbstractComposite {

	public Triangle() {
		
	}
	
	
	protected Polygon newTriangle() {
		return new Polygon() {
			@Override
            protected void begin(GL gl) {
				if (gl.isGL2()) {
					gl.getGL2().glBegin(GL.GL_TRIANGLES);
				} else {
					GLES2CompatUtils.glBegin(GL.GL_TRIANGLES);
				}
			}

			/**
			 * Override default to use a line strip to draw wire, so that the
			 * shared adjacent triangle side is not drawn.
			 */
			@Override
            protected void callPointForWireframe(GL gl) {
				if (gl.isGL2()) {
					gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b,
							wfcolor.a);
					gl.glLineWidth(wfwidth);

					beginWireWithLineStrip(gl); // <
					for (Point p : points) {
						gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
					}
					end(gl);
				} else {
					GLES2CompatUtils.glColor4f(wfcolor.r, wfcolor.g,
							wfcolor.b, wfcolor.a);
					gl.glLineWidth(wfwidth);

					beginWireWithLineStrip(gl); // <
					for (Point p : points) {
						GLES2CompatUtils.glVertex3f(p.xyz.x, p.xyz.y,
								p.xyz.z);
					}
					end(gl);
				}
			}

			protected void beginWireWithLineStrip(GL gl) {
				if (gl.isGL2()) {
					gl.getGL2().glBegin(GL.GL_LINE_STRIP);
				} else {
					GLES2CompatUtils.glBegin(GL.GL_LINE_STRIP);
				}
			}

		};
	}
}
