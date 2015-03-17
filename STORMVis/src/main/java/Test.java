import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JComponent;

import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.bridge.swing.FrameSwing;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Rectangle;

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
