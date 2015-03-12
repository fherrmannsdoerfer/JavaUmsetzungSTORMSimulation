import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;

import org.jzy3d.plot3d.primitives.Polygon;


public class TriangleObjectParser {
	
	static String FILE = "mito.nff";
	static String FILE_S = "mito_small.nff";	
	public List<Polygon> allTriangles;
	
	public int limit;
	
	public TriangleObjectParser(String path) {
		
	}
	
	public void parse() throws NumberFormatException, IOException {
		long start = System.nanoTime();
        BufferedReader br = new BufferedReader(new FileReader(FILE));
        String line;
        List<String> words = new ArrayList<String>();
        int objectNumber = 0;
        allTriangles  = new ArrayList<Polygon>();
        while ((line = br.readLine()) != null) {
            if (line.contains("pp 3") && objectNumber < limit) {
            	Polygon newTriangle = new Polygon();
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
                    Point newPoint = new Point(new Coord3d(Float.parseFloat(coordinates.get(0)), Float.parseFloat(coordinates.get(1)), Float.parseFloat(coordinates.get(2))), Color.BLACK);
                    newTriangle.add(newPoint);
                    if (i != 2) {
                    	line = br.readLine();
                    }
            	}
            	allTriangles.add(newTriangle);
            }
        }
        br.close();
        long time = System.nanoTime() - start;
        System.out.println("Took " + time / 1e9 + "s to parse and save triangles");
        System.out.println("Number of tr.: " + objectNumber);
	}
}
