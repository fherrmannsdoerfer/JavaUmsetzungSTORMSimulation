package batchProcessing;

import gui.DataTypeDetector;
import gui.ExamplesProvidingClass;
import gui.ParserWrapper;
import gui.DataTypeDetector.DataType;
import ij.gui.GUI;
import inout.FileManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JProgressBar;

import calc.Calc;
import calc.STORMCalculator;
import model.DataSet;
import model.LineDataSet;
import model.TriangleDataSet;

public class startBatchProcessing {
	static List<DataSet> allDataSets = new ArrayList<DataSet>();
	private static Random random;
	private static String EXTENSIONIMAGEOUTPUT = ".tif";
	private static String outputFolder = "Y:\\Users_shared\\SuReSim-Software Project\\SuReSim Rebuttal\\Fire\\141107-Microtubules-Nachgezeichnet\\SuReSimResult\\";
	
	public static void main(String[] args) {
		File file = new File("Y:\\Users_shared\\SuReSim-Software Project\\SuReSim Rebuttal\\Fire\\141107-Microtubules-Nachgezeichnet\\141107-MT-Modelrescaled1d.wimp");
		proceedFileImport(file);
//		DataSet data = ExamplesProvidingClass.getDataset(1);
//		furtherProceedFileImport(data, data.dataType);
		
		SimulationParameter params = standardParameterActin();
		
		
		ArrayList<Float> sigmaXY = new ArrayList<Float>(Arrays.asList(4.f,8.f,12.f,25.f));
		ArrayList<Float> sigmaZ = new ArrayList<Float>(Arrays.asList(8.f,30.f,40.f,50.f));
		ArrayList<Float> le = new ArrayList<Float>(Arrays.asList(10.f,50.f,100.f));
		ArrayList<Float> de = new ArrayList<Float>(Arrays.asList(10.f,20.f,50.f,100.f));
		ArrayList<Integer> koff = new ArrayList<Integer>(Arrays.asList(500,1000,2000,5000,10000));
		ArrayList<Integer> frames = new ArrayList<Integer>(Arrays.asList(10000,45000,60000));
	
		allDataSets.get(0).setProgressBar(new JProgressBar());
		int counter = 0;
		for (int i =0; i<sigmaXY.size(); i++){
			for (int j = 0;j< le.size(); j++){
				for (int k = 0;k<koff.size(); k++){
					counter += 1;
					params.labelingEfficiency = le.get(j);
					params.sigmaXY = sigmaXY.get(i);
					params.sigmaZ = sigmaZ.get(i);
					params.kOff = koff.get(k);
					calculate(params);
					params.detectionEfficiency = de.get(i);
					params.recordedFrames = frames.get(i);
					params.borders = getBorders();
					
					String fname = String.format("sigmas%1.0f_%1.0flabelingEff%1.0fPercentKOFF%1.0f", params.sigmaXY,params.sigmaZ,params.labelingEfficiency,params.kOff);
					new File(outputFolder+fname+"\\").mkdir();
					exportData(outputFolder+fname+"\\",fname, params);
					System.out.println(String.format("run %d of %d",counter,sigmaXY.size()*koff.size()*le.size()));
				}
			}
		}
		
		
		
	}
	
	private static SimulationParameter standardParameterActin() {
		SimulationParameter params = new SimulationParameter();
		params.angleOfLabel = (float) (Math.PI/2);
		params.backgroundPerMicroMeterCubed = 0;
		params.coupleSigmaIntensity = true;
		params.detectionEfficiency = 100;
		params.epitopeDensity = (float) 0.0115;
		params.fluorophoresPerLabel = 1;
		params.kOff = 2000;
		params.kOn = 1;
		params.labelEpitopeDistance = 1;
		params.labelingEfficiency = 10;
		params.makeItReproducible = true;
		params.MeanPhotonNumber = 4000;
		params.radiusOfFilament = (float) 12.5;
		params.recordedFrames = 10000;
		params.sigmaXY = 8;
		params.sigmaZ = 30;
		params.viewStatus = 1;
		return params;
	}
	private static SimulationParameter standardParameterMicrotubules() {
		SimulationParameter params = new SimulationParameter();
		params.angleOfLabel = (float) (Math.PI/2);
		params.backgroundPerMicroMeterCubed = 50;
		params.coupleSigmaIntensity = true;
		params.detectionEfficiency = 100;
		params.epitopeDensity = (float) 1.625;
		params.fluorophoresPerLabel = 1;
		params.kOff = 2000;
		params.kOn = 1;
		params.labelEpitopeDistance = 16;
		params.labelingEfficiency = 10;
		params.makeItReproducible = true;
		params.MeanPhotonNumber = 4000;
		params.radiusOfFilament = (float) 12.5;
		params.recordedFrames = 10000;
		params.sigmaXY = 4;
		params.sigmaZ = 8;
		params.viewStatus = 1;
		return params;
	}
	
	private static SimulationParameter standardParameterVesicles() {
		SimulationParameter params = new SimulationParameter();
		params.angleOfLabel = (float) (Math.PI/2);
		params.backgroundPerMicroMeterCubed = 50;
		params.coupleSigmaIntensity = true;
		params.detectionEfficiency = 100;
		params.epitopeDensity = (float) 0.00626;
		params.fluorophoresPerLabel = 1;
		params.kOff = 2000;
		params.kOn = 1;
		params.labelEpitopeDistance = 16;
		params.labelingEfficiency = 10;
		params.makeItReproducible = true;
		params.MeanPhotonNumber = 4000;
		params.radiusOfFilament = (float) 12.5;
		params.recordedFrames = 10000;
		params.sigmaXY = 4;
		params.sigmaZ = 8;
		params.viewStatus = 1;
		return params;
	}

	private static String getShortFilename(ArrayList<String> names, ArrayList<String> values){
		String filename = "";
		
		return filename;
	}
	
	private static String getFilename(SimulationParameter params){
		String filename;
		filename = "sigXY"+(params.sigmaXY)+"sigZ"+params.sigmaZ+"frames"+params.recordedFrames+"radOfFil"+params.radiusOfFilament+
				"meanPhotons"+params.MeanPhotonNumber+"reproducible"+params.makeItReproducible+"labEff"+params.labelingEfficiency+"labEpiDist"+params.labelEpitopeDistance+
				"kon"+params.kOn+"koff"+params.kOff+"fluorPerLabel"+params.fluorophoresPerLabel+"epiDens"+params.epitopeDensity+
				"detEff"+params.detectionEfficiency+"coupleSigmaInt"+params.coupleSigmaIntensity+"bgpermm3"+params.backgroundPerMicroMeterCubed;
		
		return filename;
	}
	
	private static ArrayList<Float> getBorders(){
		DataSet thisDataSet = allDataSets.get(0);
		ArrayList<Float> retList = new ArrayList<Float>();
		retList.add(Calc.min(thisDataSet.stormData,0));
		retList.add(Calc.max(thisDataSet.stormData,0));
		retList.add(Calc.min(thisDataSet.stormData,1));
		retList.add(Calc.max(thisDataSet.stormData,1));
		retList.add(Calc.min(thisDataSet.stormData,2));
		retList.add(Calc.max(thisDataSet.stormData,2));
		
		return retList;
		
	}
	
	private static void furtherProceedFileImport(DataSet data, DataType type){
		if(data.dataType.equals(DataType.TRIANGLES)) {
			System.out.println("Triangles parsed correctly.");
		}
		else if(type.equals(DataType.LINES)) {
			System.out.println("Lines parsed correctly.");
		}
		else if(type.equals(DataType.PLY)){
			System.out.println("PLY file parsed.");
		}
		
		allDataSets.add(data);
		
		if (allDataSets.get(allDataSets.size()-1).dataType == DataType.LINES){
			LineDataSet lines = (LineDataSet) allDataSets.get(allDataSets.size()-1);
		}
		else{
			TriangleDataSet triangles = (TriangleDataSet) allDataSets.get(allDataSets.size()-1);
		}
	
		
	}
	private static void calculate(SimulationParameter params) {
		setUpRandomNumberGenerator(params.makeItReproducible) ;
		int currentRow= 0;
		allDataSets.get(currentRow).getParameterSet().setPabs((float) (params.labelingEfficiency/100.));//Labeling efficiency
		allDataSets.get(currentRow).getParameterSet().setAoa((float) ((90)/180*Math.PI));
		allDataSets.get(currentRow).getParameterSet().setDeff((float) (params.detectionEfficiency/100)); //detection efficiency 
		allDataSets.get(currentRow).getParameterSet().setIlpmm3(params.backgroundPerMicroMeterCubed); //background per cubic micro meter
		allDataSets.get(currentRow).getParameterSet().setLoa(params.labelEpitopeDistance); //label epitope distance
		allDataSets.get(currentRow).getParameterSet().setFpab(params.fluorophoresPerLabel);//fluorophores per label
		allDataSets.get(currentRow).getParameterSet().setKOn(params.kOn); 
		allDataSets.get(currentRow).getParameterSet().setKOff(params.kOff);
		allDataSets.get(currentRow).getParameterSet().setBleachConst((float) 0);
		allDataSets.get(currentRow).getParameterSet().setFrames(params.recordedFrames); // recorded frames
		allDataSets.get(currentRow).getParameterSet().setSxy(params.sigmaXY);
		allDataSets.get(currentRow).getParameterSet().setSz(params.sigmaZ);
		allDataSets.get(currentRow).getParameterSet().setPsfwidth((float) 380);
		allDataSets.get(currentRow).getParameterSet().setMeanPhotonNumber(params.MeanPhotonNumber);
		if(allDataSets.get(currentRow).dataType == DataType.LINES) {
			allDataSets.get(currentRow).getParameterSet().setBspnm(params.epitopeDensity);
			allDataSets.get(currentRow).getParameterSet().setRof(params.radiusOfFilament);
		}
		else {
			allDataSets.get(currentRow).getParameterSet().setBspsnm(params.epitopeDensity);
		}
		allDataSets.get(currentRow).getParameterSet().setMergedPSF(false);
		allDataSets.get(currentRow).getParameterSet().setApplyBleaching(false);
		allDataSets.get(currentRow).getParameterSet().setCoupleSigmaIntensity(params.coupleSigmaIntensity);
		STORMCalculator calc = new STORMCalculator(allDataSets.get(currentRow), random);
		calc = new STORMCalculator(allDataSets.get(currentRow),random);
		//calc = new STORMCalculator(allDataSets.get(currentRow));
		calc.execute();
		while(!calc.isDone()){
			try {
				Thread.sleep(100);
				//System.out.println(calc.isCancelled()+" "+calc.isDone());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// When calc has finished, grab the new dataset
		//allDataSets.set(currentRow, calc.getCurrentDataSet());
		//visualizeAllSelectedData();
	}
	private static void proceedFileImport(File file) {
		System.out.println("Path: " + file.getAbsolutePath());
		DataType type = DataType.UNKNOWN;
		try {
			type = DataTypeDetector.getDataType(file.getAbsolutePath());
			System.out.println(type.toString());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		DataSet data = ParserWrapper.parseFileOfType(file.getAbsolutePath(), type);
		data.setName(file.getName());
		data.setProgressBar(new JProgressBar());
		furtherProceedFileImport(data,type);
	}
	
	private static void setUpRandomNumberGenerator(boolean makeItReproducible) {
		if (makeItReproducible){
			random = new Random(2);
		} else {
			random = new Random(System.currentTimeMillis());
		}
	}
	
	private static void exportData(String path, String name, SimulationParameter params){
		if(!name.endsWith(EXTENSIONIMAGEOUTPUT)){
			name = name += EXTENSIONIMAGEOUTPUT;
		}
		path = path + name;
		System.out.println("Path to write project: " + path);
		System.out.println("project name: " + name);
		FileManager.ExportToFile(allDataSets.get(0), path, params.viewStatus,params.borders);
	}
}


class SimulationParameter{
	float angleOfLabel;
	float labelingEfficiency;
	float detectionEfficiency;
	float backgroundPerMicroMeterCubed;
	float labelEpitopeDistance;
	float fluorophoresPerLabel;
	int recordedFrames;
	float kOn;
	float kOff;
	float sigmaXY;
	float sigmaZ;
	int MeanPhotonNumber;
	float radiusOfFilament;
	float epitopeDensity;
	boolean coupleSigmaIntensity;
	boolean makeItReproducible;
	ArrayList<Float> borders;
	int viewStatus = 0;
	SimulationParameter(){
	
	}

}
