package calc;


import gui.DataTypeDetector.DataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
    public Random random;
	
	public STORMCalculator(DataSet set, Random random) {
		this.currentDataSet = set;
		this.random = random;
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
		currentDataSet.getProgressBar().setString("Calculation Done!");
	}
	public void doSimulation() {
		int nbrPores = 1000;
		float[][] epPore = {{570,501,100},{530,501,100},{501,529,100},{500,571,100}, {529,600,100},{571,600,100}, {600,571,100},{600,529,100}};
		float[][] apPore = {{570,501,100},{530,501,100},{501,529,100},{500,571,100}, {529,600,100},{571,600,100}, {600,571,100},{600,529,100}};
		ArrayList<Double> shiftX = new ArrayList<Double>();
		ArrayList<Double> shiftY = new ArrayList<Double>();
		
		for ( int i = 0 ; i<nbrPores;i++){
			shiftX.add(random.nextDouble()*16000);
			shiftY.add(random.nextDouble()*16000);
		}
		float meanX = 0;
		float meanY = 0;
		float meanZ= 0;
		for (int i = 0; i<epPore.length;i++){
			meanX+= epPore[i][0];
			meanY+= epPore[i][1];
			meanZ+= epPore[i][2];
		}
		meanX /= 8;
		meanY /= 8;
		meanZ /= 8;
		
		for (int i = 0; i<epPore.length;i++){
			epPore[i][0]-=meanX;
			epPore[i][1]-=meanY;
			epPore[i][2]-=meanZ;
		}
		ArrayList<ArrayList<Float>> epal = new ArrayList<ArrayList<Float>>();//float[nbrPores][3];
		for (int i = 0;i<shiftX.size();i++){
			double phi = random.nextDouble()*2*Math.PI;
			int vielfaches =4;
			for (int j=0;j<8;j++){
				for (int kk = 0;kk<vielfaches;kk++){
					ArrayList<Float> tmp = new ArrayList<Float>();
					tmp.add((float) ((Math.cos(phi)*epPore[j][0]+Math.sin(phi)*epPore[j][1])+shiftX.get(i)));
					tmp.add((float) ((-Math.sin(phi)*epPore[j][0]+Math.cos(phi)*epPore[j][1])+shiftY.get(i)));
					tmp.add((float) random.nextDouble()*1000);
					double rand = random.nextDouble();
					if (rand < currentDataSet.getParameterSet().getPabs()){
						epal.add(tmp);
					}
				}
			}
		}
		float [][] ep = new float[epal.size()][3];
		float [][] ap = new float[epal.size()][3];
		for (int i=0;i<epal.size();i++){
			for (int j=0;j<3;j++){	
				ep[i][j] = epal.get(i).get(j);
				ap[i][j] = epal.get(i).get(j);
			}
		}
		
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
	
	public void publicSetProgress(int prog){
		setProgress(prog);
	}
	
}