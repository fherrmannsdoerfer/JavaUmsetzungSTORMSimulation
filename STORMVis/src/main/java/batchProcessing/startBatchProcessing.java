package batchProcessing;

import gui.CreateTiffStack;
import gui.DataTypeDetector;
import gui.ExamplesProvidingClass;
import gui.ParserWrapper;
import gui.DataTypeDetector.DataType;
import ij.gui.GUI;
import inout.FileManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.io.FileUtils;

import calc.Calc;
import calc.CreateStack;
import calc.STORMCalculator;
import model.DataSet;
import model.EpitopeDataSet;
import model.LineDataSet;
import model.TriangleDataSet;

public class startBatchProcessing {
	static List<DataSet> allDataSets = new ArrayList<DataSet>();
	private static Random random;
	private static String EXTENSIONIMAGEOUTPUT = ".tif";
	private static String outputFolder = "";// "C:\\Users\\herrmannsdoerfer\\Desktop\\Tiff-StackTestModelle\\Mikrotubuli\\";
	private static String inputFolder = "";
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//File file = new File("Y:\\Users_shared\\SuReSim-Software Project\\SuReSim Rebuttal\\Fire\\Simulation 12er Figure Table FRC\\Modelle\\141107-MT-Modelrescaled1d.wimp");
		JOptionPane.showMessageDialog(null, "Sind die richtigen Parameter ausgewählt?");
		proceedFileImport(new File(UserDefinedInputOutput.getInputFile()));
		outputFolder = UserDefinedInputOutput.getOutputFolder();
//		DataSet data = ExamplesProvidingClass.getDataset(1);
//		furtherProceedFileImport(data, data.dataType);
		(new File(outputFolder)).mkdir();
		boolean tiffStackOutput = false;
		boolean suReSimOutput = true;
		int numberOfSimulationsWithSameParameterSet = 1; //number of outputs for the same parameter set
		SimulationParameter params = standardParameterActin();		
		ArrayList<Float> sigmaXY = new ArrayList<Float>(Arrays.asList(4.f,8.f,12.f,25.f));
		ArrayList<Float> sigmaZ = new ArrayList<Float>(Arrays.asList(8.f,30.f,40.f,50.f));
		ArrayList<Float> le = new ArrayList<Float>(Arrays.asList(10.f,50.f,100.f));
		ArrayList<Float> varAng = new ArrayList<Float>(Arrays.asList(0.f));
		//ArrayList<Float> de = new ArrayList<Float>(Arrays.asList(10.f,20.f,50.f,100.f));
		ArrayList<Float> koff = new ArrayList<Float>(Arrays.asList(5.f/10000));
		//ArrayList<Integer> frames = new ArrayList<Integer>(Arrays.asList(10000));
		ArrayList<Float> labelLength = new ArrayList<Float>(Arrays.asList(1.f));
		ArrayList<Integer> photonOutput = new ArrayList<Integer>(Arrays.asList(4000));
		allDataSets.get(0).setProgressBar(new JProgressBar());
		
		
		int counter = 0;
		for (int ll = 0; ll<photonOutput.size();ll++){
			for (int s = 0; s<labelLength.size(); s++){
				for (int a = 0; a<varAng.size(); a++){
					for (int i =0; i<sigmaXY.size(); i++){
						for (int j = 0;j< le.size(); j++){
							for (int k = 0;k<koff.size(); k++){
								for (int p = 0;p<numberOfSimulationsWithSameParameterSet; p++){
									counter += 1;
									params.labelEpitopeDistance = labelLength.get(s);
									params.angularDeviation = (float) (varAng.get(a)/180.*Math.PI);
									params.labelingEfficiency = le.get(j);
									params.sigmaXY = sigmaXY.get(i);
									params.sigmaZ = sigmaZ.get(i);
									params.dutyCycle = koff.get(k);
									params.sigmaRendering = 0.4 * params.sigmaXY;
									calculate(params);
									//params.detectionEfficiency = de.get(i);
									//params.recordedFrames = frames.get(i);
									params.borders = getBorders();
									params.MeanPhotonNumber = photonOutput.get(ll);
									String fname = String.format("sig%1.0f_%1.0flabEff%1.0fPhoton%dAngDev%1.0fLabLen%1.0fver%d", params.sigmaXY,params.sigmaZ,params.labelingEfficiency,params.MeanPhotonNumber,params.angularDeviation*180/Math.PI,params.labelEpitopeDistance,p);
									if(suReSimOutput){
										new File(outputFolder+fname+"\\").mkdir();
										exportData(outputFolder+fname+"\\",fname, params);
									}
									if (tiffStackOutput){
										
										float[][] calibr = allDataSets.get(0).getParameterSet().getCalibrationFile();
										allDataSets.get(0).setProgressBar(new JProgressBar());
										params.sigmaXY = 0.f;
										params.sigmaZ = 0.f;
										calculate(params);
										CreateStack.createTiffStack(allDataSets.get(0).stormData, 1/133.f/**resolution*/ , 10/**emptyspace*/, 
												10.f/**emGain*/,params.borders,random,4.81f/**electrons per AD*/, (float) 30.f/**frameRate*/, 
												0.03f/**blinking duration*/, 15/**sizePSF*/, 1/**modelNR*/,1.f,
												(float) 1.45f/**NA*/, 647.f/**waveLength*/, 200.f/**zFocus*/, 
												600.f/**zDefocus*/, 35.7f/**sigmaNoise*/, 200.f/**constant offset*/, calibr/**calibration file*/,
												outputFolder+fname+"\\"+fname+"TiffStack.tif",
												false /* ensure single PSF*/, true /*split blinking over frames*/, new CreateTiffStack(null,null,null,null));
									
									}
									System.out.println(String.format("run %d of %d",counter,sigmaXY.size()*koff.size()*le.size()*varAng.size()*labelLength.size()*numberOfSimulationsWithSameParameterSet));
								}
							}
						}
					}
				}
			}
		}
		
		
		
		
	}
	
	private static void createRandomEpitopesOnLine(double length, int nbrEpitopes) {
		float[][] bp = new float[nbrEpitopes][3];
		float[][] ep = new float[nbrEpitopes][3];
		for (int i = 0; i<nbrEpitopes; i++){
			bp[i][0] = (float) (Math.random()*length);
			bp[i][1] = 0.f;
			bp[i][2] = 0.f;
			ep[i][0] = 0.f;
			ep[i][1] = 0.f;
			ep[i][2] = 1.f;
		}
		((EpitopeDataSet)allDataSets.get(0)).epitopeBase = bp;
		((EpitopeDataSet)allDataSets.get(0)).epitopeEnd = ep;
		
	}

	private static SimulationParameter standardParameterActin() {
		SimulationParameter params = new SimulationParameter();
		params.angularDeviation = 0;
		params.angleOfLabel = (float) (Math.PI/2);
		params.backgroundPerMicroMeterCubed = 0;
		params.coupleSigmaIntensity = true;
		params.detectionEfficiency = 100;
		params.epitopeDensity = (float) 0.0115;
		params.fluorophoresPerLabel = 1;
		params.dutyCycle = (float) (1.0/2000.f);
		params.labelEpitopeDistance = 16;
		params.labelingEfficiency = 10;
		params.makeItReproducible = false;
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
		params.angularDeviation = 0;
		params.angleOfLabel = (float) (Math.PI/2);
		params.backgroundPerMicroMeterCubed = 50;
		params.coupleSigmaIntensity = true;
		params.detectionEfficiency = 100;
		params.epitopeDensity = (float) 1.625;
		params.fluorophoresPerLabel = 1;
		params.dutyCycle = (float) (1.0/2000.f);
		params.labelEpitopeDistance = 16;
		params.labelingEfficiency = 10;
		params.makeItReproducible = false;
		params.MeanPhotonNumber = 4000;
		params.radiusOfFilament = (float) 12.5;
		params.recordedFrames = 1;
		params.sigmaXY = 4;
		params.sigmaZ = 8;
		params.viewStatus = 1;
		return params;
	}
	
	private static SimulationParameter standardParameterSingleEpitopes() {
		SimulationParameter params = new SimulationParameter();
		params.angularDeviation = 0;
		params.angleOfLabel = (float) (90.f/180*Math.PI);
		params.backgroundPerMicroMeterCubed = 0;
		params.coupleSigmaIntensity = true;
		params.detectionEfficiency = 100;
		params.epitopeDensity = (float) 1.625;
		params.fluorophoresPerLabel = 1;
		params.dutyCycle = (float) (7/50000.f);
		params.labelEpitopeDistance = 16;
		params.labelingEfficiency = 90;
		params.makeItReproducible = false;
		params.MeanPhotonNumber = 3000;
		params.radiusOfFilament = (float) 12.5;
		params.recordedFrames = 50000;
		params.sigmaXY = 6;
		params.sigmaZ = 30;
		params.viewStatus = 1;
		params.sigmaRendering = 5;
		params.pixelsize = 2.5;
		return params;
	}
	
	private static SimulationParameter standardParameterRandomlyDistributedEpitopes() {
		SimulationParameter params = new SimulationParameter();
		params.angularDeviation = 1000;
		params.angleOfLabel = (float) (90.f/180*Math.PI);
		params.backgroundPerMicroMeterCubed = 0;
		params.coupleSigmaIntensity = true;
		params.detectionEfficiency = 100;
		params.epitopeDensity = (float) 1.625;
		params.fluorophoresPerLabel = 1;
		params.dutyCycle = (float) (1.0/2000.f);
		params.labelEpitopeDistance = 16;
		params.labelingEfficiency = 90;
		params.makeItReproducible = false;
		params.MeanPhotonNumber = 3000;
		params.radiusOfFilament = (float) 0;
		params.recordedFrames = 50000;
		params.sigmaXY = 6;
		params.sigmaZ = 30;
		params.viewStatus = 1;
		params.sigmaRendering = 5;
		params.pixelsize = 2.5;
		return params;
	}
	
	private static SimulationParameter standardParameterMicrotubules1nm() {
		SimulationParameter params = new SimulationParameter();
		params.angularDeviation = 0;
		params.angleOfLabel = (float) (Math.PI/2);
		params.backgroundPerMicroMeterCubed = 50;
		params.coupleSigmaIntensity = true;
		params.detectionEfficiency = 100;
		params.epitopeDensity = (float) 1.625;
		params.fluorophoresPerLabel = 1;
		params.dutyCycle = (float) (1.0/2000.f);
		params.labelEpitopeDistance = 1;
		params.labelingEfficiency = 10;
		params.makeItReproducible = false;
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
		params.angularDeviation = 0;
		params.angleOfLabel = (float) (Math.PI/2);
		params.backgroundPerMicroMeterCubed = 50;
		params.coupleSigmaIntensity = true;
		params.detectionEfficiency = 100;
		params.epitopeDensity = (float) 0.00626;
		params.fluorophoresPerLabel = 1;
		params.dutyCycle = (float) (1.0/2000.f);
		params.labelEpitopeDistance = 16;
		params.labelingEfficiency = 10;
		params.makeItReproducible = false;
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
	
	}
	private static void calculate(SimulationParameter params) {
		setUpRandomNumberGenerator(params.makeItReproducible) ;
		int currentRow= 0;
		allDataSets.get(currentRow).getParameterSet().setSoa((float) (params.angularDeviation));//sigma of angle
		allDataSets.get(currentRow).getParameterSet().setPabs((float) (params.labelingEfficiency/100.));//Labeling efficiency
		allDataSets.get(currentRow).getParameterSet().setAoa((float) (params.angleOfLabel));
		allDataSets.get(currentRow).getParameterSet().setDeff((float) (params.detectionEfficiency/100)); //detection efficiency 
		allDataSets.get(currentRow).getParameterSet().setIlpmm3(params.backgroundPerMicroMeterCubed); //background per cubic micro meter
		allDataSets.get(currentRow).getParameterSet().setLoa(params.labelEpitopeDistance); //label epitope distance
		allDataSets.get(currentRow).getParameterSet().setFpab(params.fluorophoresPerLabel);//fluorophores per label
		allDataSets.get(currentRow).getParameterSet().setDutyCycle(params.dutyCycle); 
		allDataSets.get(currentRow).getParameterSet().setBleachConst((float) 0);
		allDataSets.get(currentRow).getParameterSet().setFrames(params.recordedFrames); // recorded frames
		allDataSets.get(currentRow).getParameterSet().setSxy(params.sigmaXY);
		allDataSets.get(currentRow).getParameterSet().setSz(params.sigmaZ);
		allDataSets.get(currentRow).getParameterSet().setPsfwidth((float) 380);
		allDataSets.get(currentRow).getParameterSet().setMeanPhotonNumber(params.MeanPhotonNumber);
		allDataSets.get(currentRow).getParameterSet().setPixelsize(params.pixelsize);
		allDataSets.get(currentRow).getParameterSet().setSigmaRendering(params.sigmaRendering);
		allDataSets.get(currentRow).getParameterSet().setAbpf(params.fluorophoresPerLabel);
		
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
		FileManager.ExportToFile(allDataSets.get(0), path, params.viewStatus,params.borders,
				allDataSets.get(0).getParameterSet().getPixelsize(),allDataSets.get(0).getParameterSet().getSigmaRendering());
	}
}


class SimulationParameter{
	float angleOfLabel;
	float angularDeviation;
	float labelingEfficiency;
	float detectionEfficiency;
	float backgroundPerMicroMeterCubed;
	float labelEpitopeDistance;
	float fluorophoresPerLabel;
	int recordedFrames;
	float dutyCycle;
	float sigmaXY;
	float sigmaZ;
	int MeanPhotonNumber;
	float radiusOfFilament;
	float epitopeDensity;
	boolean coupleSigmaIntensity;
	boolean makeItReproducible= false;
	ArrayList<Float> borders;
	int viewStatus = 0;
	double pixelsize = 10;
	double sigmaRendering = 20;
	SimulationParameter(){
	
	}

}
class UserDefinedInputOutput{
	private static String inputFile;
	private static String outputFile;
	private static String outputFolder;
	private static String inputFolder;
	UserDefinedInputOutput(){
		
	}
	private static String getPath(int mode){
		String tag = "";
		String lastInputPath = loadLastInputPath();
		JFileChooser fileChooserInput = new JFileChooser(lastInputPath);
		String lastOutputPath= loadLastOutputPath();
		JFileChooser fileChooserOutput = new JFileChooser(lastOutputPath);
		switch (mode){
			case 0:
				tag = "Please select input data path:";
				fileChooserInput.setDialogTitle(tag);
				fileChooserInput.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooserInput.showOpenDialog(null);
				inputFolder = fileChooserInput.getSelectedFile().toString();
				saveLastInputPath(inputFolder);
				return inputFolder+"\\";
			case 1:
				tag = "Please select output data path:";
				fileChooserOutput.setDialogTitle(tag);
				fileChooserOutput.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooserOutput.showOpenDialog(null);
				outputFolder = fileChooserOutput.getSelectedFile().toString();
				saveLastOutputPath(outputFolder);
				return outputFolder+"\\";
			case 2:
				tag = "Please select input data Filename:";
				fileChooserInput.setDialogTitle(tag);
				fileChooserInput.setCurrentDirectory(new File(lastInputPath));
				fileChooserInput.showOpenDialog(null);
				fileChooserInput.setFileSelectionMode(JFileChooser.FILES_ONLY);
				inputFile = fileChooserInput.getSelectedFile().toString();
				saveLastInputPath(inputFile);
				return inputFile;
			case 3:
				tag = "Please select output data Filename:";
				fileChooserOutput.setDialogTitle(tag);
				fileChooserOutput.showOpenDialog(null);
				fileChooserOutput.setFileSelectionMode(JFileChooser.FILES_ONLY);
				outputFile = fileChooserOutput.getSelectedFile().toString();
				saveLastOutputPath(outputFile);
				return outputFile;
			default:
				return "something went wrong";	
		}
	}
	private static String loadLastInputPath(){
		String lastPath = System.getProperty("user.home");
		try {
			lastPath = FileUtils.readFileToString(new File(System.getProperty("user.home")+"\\lastPathChosenForLoading.txt"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			if (e1 instanceof FileNotFoundException){
				System.out.println("File not found.");
			}
		}
		return lastPath;
	}
	private static void saveLastInputPath(String lastPath){
		try {
			FileUtils.writeStringToFile(new File(System.getProperty("user.home")+"\\lastPathChosenForLoading.txt"), lastPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static String loadLastOutputPath(){
		String lastPath = System.getProperty("user.home");
		try {
			lastPath = FileUtils.readFileToString(new File(System.getProperty("user.home")+"\\lastPathChosenForSaving.txt"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			if (e1 instanceof FileNotFoundException){
				System.out.println("File not found.");
			}
		}
		return lastPath;
	}
	private static void saveLastOutputPath(String lastPath){
		try {
			FileUtils.writeStringToFile(new File(System.getProperty("user.home")+"\\lastPathChosenForSaving.txt"), lastPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getInputFolder() {
		return getPath(0);
	}
	
	public static String getOutputFolder() {
		return getPath(1);
	}
	public static String getInputFile() {
		return getPath(2);
	}
	
	public static String getOutputFile() {
		return getPath(3);
	}
	
}