import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;


public class STORMObjectParser {
	
	
	
	public void parse() throws NumberFormatException, IOException {
		long start = System.nanoTime();
        BufferedReader br = new BufferedReader(new FileReader(FILE));
        String line;
        List<String> words = new ArrayList<String>();
        int objectNumber = 0;
        boolean all = false;
        allTriangles  = new ArrayList<Polygon>();
        while ((line = br.readLine()) != null) {
    		
        }
        br.close();
        long time = System.nanoTime() - start;
        System.out.println("Took " + time / 1e9 + "s to parse and save triangles");
        System.out.println("Number of tr.: " + objectNumber);
	}
}
