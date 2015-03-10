import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LineObjectParser {
	
	static String FILE = "mito.nff";
	static String FILE2 = "Microtubules.wimp";
	static String FILE3 = "Microtubules_large.wimp";
	//static String REGEX = "\\s+ \\d+ \\s+\\d+\\.*\\d+.*";
	static String REGEX = "\\s+\\d+\\s+\\d+\\.*\\d+.*";
	
	List<ArrayList<Point>> allObjects = new ArrayList<ArrayList<Point>>();
		
	public LineObjectParser(String path) {
		
	}
	
	public void parse() throws IOException {
		long start = System.nanoTime();
        BufferedReader br = new BufferedReader(new FileReader(FILE3));
        String line;
        List<String> words = new ArrayList<String>();
        int objectNumber = 0;
        jregex.Pattern pattern = new jregex.Pattern(REGEX);
        ArrayList<Point> currentObject = new ArrayList<Point>();
        while ((line = br.readLine()) != null) {
            words.clear();
            jregex.Matcher m = pattern.matcher(line);
            if (line.contains("Object #:")) {
            	objectNumber++;
            	//if(objectNumber!= 0) {
            		allObjects.add(currentObject);
            		currentObject = new ArrayList<Point>();
            	//}
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
                
                Point p = new Point(Float.parseFloat(coordinates.get(0)),Float.parseFloat(coordinates.get(1)),Float.parseFloat(coordinates.get(2)));
                currentObject.add(p);
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
        br.close();
        long time = System.nanoTime() - start;
        System.out.printf("Took %f seconds to read lines and break using indexOf%n", time / 1e9);
        System.out.println("Number of objects: "+ objectNumber);
        System.out.println("Number of objects in Array: " + allObjects.size());
        System.out.println(allObjects);
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
