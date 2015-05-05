package calc;


import gui.DataTypeDetector.DataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

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
public class STORMCalculator extends SwingWorker<Void, Void>{

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


	@Override
	public Void doInBackground() {
		long start = System.nanoTime();
		if(currentDataSet != null) {
			doSimulation();
		}
		System.out.println("Whole converting and simulation time: "+ (System.nanoTime()-start)/1e9 +"s");
		System.out.println("-------------------------------------");
		return null;
	}
	@Override
	public void done(){
		System.out.println("Worker finished");
	}
	public void doSimulation() {
		float[][] ep = null; // = {{2,3,0},{3,2,0},{2,2,2},{4,6,8},{1,2,43}};
		float[][] ap = null;
		if(currentDataSet.dataType == DataType.TRIANGLES) {
			TriangleDataSet currentTrs = (TriangleDataSet) currentDataSet;
			Pair<float[][],float[][]> p = Finder.findAntibodiesTri(currentTrs.primitives, currentDataSet, this);
			ep = p.getValue1(); 
			ap = p.getValue0();
			float [][] epCopy = new float[ep.length][];
			float [][] apCopy = new float[ap.length][];
			for(int i = 0; i< ep.length; i++){
				epCopy[i] = ep[i].clone();
				apCopy[i] = ap[i].clone();
			}
			currentDataSet.antiBodyEndPoints = epCopy;
			currentDataSet.antiBodyStartPoints = apCopy;
			float[][] result = StormPointFinder.findStormPoints(ep, currentDataSet, this);
			/**
			 * writing results to the current dataset
			 */
			currentDataSet.stormData = result;
		}
		else if(currentDataSet.dataType == DataType.LINES) {
			LineDataSet currentLines = (LineDataSet) currentDataSet;
			Pair<float[][],float[][]> p = Finder.findAntibodiesLines(currentLines.data, currentDataSet, this);
			ap = p.getValue0();
			ep = p.getValue1();
			float [][] epCopy = new float[ep.length][];
			float [][] apCopy = new float[ap.length][];
			for(int i = 0; i< ep.length; i++){
				epCopy[i] = ep[i].clone();
				apCopy[i] = ap[i].clone();
			}
			currentDataSet.antiBodyEndPoints = epCopy;
			currentDataSet.antiBodyStartPoints = apCopy;
			float[][] result = StormPointFinder.findStormPoints(ep, currentDataSet, this);
			/**
			 * writing results to the current dataset
			 */
			currentDataSet.stormData = result;
		}
	}
	
	public void publicSetProgress(int prog){
		setProgress(prog);
	}
	
}