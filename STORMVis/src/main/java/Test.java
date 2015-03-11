import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jzy3d.chart.Chart;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	
	public static void main(String[] args) throws IOException {
		LineObjectParser lineParser = new LineObjectParser(null);
		lineParser.parse();
		
		TriangleObjectParser trParser = new TriangleObjectParser(null);
		trParser.parse();
     }

}
