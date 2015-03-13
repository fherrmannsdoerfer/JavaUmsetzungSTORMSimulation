import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;


public class STORMObjectParser {
	
	public List<Point> allPoints;
	public static String FILE = "out.txt";
	
	public STORMObjectParser() {
		
	}
	
	public void parse() throws NumberFormatException, IOException {
		long start = System.nanoTime();
        BufferedReader br = new BufferedReader(new FileReader(FILE));
        String line;
        List<String> words = new ArrayList<String>();
        int objectNumber = 0;
        allPoints  = new ArrayList<Point>();
        while ((line = br.readLine()) != null) {
        	words.clear();
        	int pos = 0,end;
        	while ((end = line.indexOf(' ', pos)) >= 0) {
                words.add(line.substring(pos,end));
                pos = end + 1;
            }
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
            allPoints.add(newPoint);
        }
        br.close();
        long time = System.nanoTime() - start;
        System.out.println("Took " + time / 1e9 + "s to parse and save STORM points");
        System.out.println("Number of tr.: " + objectNumber);
	}
}
