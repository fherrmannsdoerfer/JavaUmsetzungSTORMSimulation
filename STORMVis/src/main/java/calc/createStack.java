package calc;

import java.util.List;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;

import java.util.Random;

public class createStack {
	
	/**
	 * constuctor
	 */
	public createStack() {
		
	}
	
	/**
	 * main method to test
	 */
	public static void main(String[] args){ 
        
    } 
	
	/**
	 * method createTiffStack creates a tiff-stack out of a list of points
	 * @param lInput : list of 2d-float-arrays as input; PRELIMINARY: one 2d-array
	 * @param nmPRatio : ratio pixel to nanometers 
	 * @param emptySpace : empty pixels on the edge
	 */
	public static void createTiffStack(List<float[][]> lInput, float nmPRatio, int emptySpace) { //Listenformat in z-Richtung oder für einzelne Frames???
		
		// find out minimum x- and y-values, subtract them to obtain strictly positive coordinates		
		subtractCoordinate(lInput.get(0), Calc.min(lInput.get(0), 0), 0);
		subtractCoordinate(lInput.get(0), Calc.min(lInput.get(0), 1), 1);
		
	
		// find out required height and width of the image in nm as global maxima in the list; convert into pixel
		float nmImgWidth = 0;
		float nmImgHeight = 0;
		for(int i = 0; i < lInput.size(); i++) {
			if(Calc.max(lInput.get(i), 0) > nmImgWidth) {
				nmImgWidth = Calc.max(lInput.get(i), 0);
			}		

			if(Calc.max(lInput.get(i), 1) > nmImgHeight) {
				nmImgHeight = Calc.max(lInput.get(i), 1);
			}
		}
		
		int pImgWidth = Math.round(nmImgWidth/nmPRatio);
		int pImgHeight = Math.round(nmImgHeight/nmPRatio);
		
		// create new image stack		
		ImageStack stackLeft = new ImageStack(pImgWidth + emptySpace, pImgHeight + emptySpace);
		for(int k = 0; k < lInput.size(); k++) {
			stackLeft.addSlice(lInput.get(0).getProcessor()); //addSlice erwartet einen Input vom Typ ImageProcessor
		}
		
		ImagePlus leftStack = new ImagePlus("", stackLeft);
		FileSaver fs = new FileSaver(leftStack);
		fs.saveAsTiffStack("c:\\tmp\\tiffstack.tif");
		
		// create Gaussian-distributed underground; parameters: variance, number of points

	}
	
	/**
	 * auxiliary method creates a tiff-file out of one single layer of the input float[][]-list
	 * @param mInput : input matrix
	 * 
	 */
	
	/**
	 * auxiliary method subtractCoordinate adds fixed value to one of the coordinates
	 * @param m : input matrix
	 * @param value : value to subtract
	 * @param coordinate : coordinate, in which the the subtraction takes place
	 * @return returns matrix with subtracted coordinate
	 */
	private static float[][] subtractCoordinate(float[][] m, float value, int coordinate) {
		for(int i=0; i<m.length;i++) {
			m[i][coordinate] = m[i][coordinate] - value;
		}
		return m;
	}
	
	/**
	 * auxiliary method createUnderground creates a random Gauss-distributed underground 
	 * in the domain area
	 * @param var : variance of the distribution 
	 * @return collection of points with normally-distributed underground intensity
	 */
	private static float[][] createUnderground(int nPoints, double var) {
		Random r= new Random();
		float[][] h= new float[nPoints][4];
		for(int i = 0; i < nPoints; i++) {
			h[i][4] = (float)(r.nextGaussian()*var);
		}
		return h;
		
	}
	
}
