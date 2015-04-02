package parsing;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;


public class TriangleObjectParser {
	
	static String FILE = "mito.nff";
	static String FILE_S = "mito_small.nff";	
	public List<Polygon> allTriangles;
	public List<float[][]> primitives;
	public int limit;
	public String path;
	
	public TriangleObjectParser(String path) {
		this.path = path;
		if(path == null) {
			this.path = FILE;
		}
	}
	
	public void parse() throws NumberFormatException, IOException {
		long start = System.nanoTime();
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        List<String> words = new ArrayList<String>();
        int objectNumber = 0;
        boolean all = true;
        allTriangles  = new ArrayList<Polygon>();
        primitives = new ArrayList<float[][]>();
        while ((line = br.readLine()) != null) {
    		if(limit == 0) {
    			all = true;
    		}
    		else {
    			all = objectNumber < limit;
    		}
            if (line.contains("pp 3") && all) {
            	Polygon newTriangle = newTriangle();
            	float[][] primTR = new float[3][3];
            	objectNumber++;
            	line = br.readLine();
            	//System.out.println("Current line: " + objectNumber);
            	for (int i = 0; i < 3; i++) {
            		words.clear();
            		//System.out.println("Current line: " + currentNumberLine);
            		int pos = 0,end;
                	//System.out.println(line);
                	//printMatches(line, "\\d+\\.*\\d+");
                	//line = line.replaceFirst("\\s+", "");
                	//System.out.println(line);
                	            	
                    while ((end = line.indexOf(' ', pos)) >= 0) {
                        words.add(line.substring(pos,end));
                        pos = end + 1;
                    }
                    //System.out.println(words);
                    List<String> coordinates = new ArrayList<String>();
                    for(String s : words) {
                    	if(s.isEmpty()) {
                    		continue;
                    	}
                    	else {
                    		//System.out.println(s);
                    		coordinates.add(s);
                    	}
                    }
                    Coord3d saveCoord = new Coord3d(Float.parseFloat(coordinates.get(0)), Float.parseFloat(coordinates.get(1)), Float.parseFloat(coordinates.get(2)));
                    Color color = new Color(saveCoord.x/255.f, saveCoord.y/255.f, 1-saveCoord.z/255.f, 1.f);
                    Point newPoint = new Point(saveCoord, color);
                    newTriangle.add(newPoint);
                    primTR[i][0] = saveCoord.x;
                    primTR[i][1] = saveCoord.y;
                    primTR[i][2] = saveCoord.z;
                    if (i != 2) {
                    	line = br.readLine();
                    }
            	}
            	//newTriangle.setWireframeColor(Color.BLACK);
            	allTriangles.add(newTriangle);
            	primitives.add(primTR);
            }
        }
        br.close();
        long time = System.nanoTime() - start;
        System.out.println("Took " + time / 1e9 + "s to parse and save triangles");
        System.out.println("Number of tr.: " + objectNumber);
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
