import java.io.IOException;

import org.jzy3d.analysis.AnalysisLauncher;


public class Test {

	/**
	 * @param args
	 * @throws Exception 
	 */
	
	
	public static void main(String[] args) throws Exception {
//		LineObjectParser lineParser = new LineObjectParser(null);
//		lineParser.parse();
		AnalysisLauncher.open(new ScatterDemo());
		
//		TriangleObjectParser trParser = new TriangleObjectParser(null);
//		trParser.parse();
     }

}
