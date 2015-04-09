package calc;


import gui.DataTypeDetector.DataType;

import java.io.IOException;
import java.util.List;

import model.DataSet;
import model.TriangleDataSet;

import org.javatuples.Pair;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;

import parsing.TriangleObjectParser;
import common.ScatterDemo;

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
			System.out.println("Antibodies found");
			System.out.println("ep length:" + ep.length);
			Coord3d[] allObjects = new Coord3d[ep.length];
			Color[] colors = new Color[ep.length];
			for (int i = 0; i < ep.length; i++) {
				Coord3d coord = new Coord3d(ep[i][0], ep[i][1], ep[i][2]);
				allObjects[i] = coord;
				colors[i] = new Color(coord.x/255.f, coord.y/255.f, coord.z/255.f, 1.f);
			}
			//		ScatterDemo demo = new ScatterDemo();
			//		demo.stormColors = colors;
			//		demo.stormPoints = allObjects;
			//		demo.STORM = true;
			//		Coord3d vp = demo.getChart().getViewPoint();
			//		AnalysisLauncher.open(demo);

			//Calc.print2dMatrix(ep);
			float[][] result = StormPointFinder.findStormPoints(ep, currentDataSet.getParameterSet().abpf, currentDataSet.getParameterSet().sxy, currentDataSet.getParameterSet().sz, currentDataSet.getParameterSet().bd, currentDataSet.getParameterSet().fpab, true, currentDataSet.getParameterSet().psfwidth, currentDataSet.getParameterSet().ilpmm3, currentDataSet.getParameterSet().mergedPSF);
			System.out.println("result length: "+ result.length);
			Coord3d[] allObjects2 = new Coord3d[result.length];
			Color[] colors2 = new Color[result.length];
			for (int i = 0; i < result.length; i++) {
				Coord3d coord = new Coord3d(result[i][0], result[i][1], result[i][2]);
				allObjects2[i] = coord;
				colors2[i] = new Color(coord.x/255.f, coord.y/255.f, coord.z/255.f, 1.f);
			}
			ScatterDemo demo2 = new ScatterDemo();
			demo2.stormColors = colors2;
			demo2.stormPoints = allObjects2;
			demo2.STORM = true;
			
			/**
			 * writing results to the current dataset
			 */
			currentDataSet.antiBodyEndPoints = ep;
			currentDataSet.antiBodyStartPoints = ap;
			currentDataSet.stormData = result;
		}
		else {
			return;
		}
	}
}
