import java.awt.BorderLayout;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.DebugGL2;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;


/**
 * A minimal JOGL demo.
 * 
 * @author <a href="mailto:kain@land-of-kain.de">Kai Ruhl</a>
 * @since 26 Feb 2009
 */
public class MyJoglCanvasStep1 extends GLCanvas implements GLEventListener {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The GL unit (helper class). */
    private GLU glu;

    /** The frames per second setting. */
    private int fps = 60;

    /** The OpenGL animator. */
    private FPSAnimator animator;
    
    public static List<org.jzy3d.plot3d.primitives.Polygon> triangles = new ArrayList<org.jzy3d.plot3d.primitives.Polygon>();
    /**
     * A new mini starter.
     * 
     * @param capabilities The GL capabilities.
     * @param width The window width.
     * @param height The window height.
     */
    public MyJoglCanvasStep1(GLCapabilities capabilities, int width, int height) {
        addGLEventListener(this);
    }

    /**
     * @return Some standard GL capabilities (with alpha).
     */
    private static GLCapabilities createGLCapabilities() {
        GLCapabilities capabilities = new GLCapabilities(null);
        capabilities.setRedBits(8);
        capabilities.setBlueBits(8);
        capabilities.setGreenBits(8);
        capabilities.setAlphaBits(8);
        return capabilities;
    }

    /**
     * Sets up the screen.
     * 
     * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
     */
    public void init(GLAutoDrawable drawable) {
        drawable.setGL(new DebugGL2((GL2) drawable.getGL()));
        final GL2 gl = (GL2) drawable.getGL();

        // Enable z- (depth) buffer for hidden surface removal. 
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);

        // Enable smooth shading.
        gl.glShadeModel(GL2.GL_SMOOTH);
        // Define "clear" color.
        gl.glClearColor(0f, 0f, 0f, 0f);

        // We want a nice perspective.
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        // Create GLU.
        glu = new GLU();
        // Start animator.
        animator = new FPSAnimator(this, fps);
        animator.start();
    }

    /**
     * The only method that you should implement by yourself.
     * 
     * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
     */
    public void display(GLAutoDrawable drawable) {
        if (!animator.isAnimating()) {
            return;
        }
        final GL2 gl = (GL2) drawable.getGL();

        // Clear screen.
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
        // Set camera.
        setCamera(gl, glu, 5000);
        
        // Write triangle.
        gl.glColor3f(1.f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        System.out.println(triangles.size());
        for (org.jzy3d.plot3d.primitives.Polygon p : triangles) {
        	//System.out.println("adding");
        	gl.glVertex3f(p.getPoints().get(0).xyz.x, p.getPoints().get(0).xyz.y, p.getPoints().get(0).xyz.z);
        	gl.glVertex3f(p.getPoints().get(1).xyz.x, p.getPoints().get(1).xyz.y, p.getPoints().get(1).xyz.z);
        	gl.glVertex3f(p.getPoints().get(2).xyz.x, p.getPoints().get(2).xyz.y, p.getPoints().get(2).xyz.z);
        }
        gl.glEnd();  
        //animator.stop();
    }

    /**
     * Resizes the screen.
     * 
     * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable,
     *      int, int, int, int)
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        final GL gl = drawable.getGL();
        gl.glViewport(0, 0, width, height);
    }

    /**
     * Changing devices is not supported.
     * 
     * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable,
     *      boolean, boolean)
     */
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
        throw new UnsupportedOperationException("Changing display is not supported.");
    }

    /**
     * @param gl The GL context.
     * @param glu The GL unit.
     * @param distance The distance from the screen.
     */
    private void setCamera(GL2 gl, GLU glu, float distance) {
        // Change to projection matrix.
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        // Perspective.
        float widthHeightRatio = (float) getWidth() / (float) getHeight();
        glu.gluPerspective(45, widthHeightRatio, 100, 1000);
        //glu.gluLookAt(100, 0, distance, 0, 0, 0, 0, 1, 0);
        glu.gluLookAt(250.f, 1500.f, 100.f, 
        				450.f, 1218.f, 85.f, 
        				100.f, 1.f, 0.f);
        // Change back to model view matrix.
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        //gl.glRotatef((float)-10.f, 1.f, 0.f, 0.f);
        //gl.glRotatef((float)Math.PI/3.f, 0.f, 1.f, 0.f);
        //gl.glRotatef((float)Math.PI/3.f, 0.f, 0.f, 1.f);
    }

    /**
     * Starts the JOGL mini demo.
     * 
     * @param args Command line args.
     */
    public final static void main(String[] args) {
    	
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
    	
    	triangles = trParser.allTriangles;
        GLCapabilities capabilities = createGLCapabilities();
        MyJoglCanvasStep1 canvas = new MyJoglCanvasStep1(capabilities, 800, 500);
        JFrame frame = new JFrame("Mini JOGL Demo (breed)");
        frame.getContentPane().add(canvas, BorderLayout.CENTER);
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        canvas.requestFocus();
    }

	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

}