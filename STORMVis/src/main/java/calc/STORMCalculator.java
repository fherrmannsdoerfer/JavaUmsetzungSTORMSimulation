package calc;


import gui.DataTypeDetector.DataType;

import java.util.List;

import model.DataSet;
import model.LineDataSet;
import model.TriangleDataSet;

import org.javatuples.Pair;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;

import common.ScatterDemo;

/**
 * 
 * @brief Complete STORM simulation management
 *
 */
public class STORMCalculator {

    List<float[][]> trList;
    
    DataSet currentDataSet;
	
	public STORMCalculator(DataSet set) {
		this.currentDataSet = set;
	}
	
	
	public DataSet getCurrentDataSet() {
		return currentDataSet;
	}



	public void setCurrentDataSet(DataSet currentDataSet) {
		this.currentDataSet = currentDataSet;
	}



	public void startCalculation() {
		long start = System.nanoTime();
//		String path = "/Users/maximilianscheurer/ex.nff";
//		TriangleObjectParser trParser = new TriangleObjectParser(path);
//		trParser.limit = 0;
//		try {
//			trParser.parse();
//		} catch (NumberFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		trList = trParser.primitives;
		if(currentDataSet != null) {
			doSimulation();
		}
		System.out.println("Whole converting and simulation time: "+ (System.nanoTime()-start)/1e9 +"s");
		System.out.println("-------------------------------------");
	}
	
	public void doSimulation() {
		float[][] ep = null;
		float[][] ap = null;
		if(currentDataSet.dataType == DataType.TRIANGLES) {
			TriangleDataSet currentTrs = (TriangleDataSet) currentDataSet;
			Pair<float[][],float[][]> p = Finder.findAntibodiesTri(currentTrs.primitives, currentDataSet.getParameterSet().bspsnm, currentDataSet.getParameterSet().pabs, currentDataSet.getParameterSet().loa, currentDataSet.getParameterSet().aoa, currentDataSet.getParameterSet().doc, currentDataSet.getParameterSet().nocpsmm, currentDataSet.getParameterSet().docpsnm);
			ep = p.getValue1();
			ap = p.getValue0();
			float[][] result = StormPointFinder.findStormPoints(ep, currentDataSet.getParameterSet().abpf, currentDataSet.getParameterSet().sxy, currentDataSet.getParameterSet().sz, currentDataSet.getParameterSet().bd, currentDataSet.getParameterSet().fpab, true, currentDataSet.getParameterSet().psfwidth, currentDataSet.getParameterSet().ilpmm3, currentDataSet.getParameterSet().mergedPSF);
			/**
			 * writing results to the current dataset
			 */
			currentDataSet.antiBodyEndPoints = ep;
			currentDataSet.antiBodyStartPoints = ap;
			currentDataSet.stormData = result;
		}
		else if(currentDataSet.dataType == DataType.LINES) {
			LineDataSet currentLines = (LineDataSet) currentDataSet;
			
			Pair<float[][],float[][]> p = Finder.findLines(currentLines.data, currentDataSet.getParameterSet().bspnm, currentDataSet.getParameterSet().pabs, currentDataSet.getParameterSet().aoa, currentDataSet.getParameterSet().loa, currentDataSet.getParameterSet().rof);
			ap = p.getValue0();
			ep = p.getValue1();
			float[][] result = StormPointFinder.findStormPoints(ep, currentDataSet.getParameterSet().abpf, currentDataSet.getParameterSet().sxy, currentDataSet.getParameterSet().sz, currentDataSet.getParameterSet().bd, currentDataSet.getParameterSet().fpab, true, currentDataSet.getParameterSet().psfwidth, currentDataSet.getParameterSet().ilpmm3, currentDataSet.getParameterSet().mergedPSF);
			currentDataSet.antiBodyEndPoints = ep;
			currentDataSet.antiBodyStartPoints = ap;
			currentDataSet.stormData = result;
		}
	}
}
