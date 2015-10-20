package inout;

import gui.DataTypeDetector.DataType;
import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import java.util.List;

import calc.Calc;
import model.DataSet;
import model.LineDataSet;
import model.LineDataSetSerializable;
import model.ParameterSet;
import model.Project;
import model.TriangleDataSet;
import model.TriangleDataSetSerializable;
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
		}
		
		return p;
	}

	public static void ExportToFile(DataSet dataset, String path, int mode,ArrayList<Float> borders) {
	
		String basename = path.substring(0, path.length()-4);
		writeProjectionToFile(dataset,path,mode,borders);
		writeLocalizationsToFile(dataset.stormData,basename,borders);
		writeLocalizationsToFileForFRC(dataset.stormData, basename,borders);
		writeLogFile(dataset, basename,borders);
	}
	
	private static void writeProjectionToFile(DataSet dataset, String path, int mode, ArrayList<Float> borders){
		float[][] stormData = dataset.stormData;
		double pixelsize = 10;
		double sigma = 20/pixelsize; //in nm sigma to blur localizations
		int filterwidth = 3; // must be odd
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
				pixelX =(int) Math.pow(2, Math.ceil(Math.log((xmax-xmin) / pixelsize)/Math.log(2)));
				pixelY = (int) Math.pow(2, Math.ceil(Math.log((ymax-ymin) / pixelsize)/Math.log(2)));
				break;
			case 2://xz
				pixelX =(int) Math.pow(2, Math.ceil(Math.log((xmax-xmin) / pixelsize)/Math.log(2)));
				pixelY = (int) Math.pow(2, Math.ceil(Math.log((zmax-zmin) / pixelsize)/Math.log(2)));
				break;
			case 3://yz
				pixelX =(int) Math.pow(2, Math.ceil(Math.log((ymax-ymin) / pixelsize)/Math.log(2)));
				pixelY = (int) Math.pow(2, Math.ceil(Math.log((zmax-zmin) / pixelsize)/Math.log(2)));
				break;
		}
		
		float [][] image = new float[pixelX][pixelY];
		image = Calc.addFilteredPoints(image, sigma, filterwidth, pixelsize, stormData,mode,dims,borders);
		write2dImage(pixelX, pixelY, image, path);
		
		ArrayList<float[][]> coloredImage = new ArrayList<float[][]>();
		for (int i = 0; i<3; i++){
			float[][] ch = new float[pixelX][pixelY];
			coloredImage.add(ch);
		}
		
		coloredImage = Calc.addFilteredPoints3D(coloredImage, sigma, filterwidth, pixelsize, stormData,mode,borders,dims);
		write3dImage(pixelX, pixelY, coloredImage, path);
		
		ArrayList<float[][]> colorBar = new ArrayList<float[][]>();
		for (int i = 0; i<3; i++){
			float[][] ch = new float[512][30];
			colorBar.add(ch);
		}
		colorBar = Calc.fillColorBar(colorBar, dims.get(4), dims.get(5));
		String tag = "ColorBar_zmin" + dims.get(4)+"_zmax"+dims.get(5); 
		write3dImage(512,30,colorBar,path,tag);

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
		write3dImage(pixelX, pixelY, coloredImage, path,"");
	}
	private static void write3dImage(int pixelX, int pixelY, ArrayList<float[][]> coloredImage,String path,String tag){
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
		ij.IJ.save(colImg.get(0),basename+tag+"redCh.tif");
		ij.IJ.save(colImg.get(1),basename+tag+"greenCh.tif");
		ij.IJ.save(colImg.get(2),basename+tag+"blueCh.tif");
	}

	private static void writeLogFile(DataSet dataset, String basename,ArrayList<Float> borders) {
		ParameterSet ps = dataset.getParameterSet();
		try{
			FileWriter writer = new FileWriter(basename+"Parameters.txt");
			writer.append("Automatically generated Logfile containing all settings.\n");
			writer.append("Angle of antibodies [degree]: "+ ps.getAoa()+"\n");
			writer.append("Bindingsites per nm [nm^-1]: " + ps.getBspnm()+"\n");
			writer.append("Labeling efficiency [%]: "+ ps.getPabs()*100+"\n");
			writer.append("Radius of filament [nm]: " + ps.getRof()+"\n");
			writer.append("Fluorophores per label: "+ ps.getFpab()+"\n");
			writer.append("Label epitope distance [nm]: "+ps.getLoa()+"\n");
			writer.append(String.format("Color model [r,g,b]: %d, %d, %d\n", ps.getEmColor().getRed(),ps.getEmColor().getGreen(),ps.getEmColor().getBlue()));
			writer.append(String.format("Color simulation [r,g,b]: %d, %d, %d\n", ps.getStormColor().getRed(),ps.getStormColor().getGreen(),ps.getStormColor().getBlue()));
			writer.append(String.format("Color label [r,g,b]: %d, %d, %d\n", ps.getAntibodyColor().getRed(),ps.getAntibodyColor().getGreen(),ps.getAntibodyColor().getBlue()));
			writer.append("Localization precision (sigma x,y) [nm]: "+ps.getSxy()+"\n");
			writer.append("Localization precision (sigma z) [nm]: "+ps.getSz()+"\n");
			writer.append("Bindingsites per square nanometer [nm^-2]: "+ps.getBspsnm()+"\n");
			writer.append("Frames: "+ps.getFrames()+"\n");
			writer.append("K_On time [AU]: "+ ps.getKOn()+"\n");
			writer.append("K_Off time [AU]: "+ ps.getKOff()+"\n");
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
				writer.append("Couple localization error with intensity: TRUE\n");
			}
			else{
				writer.append("Couple localization error with intensity: FALSE\n");
			}
			writer.append("Point size: "+ps.getPointSize()+"\n");
			writer.append("Line width: "+ps.getLineWidth()+"\n");
			writer.append("Selected borders in x: "+borders.get(0)+" to "+borders.get(1)+"\n");
			writer.append("Selected borders in y: "+borders.get(2)+" to "+borders.get(3)+"\n");
			writer.append("Selected borders in z: "+borders.get(4)+" to "+borders.get(5)+"\n");
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
	
	private static void writeLocalizationsToFileForFRC(float[][] stormData, String basename,ArrayList<Float> borders) {
		try{
			double minx = Calc.min(stormData, 0);
			double miny = Calc.min(stormData, 1);
			FileWriter writer = new FileWriter(basename+"LocalizationsForFRCAnalysis.txt");
			for (int i = 0; i<stormData.length; i++){
				float[] tmp = stormData[i];
				if (Calc.isInRange(tmp,borders)){
					writer.append((tmp[0]-minx)/106.6666+" "+(tmp[1]-miny)/106.6666+" "+tmp[3]+"\n");
        		}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}
} 
