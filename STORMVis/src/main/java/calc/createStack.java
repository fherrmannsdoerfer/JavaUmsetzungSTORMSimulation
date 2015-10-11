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



public class CreateStack {
	
	/**
	 * constuctor
	 */
	public CreateStack() {
		
	}
	
	/**
	 * main method to test
	 */
	public static void main(String[] args){ 

    } 
	
	/**
	 * method simulateData creates a simulated data set
	 * @param numberPSF : number of PSFs
	 * @param domainSize : size of the domain area in nanometers
	 * @param domainSizeZ : size of the domain in z-direction
	 * @param frameNumber
	 * @param meanPoissonPSF : mean value of the Poisson-distribution for the intensity of a PSF
	 * @param intensityPerPhoton
	 */
	public static List<float[]> simulateData (int numberPSF, 
			float domainSize, float domainSizeZ, int frameNumber, float meanPoissonPSF, float intensityPerPhoton) {
		List<float[]> dataEvents = new ArrayList<float[]>();
		
		//simulate position of PSFs
		float[][] positions = new float[numberPSF][3]; 
		for(int i = 0; i < numberPSF; i++) {
			positions[i][0] = (float) Math.random()*domainSize;
			positions[i][1] = (float) Math.random()*domainSize;
			positions[i][2] = (float) Math.random()*domainSizeZ; 
		}
		
		//simulate time when PSFs blink
		Random rand = new Random();
		for (int j = 0; j < frameNumber; j++) {
			int a = rand.nextInt(numberPSF);
			float d = (float) calc.RandomClass.poissonNumber(meanPoissonPSF)*intensityPerPhoton; //random intensity
			float[] b = {positions[a][0], positions[a][1], positions[a][2], (float) j, d};
			dataEvents.add(b);
		}
		
		return dataEvents;
	}
	
	/**
	 * method createTiffStack creates a tiff-stack out of a list of points
	 * @param lInput : list of float-arrays as input
	 * @param resolution : resolution, i.e. ratio pixel per nanometers 
	 * @param emptySpace : empty pixels on the edges
	 * @param intensityPerPhoton : measure for the intensity contribution of one photon on a pixel

	 */
	public static void createTiffStack(List<float[]> lInput, float resolution, int emptySpace, float intensityPerPhoton, float meanPoisson) { 
		
		//create underground
		for(int i = 0; i < lInput.size(); i++) {
			lInput.get(i)[4] += (float) calc.RandomClass.poissonNumber(meanPoisson)*intensityPerPhoton; 
		}
		
		//find out minimum x- and y-values			
		float minX = globalMin(lInput, 0); 
		float minY = globalMin(lInput, 1);
		
		//subtract minimum values to obtain coordinates in [0, infty]x[0, infty]
		for (int j = 0; j < lInput.size(); j++) {   	//shifts the window to positive values leaving empty space on the left and lower edge
			lInput.get(j)[0] -= (minX - (emptySpace/resolution));
			lInput.get(j)[1] -= (minY - (emptySpace/resolution));
		}
		
	
		// find out required height and width of the image in nm as global maxima in the list; convert into pixel		
		int pImgWidth = Math.round(globalMax(lInput, 0)*resolution);
		int pImgHeight = Math.round(globalMax(lInput, 1)*resolution);
		
		
		// find out minimum and maximum intensity to get a normalised (?) metric for the grey-value
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
			pro.reset(); 
			
			//goes through real blinking events
			for (int i = 0; i < pointsInFrame.size(); i++) { // goes through list of blinking events
				float[] currentPoint = pointsInFrame.get(i);
				int localX = Math.round(currentPoint[0] / resolution);
				int localY = Math.round(currentPoint[1] / resolution);
				//pro.setColor(value); value is either double or int; rgb with equal values represents a shade of grey
				//pro.drawPixel(localX, localY); 	// draws a point at each blinking event in the frame 
												// TODO: colour to encode intensity; interpolation according to the model 
				pro.setf(localX, localY, currentPoint[4]); //attaches a float value to each pixel
			}
			
			//adds Poissonian underground
			for (int l = 0; l < (pImgWidth + emptySpace); l++) { //goes through whole xy-domain
				for (int k = 0; k < (pImgHeight + emptySpace); k++) {
					float val = 0;
					if (Math.round(pointsInFrame.get(j)[0]) == l && Math.round(pointsInFrame.get(j)[1]) == k) { //if current pixel carries PSF
						val = (float) calc.RandomClass.poissonNumber(meanPoisson)*intensityPerPhoton + pointsInFrame.get(j)[4];
					}
					else {
						val = (float) calc.RandomClass.poissonNumber(meanPoisson)*intensityPerPhoton;
					}
					pro.setf(l, k, val); //attaches a float value to each pixel, sets minInt to 0
				}
			}
			stackLeft.addSlice(pro); // adds a processor for each frame to the stack
		}

		
		//save imagestack
		ImagePlus leftStack = new ImagePlus("", stackLeft);
		FileSaver fs = new FileSaver(leftStack);
		fs.saveAsTiffStack("C:\\Users\\Niels\\Desktop\\Documents\\STORMVis_HiWi\\tiffstack.tif");
	
	}
	
//	/**
//	 * auxiliary method creates a tiff-file out of one single layer of the input float[][]-list PRELIMINARY
//	 * @param mInput : input matrix
//	 * 
//	 */
//	private static NewImage createTiff(float[][] m) {
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
	private static float globalMax(List<float[]> input, int col) {
		float max = (float) -1e19; //initialise max to be very small in order to find real maximum
		for(int i = 0; i < input.size(); i++) {
			if(input.get(i)[col] > max) {
				max = input.get(i)[col];
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
	private static float globalMin(List<float[]> input, int col) {
		float min = (float) 1e19; //initialise max to be very large in order to find real minimum
		for(int i = 0; i < input.size(); i++) {
			if(input.get(i)[col] < min) {
				min = input.get(i)[col];
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
	private static List<float[]> findFrame(List<float[]> input, int frNr) {
		List<float[]> ret = new ArrayList<float[]>();
		for(int i = 0; i < input.size(); i++) {
			if(input.get(i)[3] == frNr) {
				float[] data = {input.get(i)[0], input.get(i)[1], input.get(i)[2], input.get(i)[3], input.get(i)[4]};
				ret.add(data);
			}
		}
		return ret;
	}
	
}
