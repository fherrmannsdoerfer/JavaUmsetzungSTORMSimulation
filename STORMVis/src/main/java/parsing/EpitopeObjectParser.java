package parsing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import model.EpitopeDataSet;
import model.ParameterSet;
import model.PointsOnlyDataSet;

public class EpitopeObjectParser {
		static String REGEX = "\\s+\\d+\\s+\\d+\\.*\\d+.*";
		
		public ArrayList<float[]> allPoints = new ArrayList<float[]>();
		public int objectNumber = 0;
		public int pointNumber = 0;
		
		public String path;
		InputStream is = null;
		BufferedReader br;
		/**
		 * 
		 * @param path - abs. file path
		 */
		public EpitopeObjectParser(String path) {
			this.path = path;
		}
		
		/**
		 * Parses the selected file and puts the results into class field.
		 * @throws IOException
		 */
		public ArrayList<float[]> parse() throws IOException {
			return parse(path);
		}
		
		public ArrayList<float[]> parse(String path) throws IOException {
			long start = System.nanoTime();
			try {
				br = new BufferedReader(new FileReader(path));
			}
			catch(Exception e){
				is = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
			    br = new BufferedReader(new InputStreamReader(is));
			}
	        String line;
	        List<String> words = new ArrayList<String>();
	        jregex.Pattern pattern = new jregex.Pattern(REGEX);
	       
	        line = br.readLine(); //skip header
	        while ((line = br.readLine()) != null) {
	            words.clear();
	            jregex.Matcher m = pattern.matcher(line);
	           
	            
            	int pos = 0,end;
            
                while ((end = line.indexOf(' ', pos)) >= 0) {
                    words.add(line.substring(pos,end));
                    pos = end + 1;
                }
                words.add(line.substring(pos));
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
                
                float[] point =new float[6];
               
                for (int i = 0; i<6; i++){
                	point[i] = Float.parseFloat(coordinates.get(i));
                }
                allPoints.add(point);
               
         
	        }
	        br.close();
	        long time = System.nanoTime() - start;
	        System.out.printf("Took %f seconds to read lines", time / 1e9);
	        System.out.println("Number of objects: "+ objectNumber);
	        System.out.println("Number of objects in Array: " + allPoints.size());
	        return allPoints;
		}
		
		/**
		 * Creates a PointsOnlyDataSet from the parsed objetcs
		 * @return
		 */
		public EpitopeDataSet wrapParsedObjectsToPointDataSet() {
			EpitopeDataSet set = new EpitopeDataSet(new ParameterSet());
			float[][] ap = new float[allPoints.size()][3];
			float[][] ep = new float[allPoints.size()][3];
			for (int i = 0; i<allPoints.size(); i++){
				for (int j = 0; j<3; j++){
					ap[i][j] = allPoints.get(i)[j];
					ep[i][j] = allPoints.get(i)[j] + allPoints.get(i)[j+3];
				}
			}
			set.epitopeEnd = ep;
			set.epitopeBase = ap;
			return set;
		}
		
}
