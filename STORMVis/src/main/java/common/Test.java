package common;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.bridge.swing.FrameSwing;

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
		float[][] m = {{1,2,3},{0,1,0},{2,1,0}};
		float[][] m2 = {{0,4,0},{1,2,5},{0,1,1}};
//		Calc.print2dMatrix(Calc.addToLowerTriangle(m2, 100));
		//Calc.print2dMatrix(Calc.transpose(m));
		float[] vec = {1,3};
		float[] vec2 = {1,-3};
//		Calc.printVector(Calc.difference(vec, vec2));
//		System.out.println("norm: " + Calc.getNorm(vec));
//		Calc.printVector(Calc.applyMatrix(m, vec));
		//Calc.print2dMatrix(Calc.matrixAddition(m, m2));
		
		float[][] mx = {{1,0},{2,2},{3,1}};
//		Calc.print2dMatrix(Calc.pairwiseDistance(mx, mx));
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
