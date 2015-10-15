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
		System.out.println(symmInt(2, 2, (float) 2.5, (float) 1.5, 1, 1, 1));
		System.out.println(symmInt(2, 2, (float) 2.5, (float) 2, 1, 1, 1));
		System.out.println(symmInt(2, 2, (float) 2.5, (float) 2.5, 1, 1, 1));
		System.out.println(symmInt(2, 2, (float) 3, (float) 2.5, 1, 1, 1));
		System.out.println(symmInt(2, 2, (float) 3.5, (float) 2.5, 1, 1, 1));
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
	 * @param input : list of float tables as input
	 * @param resolution : resolution, i.e. ratio pixel per nanometres 
	 * @param emptySpace : empty pixels on the edges
	 * @param intensityPerPhoton : measure for the intensity contribution of one photon on a pixel
	 * @param frameRate : rate with which frames are taken
	 * @param decayTime : mean duration of a blinking event
	 * @param sizePSF : size over which the PSF is spread
	 * @param modelNumber : kind of model for the PSF
	 * @parama numerical aperture of the microscope
	 * @param wavelength of the used light
	 * @param zFocus : z-plane, in which the focus lies
	 * @param zDefocus : z-value, for which the microscope defocusses
	 * @param sigmaNoise : sigma of the Gaussian noise in the whole image
	 */
	public static void createTiffStack(List<float[][]> input, float resolution, int emptySpace, 
			float intensityPerPhoton, float meanPoisson, float frameRate, float decayTime, int sizePSF, int modelNumber, 
			float numericalAperture, float waveLength, float zFocus, float zDefocus, float sigmaNoise) { 
		
		//convert List<float[][]> to List<float[]>
		List<float[]> lInput = convertList(input);
		
		//simulate separation in different frames
		float frameTime = 1 / frameRate;
		for (int i = 0; i < lInput.size(); i++) {
			float beginningTime = (float) (Math.random()/frameRate);
			if (beginningTime + decayTime > frameTime) {
				float overlap = (beginningTime + decayTime - frameTime)/decayTime;
				float[] val1 = {lInput.get(i)[0], lInput.get(i)[1], lInput.get(i)[2], lInput.get(i)[3] + 1 , lInput.get(i)[4]*overlap};
				float[] val2 = {lInput.get(i)[0], lInput.get(i)[1], lInput.get(i)[2], lInput.get(i)[3], lInput.get(i)[4]*(1 - overlap)};
				lInput.add(val2);
				lInput.add(val1);
				lInput.remove(i);
			}
		}
		
		//find out minimum x- and y-values			
		float minX = globalMin(lInput, 0); 
		float minY = globalMin(lInput, 1);
		
		//subtract minimum values to obtain coordinates in [0, infty]x[0, infty]
		for (int j = 0; j < lInput.size(); j++) {   	//shifts the window to positive values
			lInput.get(j)[0] -= (minX - ((emptySpace + sizePSF)/resolution));
			lInput.get(j)[1] -= (minY - ((emptySpace + sizePSF)/resolution));
		}
		
	
		// find out required height and width of the image in nm as global maxima in the list; convert into pixel		
		int pImgWidth = Math.round(globalMax(lInput, 0)*resolution);
		int pImgHeight = Math.round(globalMax(lInput, 1)*resolution);
		
		
		// find out minimum and maximum frame value
		int maxFr = (int) globalMax(lInput, 3);
		int minFr = (int) globalMin(lInput, 3);
		
		// create new image stack	
		ImagePlus imgpls = new ImagePlus(""); //initialises an empty image
		ImageStack stackLeft = new ImageStack(pImgWidth + emptySpace + sizePSF, pImgHeight + emptySpace + sizePSF); 
		// now we have emptySpace on both sides
		ImageProcessor pro = imgpls.getProcessor();
		
		// fill image stack with images
		for (int j = minFr; j < maxFr; j++) {
			pro.reset(); 
			List<float[]> pointsInFrame = findFrame(lInput, j); // looks for all points in the slice which have the correct frame number
			
			//modelling the form of the PSF
			for (int i = 0; i < pointsInFrame.size(); i++) {
				int pixelX = Math.round(pointsInFrame.get(i)[0] / resolution);
				int pixelY = Math.round(pointsInFrame.get(i)[1] / resolution);
				switch (modelNumber) {
				case 1: // symmetric Gaussian
					for (int k = -sizePSF; k <= sizePSF; k++) {
						for (int m = -sizePSF; m <= sizePSF; m++) {
							float intensityPhotons = symmInt(pixelX + k, pixelY + m, pointsInFrame.get(i)[0], 
									pointsInFrame.get(i)[1], pointsInFrame.get(i)[4], calcSig(pointsInFrame.get(i)[2], numericalAperture, 
											waveLength, zFocus, zDefocus), resolution)/intensityPerPhoton;
							pro.setf(pixelX + k + emptySpace + sizePSF, pixelY + m + emptySpace + sizePSF, //conserve empty space
									calc.RandomClass.poissonNumber(intensityPhotons)*intensityPerPhoton);
						}
					}
					break;
					
				case 2: //antisymmetric Gaussian
					break;
				}
			}
			
			//add Gaussian underground noise
			Random rand = new Random();
			for (int n = emptySpace; n < pImgWidth + sizePSF; n++){
				for (int p = emptySpace; p < pImgHeight + sizePSF; p++) {
					float val3 = (float) rand.nextGaussian()*sigmaNoise;
					val3 += pro.getf(n, p);
					pro.setf(n, p, val3);
				}
			}
			
			stackLeft.addSlice(pro); // adds a processor for each frame to the stack
		}
		
		

		//save imagestack
		ImagePlus leftStack = new ImagePlus("", stackLeft);
		FileSaver fs = new FileSaver(leftStack);
		fs.saveAsTiffStack("C:\\Users\\Niels\\Desktop\\Documents\\STORMVis_HiWi\\tiffstack.tif");
	
	}
	

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
	 * auxiliary method creates a list out of the list of tables of events
	 * @param input list
	 * @return output list
	 */
	private static List<float[]> convertList (List<float[][]> lInput) {
		List<float[]> ret = new ArrayList<float[]>();
		for(int i = 0; i < lInput.size(); i++) {
			for(int j = 0; j < lInput.get(i).length; j++) {
				float[] val = {lInput.get(i)[j][0], lInput.get(i)[j][1], lInput.get(i)[j][2], lInput.get(i)[j][3], lInput.get(i)[j][4]};
				ret.add(val);
			}
		}
		return ret;
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
	
	/**
	 * auxiliary method calculates a symmetric-Gaussian-distributed 
	 * intensity at the value of integer pixels;
	 * coordinates of a pixel are coordinates of its lower left corner
	 * nevertheless, the intensity is the intensity in the centre of the pixel
	 * @param pX : x-coordinate in pixels
	 * @param pY : y-coordinate in pixels
	 * @param maxX : float-x-cordinate of the maximum in nm
	 * @param maxY : float-y-cordinate of the maximum in nm
	 * @param maxInt : intensity of the maximum at (maxX, maxY)
	 * @param sig : sigma value of the gaussian distribution
	 * @param res : resolution, i.e. ratio pixel per nanometres
	 * @return Gauss-value
	 */
	private static float symmInt(int pX, int pY, float maxX, 
			float maxY, float maxInt, float sig, float res) {
		
		float ret = 0;
		float x = (float) pX;
		float y = (float) pY;
		float dx = (float) ((x + 0.5)/res) - maxX; //calculates difference between maximum of PSF-Gauss and current pixel 
		float dy = (float) ((y + 0.5)/res) - maxY;
		
		double exponent = (double) (Math.pow(dx, 2) + Math.pow(dy, 2))/2 /Math.pow(sig, 2);
		ret = (float) Math.exp(-exponent)*maxInt;
		return ret;
	}

	/**
	 * auxiliary method calculates sigma dependent on z for the symmetric Gaussian
	 * @param maxZ : z-value of the maximum
	 * @param numerical aperture of the microscope
	 * @param Wavelength of the light
	 * @param zFoc : z-plane, in which the focus lies
	 * @param zDefoc : z-value, for which the microscope defocusses
	 * @return sigma for symmetric Gaussian
	 */
	private static float calcSig(float maxZ, float numAperture, float waveLgth, float zFoc, float zDefoc) {
		float s = (float) Math.pow(2, Math.abs((maxZ - zFoc)/(zFoc - zDefoc)));
		s = s*waveLgth/2 /numAperture;
		return s;
	}
	
	
}
