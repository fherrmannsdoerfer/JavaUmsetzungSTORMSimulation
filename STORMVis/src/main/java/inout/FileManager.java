package inout;

import gui.DataTypeDetector.DataType;
import ij.ImagePlus;
import ij.plugin.RGBStackMerge;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.jzy3d.maths.Coord3d;

import calc.Calc;
import model.DataSet;
import model.EpitopeDataSetSerializable;
import model.EpitopeDataSet;
import model.LineDataSet;
import model.LineDataSetSerializable;
import model.ParameterSet;
import model.PointsOnlyDataSet;
import model.Project;
import model.TriangleDataSet;
import model.TriangleDataSetSerializable;
import model.PointsOnlyDataSetSerializable;
/**
 * 
 * @author maxscheurer
 * @brief import/export .storm
 * Import and export of .storm-files, including serialization
 */
public class FileManager {
	
	public static void writeProjectToFile(Project p, String path) {
		for(DataSet set : p.dataSets) {
			if(set.dataType == DataType.LINES) {
				LineDataSetSerializable serial = new LineDataSetSerializable(set.parameterSet, (LineDataSet) set);
				p.dataSets.set(p.dataSets.indexOf(set), serial);
			}
			else if(set.dataType == DataType.TRIANGLES) {
				TriangleDataSetSerializable serial = new TriangleDataSetSerializable(set.parameterSet, (TriangleDataSet) set);
				serial.setDataType(DataType.TRIANGLES);
				p.dataSets.set(p.dataSets.indexOf(set), serial);
//				((TriangleDataSet) set).logPoints();
			}
			else if(set.dataType == DataType.POINTS){
				PointsOnlyDataSetSerializable serial = new PointsOnlyDataSetSerializable(set.parameterSet, (PointsOnlyDataSet) set);
				serial.setDataType(DataType.POINTS);
				p.dataSets.set(p.dataSets.indexOf(set), serial);
			}
			else if(set.dataType == DataType.EPITOPES){
				EpitopeDataSetSerializable serial = new EpitopeDataSetSerializable(set.parameterSet, (EpitopeDataSet) set);
				p.dataSets.set(p.dataSets.indexOf(set),serial);
			}
		}
		
		try (
				OutputStream file = new FileOutputStream(path);
				OutputStream buffer = new BufferedOutputStream(file);
				ObjectOutput output = new ObjectOutputStream(buffer);
				)
				{
			output.writeObject(p);
				}  
		catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static Project openProjectFromFile(String path) {
		Project p = null;
		try{
			InputStream file = new FileInputStream(path);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream (buffer);
			p = (Project) input.readObject();
			System.out.println(input);
		}
		catch(ClassNotFoundException ex){
			ex.printStackTrace();
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		
		for(DataSet set : p.dataSets) {
			if(set.dataType == DataType.LINES) {
				LineDataSet serial = new LineDataSet(set.parameterSet, (LineDataSetSerializable) set);
				p.dataSets.set(p.dataSets.indexOf(set), serial);
			}
			else if(set.dataType == DataType.TRIANGLES) {
				TriangleDataSet serial = new TriangleDataSet(set.parameterSet, (TriangleDataSetSerializable) set);
				p.dataSets.set(p.dataSets.indexOf(set), serial);
			}
			else if(set.dataType == DataType.POINTS){
				PointsOnlyDataSet serial = new PointsOnlyDataSet(set.parameterSet, (PointsOnlyDataSetSerializable) set);
				p.dataSets.set(p.dataSets.indexOf(set),serial);
			}
			else if(set.dataType == DataType.EPITOPES){
				EpitopeDataSet serial = new EpitopeDataSet(set.parameterSet, (EpitopeDataSetSerializable) set);
				p.dataSets.set(p.dataSets.indexOf(set), serial);
			}
		}
		
		return p;
	}

	public static void ExportToFile(DataSet dataset, String path, int mode,ArrayList<Float> borders,
			double pixelsize, double sigma, float[] shifts) {
		
		String basename = path.substring(0, path.length()-4);
		writeProjectionToFile(dataset,path,mode,borders,pixelsize, sigma, shifts);
		writeEpitopesToFile(dataset.antiBodyStartPoints, basename,borders);
		writeFluorophoresToFile(dataset.fluorophorePos,basename,borders);
		writeLocalizationsToFile(dataset.stormData,basename,borders);
		writeLocalizationsToFileForFRC(dataset.stormData, basename,borders);
		writeLogFile(dataset.getParameterSet(), basename,borders);
	}
	
	private static void writeProjectionToFile(DataSet dataset, String path, int mode, 
			ArrayList<Float> borders, double pixelsize, double sigmaOrig, float[] shifts){
		float[][] stormData = dataset.stormData;
//		double pixelsize = 10;
		double sigma = sigmaOrig/pixelsize; //in nm sigma to blur localizations
		int filterwidth = 9; // must be odd
		float xmin = Calc.min(stormData, 0);
		float xmax = Calc.max(stormData, 0);
		float ymin = Calc.min(stormData, 1);
		float ymax = Calc.max(stormData, 1);
		float zmin = Calc.min(stormData, 2);
		float zmax = Calc.max(stormData, 2);
		ArrayList<Float> dims = new ArrayList<Float>();
		dims.add(xmin);
		dims.add(xmax);
		dims.add(ymin);
		dims.add(ymax);
		dims.add(borders.get(4));
		dims.add(borders.get(5));
		int pixelX = 0;
		int pixelY = 0;
		if (mode == 0){
			mode = 1;
		}
		
		switch (mode){//which projection is exported
			case 1://xy
				pixelX = (int) Math.pow(2, Math.ceil(Math.log((xmax+shifts[0]) / pixelsize+2*filterwidth)/Math.log(2)));
				pixelY = (int) Math.pow(2, Math.ceil(Math.log((ymax+shifts[1]) / pixelsize+2*filterwidth)/Math.log(2)));
				break;
			case 2://xz
				pixelX = (int) Math.pow(2, Math.ceil(Math.log((xmax+shifts[0]) / pixelsize+2*filterwidth)/Math.log(2)));
				pixelY = (int) Math.pow(2, Math.ceil(Math.log((zmax+shifts[2]) / pixelsize+2*filterwidth)/Math.log(2)));
				break;
			case 3://yz
				pixelX = (int) Math.pow(2, Math.ceil(Math.log((ymax+shifts[1]) / pixelsize+2*filterwidth)/Math.log(2)));
				pixelY = (int) Math.pow(2, Math.ceil(Math.log((zmax+shifts[2]) / pixelsize+2*filterwidth)/Math.log(2)));
				break;
		}
		
		float [][] image = new float[pixelX][pixelY];
		image = Calc.addFilteredPoints(image, sigma, filterwidth, pixelsize, stormData,mode,dims,borders,shifts);
		write2dImage(pixelX, pixelY, image, path);
		
		ArrayList<float[][]> coloredImage = new ArrayList<float[][]>();
		for (int i = 0; i<3; i++){
			float[][] ch = new float[pixelX][pixelY];
			coloredImage.add(ch);
		}
		
		coloredImage = Calc.addFilteredPoints3D(coloredImage, sigma, filterwidth, pixelsize, stormData,mode,borders,dims,dataset.getParameterSet().isColorProof());
		write3dImage(pixelX, pixelY, coloredImage, path,"",false);
		
		ArrayList<float[][]> colorBar = new ArrayList<float[][]>();
		for (int i = 0; i<3; i++){
			float[][] ch = new float[512][30];
			colorBar.add(ch);
		}
		colorBar = Calc.fillColorBar(colorBar, dims.get(4), dims.get(5), dataset.getParameterSet().isColorProof());
		String tag = "ColorBar_zmin" + dims.get(4)+"_zmax"+dims.get(5); 
		write3dImage(512,30,colorBar,path,tag,true);

	}
	

	private static void write2dImage(int pixelX, int pixelY, float[][] image,String path){
		ImageProcessor ip = new FloatProcessor(pixelX,pixelY);
		ip.setFloatArray(image);
		ImagePlus imgP = new ImagePlus("", ip);
		//System.out.println("Image rendered ("+imgP.getWidth()+"*"+imgP.getHeight()+")");
		ij.IJ.save(new ImagePlus("",imgP.getProcessor().convertToByte(false)), path);
		ij.IJ.save(imgP, path);
	}
	private static void write3dImage(int pixelX, int pixelY, ArrayList<float[][]> coloredImage,String path){
		write3dImage(pixelX, pixelY, coloredImage, path,"", true);
	}
	private static void write3dImage(int pixelX, int pixelY, ArrayList<float[][]> coloredImage,String path,String tag,boolean scalebar){
		ImageProcessor ipRed = new FloatProcessor(pixelX,pixelY);
		ImageProcessor ipGreen = new FloatProcessor(pixelX,pixelY);
		ImageProcessor ipBlue = new FloatProcessor(pixelX,pixelY);
		ipRed.setFloatArray(coloredImage.get(0));
		ipGreen.setFloatArray(coloredImage.get(1));
		ipBlue.setFloatArray(coloredImage.get(2));
		ImagePlus imgPRed = new ImagePlus("", ipRed);
		ImagePlus imgPGreen = new ImagePlus("", ipGreen);
		ImagePlus imgPBlue = new ImagePlus("", ipBlue);
		System.out.println("3D Image rendered ("+imgPRed.getWidth()+"*"+imgPRed.getHeight()+")");
		ArrayList<ImagePlus> colImg = new ArrayList<ImagePlus>();
		colImg.add(imgPRed);
		colImg.add(imgPGreen);
		colImg.add(imgPBlue);
		String basename = path.substring(0, path.length()-4);
		if (!scalebar){
			ij.IJ.save(colImg.get(0),basename+tag+"redCh.tif");
			ij.IJ.save(colImg.get(1),basename+tag+"greenCh.tif");
			ij.IJ.save(colImg.get(2),basename+tag+"blueCh.tif");
		}
		ImagePlus[] imPlusStack = new ImagePlus[3];
		if (scalebar){
			colImg.get(0).setDisplayRange(0, 255);
			colImg.get(1).setDisplayRange(0, 255);
			colImg.get(2).setDisplayRange(0, 255);
		}
		else {
			//colImg.get(0).setDisplayRange(0, 65500);
			//colImg.get(1).setDisplayRange(0, 65500);
		//	colImg.get(2).setDisplayRange(0, 65500);
		}
		imPlusStack[0] = colImg.get(0);
		imPlusStack[1] = colImg.get(1);
		imPlusStack[2] = colImg.get(2);
		ImagePlus imgRGB = RGBStackMerge.mergeChannels(imPlusStack, true);
		ij.IJ.saveAs(imgRGB, "png", basename+tag);
	}

	public static void writeLogFile(ParameterSet ps, String basename,ArrayList<Float> borders) {
		writeLogFile(ps, basename,borders,false);
	}
	
	public static void writeLogFile(ParameterSet ps, String basename,ArrayList<Float> borders,boolean tiffStackOutput) {
		try{
			FileWriter writer = new FileWriter(basename+"Parameters.txt");
			writer.append("Automatically generated Logfile containing all settings.\n");
			writer.append("Angle of antibodies [degree]: "+ 180/Math.PI*ps.getAoa()+"\n");
			writer.append("Sigma of antibodies deviation [degree]: "+ 180/Math.PI*ps.getSoa()+"\n");
			writer.append("Bindingsites per nm [nm^-1]: " + ps.getBspnm()+"\n");
			writer.append("Labeling efficiency [%]: "+ ps.getPabs()*100+"\n");
			writer.append("Radius of filament [nm]: " + ps.getRof()+"\n");
			writer.append("Fluorophores per label: "+ ps.getFpab()+"\n");
			writer.append("Label epitope distance [nm]: "+ps.getLoa()+"\n");
			writer.append(String.format("Color model [r,g,b]: %d, %d, %d\n", ps.getEmColor().getRed(),ps.getEmColor().getGreen(),ps.getEmColor().getBlue()));
			writer.append(String.format("Color simulation [r,g,b]: %d, %d, %d\n", ps.getStormColor().getRed(),ps.getStormColor().getGreen(),ps.getStormColor().getBlue()));
			writer.append(String.format("Color label [r,g,b]: %d, %d, %d\n", ps.getAntibodyColor().getRed(),ps.getAntibodyColor().getGreen(),ps.getAntibodyColor().getBlue()));
			if (!tiffStackOutput){
				writer.append("Localization precision (sigma x,y) [nm]: "+ps.getSxy()+"\n");
				writer.append("Localization precision (sigma z) [nm]: "+ps.getSz()+"\n");
			}
			writer.append("Bindingsites per square nanometer [nm^-2]: "+ps.getBspsnm()+"\n");
			writer.append("Frames: "+ps.getFrames()+"\n");
			writer.append("Duty cycle: "+ ps.getDutyCycle()+"\n");
			writer.append("Bleaching constant [1/10000 frames]: "+ ps.getBleachConst()+"\n");
			writer.append("Median photon number: "+ps.getMeanPhotonNumber()+"\n");
			writer.append("Detection efficiency [%]: "+ ps.getDeff()*100+"\n");
			if (ps.getGeneralVisibility()){
				writer.append("General visibility: TRUE\n");
			}
			else{
				writer.append("General visibility: FALSE\n");
			}
			if (ps.getEmVisibility()){
				writer.append("Model visibility: TRUE\n");
			}
			else{
				writer.append("Model visibility: FALSE\n");
			}
			if (ps.getStormVisibility()){
				writer.append("Simulation visibility: TRUE\n");
			}
			else{
				writer.append("Simulation visibility: FALSE\n");
			}
			if (ps.getAntibodyVisibility()){
				writer.append("Label visibility: TRUE\n");
			}
			else{
				writer.append("Label visibility: FALSE\n");
			}
			writer.append("Background per volume [m^-3]: " + ps.getIlpmm3()+"\n");
			writer.append("FWHM of PSF [nm]: "+ps.getPsfwidth()+"\n");
			if (ps.getApplyBleaching()){
				writer.append("Apply bleaching: TRUE\n");
			}
			else{
				writer.append("Apply bleaching: FALSE\n");
			}
			if (ps.getMergedPSF()){
				writer.append("Merge close PSFs: TRUE\n");
			}
			else{
				writer.append("Merge close PSFs: FALSE\n");
			}
			if (ps.getCoupleSigmaIntensity()){
				writer.append("Constant Localization Precision: False\n");
			}
			else{
				writer.append("Constant Localization Precision: TRUE\n");
			}
			writer.append("Point size: "+ps.getPointSize()+"\n");
			writer.append("Line width: "+ps.getLineWidth()+"\n");
			writer.append("Selected borders in x: "+borders.get(0)+" to "+borders.get(1)+"\n");
			writer.append("Selected borders in y: "+borders.get(2)+" to "+borders.get(3)+"\n");
			writer.append("Selected borders in z: "+borders.get(4)+" to "+borders.get(5)+"\n");
			writer.append("Pixelsize for output images in nm: "+ps.getPixelsize()+"\n");
			writer.append("Width of Gaussian for rendering of output images in nm: "+ps.getSigmaRendering()+"\n");
			if (ps.isColorProof()){
				writer.append("Only blue and green colors used: TRUE\n");
			}
			else{
				writer.append("Only blue and green colors used: FALSE\n");
			}
			
			if (tiffStackOutput){
				writer.append("\n");
				writer.append("Parameters for Tiffstack creation\n");
				writer.append("Pixel to nanometer ratio: "+ps.getPixelToNmRatio()+"\n");
				writer.append("Frame rate: "+ps.getFrameRate()+"\n");
				writer.append("Mean blinking duration: "+ps.getMeanBlinkingTime()+"\n");
				writer.append("Dead time: "+ps.getDeadTime()+"\n");
				writer.append("Sigma of Readout noise: "+ps.getSigmaBg()+"\n");
				writer.append("Value of constant background offset: "+ps.getConstOffset()+"\n");
				writer.append("Em Gain: "+ps.getEmGain()+"\n");
				writer.append("Quantum efficiency: "+ps.getQuantumEfficiency()+"\n");
				writer.append("Conversion factor digitizer in electrons per A/D count : "+ps.getElectronPerAdCount()+"\n");
				writer.append("Window size for PSF rendering: "+ps.getWindowsizePSF()+"\n");
				writer.append("Size of outer rim without PSFs in Tiffstack: "+ps.getEmptyPixelsOnRim()+"\n");
				writer.append("Numeric aperture: "+ps.getNa()+"\n");
				writer.append("Z value of focal plane (2D): "+ps.getFokus()+"\n");
				writer.append("Z value of plane with doubled PSF width: "+ps.getDefokus()+"\n");
				if (ps.isTwoDPSF()){
					writer.append("2D simulation: TRUE\n");
				}
				else{
					writer.append("2D simulation: FALSE\n");
				}
				if (ps.isDistributePSFoverFrames()){
					writer.append("Distribute PSFs over Frames: TRUE\n");
				}
				else{
					writer.append("Distribute PSFs over Frames: FALSE\n");
				}
				if (ps.isEnsureSinglePSF()){
					writer.append("Ensure Single PSFs: TRUE\n");
				}
				else{
					writer.append("Ensure Single PSFs: FALSE\n");
				}
				writer.append("Calibration File:\n");
				writer.append("Fokal position in nm | sigmax in micrometers | sigmay in micrometers\n");
				float[][] calib = ps.getCalibrationFile();
				for (int i = 0; i<ps.getCalibrationFile().length; i++){
					writer.append(calib[i][0]+ " "+calib[i][1]+" "+calib[i][2]+"\n");
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}

	private static void writeLocalizationsToFile(float[][] stormData, String basename,ArrayList<Float> borders) {
		try{
			FileWriter writer = new FileWriter(basename+"Localizations.txt");
			writer.append("Pos_x Pos_y Pos_z Frame Intensity\n");
			for (int i = 0; i<stormData.length; i++){
				float[] tmp = stormData[i];
				if (Calc.isInRange(tmp,borders)){
					writer.append(tmp[0]+" "+tmp[1]+" "+tmp[2]+" "+tmp[3]+" "+tmp[4]+"\n");
        		}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	private static void writeEpitopesToFile(float[][] epitopeData, String basename,ArrayList<Float> borders) {
		try{
			FileWriter writer = new FileWriter(basename+"Epitopes.txt");
			writer.append("Pos_x Pos_y Pos_z \n");
			for (int i = 0; i<epitopeData.length; i++){
				float[] tmp = epitopeData[i];
				if (Calc.isInRange(tmp,borders)){
					writer.append(tmp[0]+" "+tmp[1]+" "+tmp[2]+"\n");
        		}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	private static void writeFluorophoresToFile(float[][] fluorophorePos, String basename,ArrayList<Float> borders) {
		try{
			FileWriter writer = new FileWriter(basename+"FluorophorePos.txt");
			writer.append("Pos_x Pos_y Pos_z \n");
			for (int i = 0; i<fluorophorePos.length; i++){
				float[] tmp = fluorophorePos[i];
				if (Calc.isInRange(tmp,borders)){
					writer.append(tmp[0]+" "+tmp[1]+" "+tmp[2]+"\n");
        		}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	private static void writeLocalizationsToFileForFRC(float[][] stormData, String basename,ArrayList<Float> borders) {
		try{
			double minx = Calc.min(stormData, 0);
			double miny = Calc.min(stormData, 1);
			double minz = Calc.min(stormData, 2);
			FileWriter writerXY = new FileWriter(basename+"LocalizationsForFRCAnalysisXY.txt");
			FileWriter writerXZ = new FileWriter(basename+"LocalizationsForFRCAnalysisXZ.txt");
			FileWriter writerYZ = new FileWriter(basename+"LocalizationsForFRCAnalysisYZ.txt");
			for (int i = 0; i<stormData.length; i++){
				float[] tmp = stormData[i];
				if (Calc.isInRange(tmp,borders)){
					writerXY.append((tmp[0]-minx)/106.6666+" "+(tmp[1]-miny)/106.6666+" "+tmp[3]+"\n");
					writerXZ.append((tmp[0]-minx)/106.6666+" "+(tmp[2]-minz)/106.6666+" "+tmp[3]+"\n");
					writerYZ.append((tmp[1]-miny)/106.6666+" "+(tmp[2]-minz)/106.6666+" "+tmp[3]+"\n");
        		}
			}
			writerXY.flush();
			writerXZ.flush();
			writerYZ.flush();
			writerXY.close();
			writerXZ.close();
			writerYZ.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public static void writeWIMPFile(LineDataSet set, String fname){
		try{
			Locale.setDefault(Locale.ENGLISH);
			FileWriter writer = new FileWriter(fname);
			writer.append("Model file name........................"+fname+"\n");
			writer.append("max # of object....................... "+set.data.size()+"\n");
			writer.append("# of node............................. 6070\n");
			writer.append("# of object........................... "+set.data.size()+"\n");
			writer.append("Object sequence :\n");
			int counter = 0;
			for (int i = 0; i<set.data.size(); i++){
				ArrayList<Coord3d> tmp = set.data.get(i);
				writer.append(String.format(" Object #: %d\n",counter));
				writer.append(String.format("# of points: %d\n",tmp.size()));
				writer.append(String.format("Display switch:1 247\n"));
				writer.append(String.format("  #    X       Y       Z      Mark    Label\n"));
				
			    for (int j = 0; j<set.data.get(i).size(); j++){
					 counter +=1;
					 writer.append(String.format(" %d %4.2f %4.2f %4.2f \n",counter,tmp.get(j).x,tmp.get(j).y,tmp.get(j).z));
			    } 
			}
			writer.append("END");
			writer.close();
		}
		catch (IOException e) {e.printStackTrace();}
	}
} 
