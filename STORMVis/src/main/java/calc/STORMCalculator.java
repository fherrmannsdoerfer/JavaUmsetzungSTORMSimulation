package calc;


import gui.ThreadCompleteListener;
import gui.DataTypeDetector.DataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.SwingWorker;

import model.DataSet;
import model.EpitopeDataSet;
import model.LineDataSet;
import model.ParameterSet;
import model.TriangleDataSet;

import org.javatuples.Pair;

/**
 * 
 * @brief Complete STORM simulation management
 *
 */
public class STORMCalculator extends SwingWorker<Void, Void>{

    List<float[][]> trList;
    
    DataSet currentDataSet;
    public Random random;
    public boolean isRunning = true;
	
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
		currentDataSet.isCalculating = true;
		long start = System.nanoTime();
		if(currentDataSet != null) {
			doSimulation();
		}
		System.out.println("Whole converting and simulation time: "+ (System.nanoTime()-start)/1e9 +"s");
		System.out.println("-------------------------------------");
		currentDataSet.isCalculating = false;
		return null;
	}
	@Override
	public void done(){
		System.out.println("Worker finished");
		currentDataSet.getProgressBar().setString("Calculation Done!");
		currentDataSet.isCalculating = false;
		notifyListeners();
	}
	
	  private  Set<ThreadCompleteListener> listeners
      = new CopyOnWriteArraySet<ThreadCompleteListener>();
	public  void addListener(final ThreadCompleteListener listener) {
		listeners.add(listener);
	}
	public  void removeListener(final ThreadCompleteListener listener) {
		listeners.remove(listener);
	}
	
	public  void notifyListeners() {
		for (ThreadCompleteListener listener : listeners) {
			listener.notifyOfThreadComplete(this);
		}
	}
	
	public void doSimulation() {
		float[][] ep = null; 
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
			
			
		}
		else if(currentDataSet.dataType == DataType.EPITOPES){
			float[][] ep2 = new float[((EpitopeDataSet)currentDataSet).epitopeEnd.length][3];
			for (int i = 0; i<((EpitopeDataSet)currentDataSet).epitopeEnd.length;i++){
				ep2[i] = ((EpitopeDataSet)currentDataSet).epitopeEnd[i].clone();
			}
			//ep = ((EpitopeDataSet)currentDataSet).epitopeEnd.clone();
			ap = ((EpitopeDataSet)currentDataSet).epitopeBase;
			ParameterSet ps = currentDataSet.parameterSet;
			List<float[]> endPoints = new ArrayList<float[]>();
			List<float[]> startPoints = new ArrayList<float[]>();
			for (int i = 0; i<ep2.length;i++){
				double rn = random.nextDouble();
				if(rn<=ps.getPabs()){
					float[] normTri = new float[3];
					for (int j = 0; j<3; j++){
						normTri[j] = ep2[i][j] - ap[i][j];
					}
					float angleDeviation = (float) (random.nextGaussian()*ps.getSoa()); //same procedure as for triangles
					float alpha =  (float) (random.nextDouble()*2*Math.PI);
					float[] vec = Calc.getVectorLine(ps.getAoa()+angleDeviation,ps.getLoa(),alpha); //a random vector with the set angle is created

					vec = Finder.findRotation(vec,normTri); //the surface normal is rotated 
					double length = Math.sqrt(vec[0]*vec[0]+vec[1]*vec[1]+vec[2]*vec[2]);
					for (int j = 0; j<3; j++){
						ep2[i][j] = (float) (ap[i][j] + vec[j]/length*currentDataSet.parameterSet.getLoa());
					}
					endPoints.add(ep2[i]);
					startPoints.add(ap[i]);
				}		
				else{
					
				}
			}
			ep2 = Calc.toFloatArray(endPoints);
			ap = Calc.toFloatArray(startPoints);
			currentDataSet.antiBodyEndPoints = ep2;
			currentDataSet.antiBodyStartPoints = ap;
		}
		try {
			currentDataSet.stormData = StormPointFinder.findStormPoints(currentDataSet.antiBodyEndPoints, currentDataSet, this);
		}
		catch(Exception e){
			currentDataSet.stormData = null;
		}
		/**
		 * writing results to the current dataset
		 */
		currentDataSet.getProgressBar().setString("Calculation Done!");
	}
	
	public void publicSetProgress(int prog){
		setProgress(prog);
	}


	
}