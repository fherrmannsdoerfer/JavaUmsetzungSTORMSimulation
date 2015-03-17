import org.jzy3d.bridge.swing.FrameSwing;

import calc.Calc;
import calc.STORMCalculator;


public class Test extends FrameSwing {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param args
	 * @throws Exception 
	 */
	
	static String FILE2 = "Microtubules.wimp";
	static String FILE3 = "Microtubules_large.wimp";
	
	public Test() {

//        initUI();
    }
	
	
	
	public static void main(String[] args) throws Exception {
//		LineObjectParser lineParser = new LineObjectParser(FILE3);
//		lineParser.parse();
//		AnalysisLauncher.open(new ScatterDemo());
//		TriangleObjectParser trParser = new TriangleObjectParser(null);
//		trParser.parse();
//	        
//                Test ex = new Test();
//                ex.setVisible(true);
//		float[][] m = {{2,0,1},{0,2,0},{0,0,1}};
//		float[] vec = {1,3,1};
//		Calc.printVector(Calc.applyMatrix(m, vec));
		STORMCalculator calc = new STORMCalculator();
		calc.startCalculation();
     }
	
//	private void initUI() {
//        setTitle("Simple example");
//        setSize(300, 200);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        
//		ScatterDemo demo = new ScatterDemo();
//        
//		Chart chart = demo.createChart();
//		add((JComponent)chart.getCanvas(), BorderLayout.CENTER);
//    }

}
