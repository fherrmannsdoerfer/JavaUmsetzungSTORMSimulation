package parsing;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.LineDataSet;
import model.ParameterSet;

import org.apache.commons.io.FileUtils;
import org.jzy3d.maths.Coord3d;


public class LineObjectParser {
	
	//static String REGEX = "\\s+ \\d+ \\s+\\d+\\.*\\d+.*";
	static String REGEX = "\\s+\\d+\\s+\\d+\\.*\\d+.*";
	
	public List<ArrayList<Coord3d>> allObjects = new ArrayList<ArrayList<Coord3d>>();
	public int objectNumber = 0;
	public int pointNumber = 0;
	
	public String path;
	InputStream is = null;
	BufferedReader br;
	/**
	 * 
	 * @param path - abs. file path
	 */
	public LineObjectParser(String path) {
		this.path = path;
	}
	
	/**
	 * Parses the selected file and puts the results into class field.
	 * @throws IOException
	 */
	public List<ArrayList<Coord3d>> parse() throws IOException {
		return parse(path);
	}
	
	public List<ArrayList<Coord3d>> parse(String path) throws IOException {
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
        ArrayList<Coord3d> currentObject = new ArrayList<Coord3d>();
        while ((line = br.readLine()) != null) {
            words.clear();
            jregex.Matcher m = pattern.matcher(line);
            if (line.contains("Object #:")) {
            	if(objectNumber!= 0) {
            		allObjects.add(currentObject);
            		currentObject = new ArrayList<Coord3d>();
            	}
            	objectNumber++;
            	continue;
            }
            else if (objectNumber > 0 && m.matches()) {
            	int pos = 0,end;
            	//System.out.println(line);
            	//printMatches(line, "\\d+\\.*\\d+");
            	//line = line.replaceFirst("\\s+", "");
            	//System.out.println(line);
            	            	
                while ((end = line.indexOf(' ', pos)) >= 0) {
                    words.add(line.substring(pos,end));
                    pos = end + 1;
                }
                
//                for(int i = 0; i<line.length(); i++) {
//                	end = line.indexOf(' ', i);
//                	words.add(line.substring(i,end));
//                    i = end+1;
//                }
                // strange failure with for loop
                
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
                if (coordinates.size() > 3) {
        	    	coordinates.remove(0);
        	    }
                
                //Point p = new Point(Float.parseFloat(coordinates.get(0)),Float.parseFloat(coordinates.get(1)),Float.parseFloat(coordinates.get(2)));
                Coord3d p = new Coord3d(Float.parseFloat(coordinates.get(0)),Float.parseFloat(coordinates.get(1)),Float.parseFloat(coordinates.get(2)));
                currentObject.add(p);
                pointNumber++;
//                for (int i = 0; i < 3;i++) {
//                	System.out.println(point[i]);
//                }
                //System.out.println(coordinates);
            }
            else {
            	continue;
            }
            // words.
            //System.out.println(words);
        }
        allObjects.add(currentObject);
        br.close();
        long time = System.nanoTime() - start;
        System.out.printf("Took %f seconds to read lines", time / 1e9);
        System.out.println("Number of objects: "+ objectNumber);
        System.out.println("Number of objects in Array: " + allObjects.size());
        return allObjects;
	}
	
	/**
	 * Creates a LineDataSet from the parsed objetcs
	 * @return
	 */
	public LineDataSet wrapParsedObjectsToLineDataSet() {
		LineDataSet set = new LineDataSet(new ParameterSet());
		set.data = allObjects;
		set.pointNumber = new Integer(pointNumber);
		set.objectNumber = new Integer(objectNumber);
		return set;
	}
	
	public static void printMatches(String text, String regex) {
	    Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(text);
	    // Check all occurrences
	    List<String> coordinates = new ArrayList<String>();
	    while (matcher.find()) {
	        //System.out.print("Start index: " + matcher.start());
	        //System.out.print(" End index: " + matcher.end());
	        //System.out.println(" Found: " + matcher.group());
	    	coordinates.add(matcher.group());
	    }
	    if (coordinates.size() > 3) {
	    	coordinates.remove(0);
	    }
	    //System.out.println(coordinates);
	    //System.out.println("\n");
	}
}
