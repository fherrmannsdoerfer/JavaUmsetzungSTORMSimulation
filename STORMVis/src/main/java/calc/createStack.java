package calc;

import java.util.List;
import java.util.ArrayList;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;

import ij.*;
import ij.gui.*;
import ij.process.*;
import ij.measure.*;
import java.lang.*;
import java.awt.*;

import ij.util.*;
import ij.plugin.*;
import ij.plugin.filter.*;
import ij.plugin.frame.*;


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
//		createTiffStack(, 100, 5);
    } 
	
	/**
	 * method createTiffStack creates a tiff-stack out of a list of points
	 * @param lInput : list of 2d-float-arrays as input; PRELIMINARY: one 2d-array
	 * @param resolution : resolution, i.e. ratio pixel per nanometers 
	 * @param emptySpace : empty pixels on the edges
	 */
	private static void createTiffStack(List<float[][]> lInput, float resolution, int emptySpace) { 
		
		//find out minimum x- and y-values, subtract them to obtain strictly positive coordinates				
		float minX = globalMin(lInput, 0); //take very large initial values to make sure that 0 is not returned as the false minimum
		float minY = globalMin(lInput, 1);
		
		for (int j = 0; j < lInput.size(); j++) {   	//shifts the window to positive values leaving empty space on the left and lower edge
			subtractCoordinate(lInput.get(j), (minX - (emptySpace/resolution)), 0);
			subtractCoordinate(lInput.get(j), (minY - (emptySpace/resolution)), 1);
		}
		
	
		// find out required height and width of the image in nm as global maxima in the list; convert into pixel		
		int pImgWidth = Math.round(globalMax(lInput, 0)*resolution);
		int pImgHeight = Math.round(globalMax(lInput, 1)*resolution);
		
		
		// find out minimum and maximum intensity to get am metric for the colour
		float maxInt = globalMax(lInput, 4);
		float minInt = globalMin(lInput, 4);
		
		// find out minimum and maximum frame value
		int maxFr = (int) globalMax(lInput, 3);
		int minFr = (int) globalMin(lInput, 3);
		
		// create new image stack	
		ImagePlus imgpls = new ImagePlus(""); //initialises an empty image
		ImageStack stackLeft = new ImageStack(pImgWidth + emptySpace, pImgHeight + emptySpace); // now we have emptySpace on both sides
		ImageProcessor pro = imgpls.getProcessor();
		
		// fill image stack with images
		for (int j = minFr; j < maxFr; j++) {
			List<float[]> pointsInFrame = findFrame(lInput, j); // looks for all points in the slice which have the correct frame number
			for (int i = 0; i < pointsInFrame.size(); i++) { // goes through list of blinking events																										
				float[] currentPoint = pointsInFrame.get(i);
				int localX = Math.round(currentPoint[0] / resolution);
				int localY = Math.round(currentPoint[1] / resolution);
				pro.drawDot2(localX, localY); 	// draws a point at each blinking event in the frame 
												// TODO: coloour to encode intensity; interpolation according to the model
			}
			stackLeft.addSlice(pro); // adds a processor for each frame to the stack
		}

		
		//save imagestack
		ImagePlus leftStack = new ImagePlus("", stackLeft);
		FileSaver fs = new FileSaver(leftStack);
		fs.saveAsTiffStack("c:\\tmp\\tiffstack.tif");
	
	}
	
	/**
	 * auxiliary method creates a tiff-file out of one single layer of the input float[][]-list PRELIMINARY
	 * @param mInput : input matrix
	 * 
	 */
//	private static ImagePlus createTiff(float[][] m) {
//		NewImage im = new NewImage();
//		return im;
//	}


	
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
	 * intensity in the domain area
	 * @param var : variance of the distribution 
	 * @return collection of points with normally-distributed underground intensity
	 */
	private static float[][] createUnderground(int nPoints, double var) {
		Random r= new Random();
		float[][] h= new float[nPoints][4];
		for(int i = 0; i < nPoints; i++) {
			h[i][4] = (float)(r.nextGaussian()*var); //creates Gaussian distributed number in [0,1], multiply by variance
		}
		return h;
		
	}
	
	/**
	 * auxiliary method finds global maximum in the list of arrays for a given column
	 * @param input : input list of arrays/matrices
	 * @param col : column number 
	 * @return max: global maximum
	 */
	private static float globalMax(List<float[][]> input, int col) {
		float max = (float) -1e19; //initialise max to be very small in order to find real maximum
		for(int i = 0; i < input.size(); i++) {
			if(Calc.max(input.get(i), 0) > max) {
				max = Calc.max(input.get(i), col);
			}		
		}
		return max;
	}
	
	/**
	 * auxiliary method finds global minmum in the list of arrays for a given column
	 * @param input : input list of arrays/matrices
	 * @param col : column number 
	 * @return max: global maximum
	 */
	private static float globalMin(List<float[][]> input, int col) {
		float min = (float) 1e19; //initialise max to be very large in order to find real minimum
		for(int i = 0; i < input.size(); i++) {
			if(Calc.max(input.get(i), 0) < min) {
				min = Calc.min(input.get(i), col);
			}		
		}
		return min;
	}
	
	/**
	 * auxiliary method findFrame finds all data sets with a given frame number
	 * @param frNr : frame Number
	 * @param input : input array list
	 * @return returns a list of all data sets 
	 */
	private static List<float[]> findFrame(List<float[][]> input, int frNr) {
		List<float[]> ret = new ArrayList<float[]>();
		for(int i = 0; i < input.size(); i++) {
			float[][] h = input.get(i);
			for(int j = 0; j < h.length; j++) {
				if(h[j][3] == frNr) {
					float[] data = {h[j][0], h[j][1], h[j][2], h[j][3], h[j][4]};
					ret.add(data);
				}
			}
		}
		return ret;
	}
	
}
