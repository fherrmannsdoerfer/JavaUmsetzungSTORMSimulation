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

	public static void writeProjectionToFile(float[][] stormData, String path, int mode) {
		double pixelsize = 10;
		double sigma = 20/pixelsize; //in nm sigma to blur localizations
		int filterwidth = 3; // must be odd
		float xmin = Calc.min(stormData, 0);
		float xmax = Calc.max(stormData, 0);
		float ymin = Calc.min(stormData, 1);
		float ymax = Calc.max(stormData, 1);
		float zmin = Calc.min(stormData, 2);
		float zmax = Calc.max(stormData, 2);
		int pixelX = 0;
		int pixelY = 0;
		if (mode == 0){
			mode = 1;
		}
		
		switch (mode){
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
		image = Calc.addFilteredPoints(image, sigma, filterwidth, pixelsize, stormData,mode,xmin,ymin,zmin);
		ImageProcessor ip = new FloatProcessor(pixelX,pixelY);
		ip.setFloatArray(image);
		ImagePlus imgP = new ImagePlus("", ip);
		//System.out.println("Image rendered ("+imgP.getWidth()+"*"+imgP.getHeight()+")");
		ij.IJ.save(new ImagePlus("",imgP.getProcessor().convertToByte(false)), path);
		ij.IJ.save(imgP, path);
		
		float [][] imageRed = new float[pixelX][pixelY];
		float [][] imageGreen = new float[pixelX][pixelY];
		float [][] imageBlue = new float[pixelX][pixelY];
		ArrayList<float[][]> coloredImage = new ArrayList<float[][]>();
		coloredImage.add(imageRed);
		coloredImage.add(imageGreen);
		coloredImage.add(imageBlue);
		coloredImage = Calc.addFilteredPoints3D(coloredImage, sigma, filterwidth, pixelsize, stormData,mode);
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
		ij.IJ.save(colImg.get(0),basename+"RedCh.tif");
		ij.IJ.save(colImg.get(1),basename+"GreenCh.tif");
		ij.IJ.save(colImg.get(2),basename+"BlueCh.tif");
			
		writeLocalizationsToFile(stormData,basename);
	}

	private static void writeLocalizationsToFile(float[][] stormData, String basename) {
		try{
			FileWriter writer = new FileWriter(basename+"Localizations.txt");
			for (int i = 0; i<stormData.length; i++){
				float[] tmp = stormData[i];
				writer.append(tmp[0]+" "+tmp[1]+" "+tmp[2]+" "+tmp[3]+" "+tmp[4]+"\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}
} 
