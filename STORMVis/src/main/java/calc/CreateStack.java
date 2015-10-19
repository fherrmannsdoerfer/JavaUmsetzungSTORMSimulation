package calc;

import java.util.List;
import java.util.ArrayList;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;

import ij.process.*;

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
		
		//random creation of a list of tables as input
		List<float[][]> b = new ArrayList<float[][]>();
		for (int i = 0; i < 2; i++) {
			float[][] c = new float[30][5];
			for (int j = 0; j < 3; j++) {
				c[j][0] = (float) (Math.random()*30);
				c[j][1] = (float) (Math.random()*30);
				c[j][2] = (float) (Math.random()*20);
				c[j][3] = (float) (Math.random()*4);
				c[j][4] = (float) (Math.random()*10);
			}
			b.add(c);
		}
		System.out.println("finished simulation");
//		List<float[]> ba = convertList(b);
//		System.out.println("finished conversion");
//		List<float[]> r = distributePSF(ba, 20, (float) 0.2);
//		System.out.println("finished distribution");
		createTiffStack(b, 1 , 10, 2, (float) 0.5, 3, 2, 3, 1, //model nr 1 
				(float) 0.4, 400, 10, 15, 1);
//		ba.addAll(r);
		System.out.println("finished merging");

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
	 * @param numerical aperture of the microscope
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
		System.out.println("finished conversion");
		
		//simulate distribution of the intensity over different frames
		lInput = distributePSF(lInput, frameRate, decayTime);
		System.out.println("finished distribution");
		
		//find out minimum x- and y-values			
		float minX = globalMin(lInput, 0); 
		float minY = globalMin(lInput, 1);
		
		//subtract minimum values to obtain coordinates in [0, infty]x[0, infty]
		for (int j = 0; j < lInput.size(); j++) {   	//shifts the window to positive values
			lInput.get(j)[0] -= minX; 
			lInput.get(j)[0] +=	(emptySpace + sizePSF)/resolution;
			lInput.get(j)[1] -= minY;
			lInput.get(j)[1] += (emptySpace + sizePSF)/resolution;
		}
		
	
		//find out required height and width of the image in nm as global maxima in the list; convert into pixel		
		int pImgWidth = (int) (globalMax(lInput, 0)*resolution);
		pImgWidth++; //ensures rounding up
		int pImgHeight = (int) (globalMax(lInput, 1)*resolution);
		pImgHeight++;
		
		//create new image stack	
		//ImagePlus imgpls = new ImagePlus(""); //initialises an empty image
		ImageStack stackLeft = new ImageStack(pImgWidth + emptySpace + sizePSF, pImgHeight + emptySpace + sizePSF); //emptySpace on both sides
		System.out.println("finished initialisation of image devices");
		
		//performing sorting operation by quicksort algorithm
		SortClass s = new SortClass(lInput);
		s.quickSort(0, lInput.size() - 1);
		lInput = s.getList(); 
		
		//find out minimum and maximum frame value
		int maxFr = (int) lInput.get(lInput.size()-1)[3];
		int minFr = (int) lInput.get(0)[3];
		
		// fill image stack with images
		for (int j = minFr; j < maxFr; j++) {
			//ImagePlus imgpls = new ImagePlus(""); //initialises an empty image
			FloatProcessor pro = new FloatProcessor(pImgWidth + emptySpace + sizePSF, pImgHeight + emptySpace + sizePSF);
			
			//modelling the form of the PSF
			for (int i = 0; i < lInput.size(); i++) {
				int pixelX = Math.round(lInput.get(i)[0] * resolution);
				int pixelY = Math.round(lInput.get(i)[1] * resolution);
				if(modelNumber == 1) { // symmetric Gaussian
					for (int k = -sizePSF; k <= sizePSF; k++) {
						for (int m = -sizePSF; m <= sizePSF; m++) {
							float intensityPhotons = symmInt(pixelX + k, pixelY + m, lInput.get(i)[0], 
									lInput.get(i)[1], lInput.get(i)[4], calcSig(lInput.get(i)[2], numericalAperture, 
											waveLength, zFocus, zDefocus), resolution)/intensityPerPhoton;
							float val4 = pro.getf(pixelX + k, pixelY + m); 
							val4 += calc.RandomClass.poissonNumber(intensityPhotons)*intensityPerPhoton;
							pro.setf(pixelX + k, pixelY + m, val4); //exception, probably out of bounds
						}
					}
				}
				else { 
					if(modelNumber == 2) { //calibration file Gaussian
						
					}
					else {
						System.out.println("model number not valid");
						return;
					}
				}
			}
					
			pro.noise(sigmaNoise); //add Gaussian underground noise
			
			stackLeft.addSlice(pro); // adds a processor for each frame to the stack
		}
		System.out.println("finished procession");
		

		//save imagestack
		ImagePlus leftStack = new ImagePlus("", stackLeft);
		FileSaver fs = new FileSaver(leftStack);
		fs.saveAsTiffStack("C:\\Users\\Niels\\Desktop\\Documents\\STORMVis_HiWi\\tiffstack3.tif");
		System.out.println("file succesfully saved");
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
	
	/**
	 * auxiliary method distributePSF distributes the PSF in an amount different frames
	 * initialisation of new ArrayList necessary since adding elements to a non-specified list is not possible
	 * @param iInp : input list 
	 * @param frRate : rate in which frames are taken
	 * @param decTime : time interval, over which the PSF distributes its intensity
	 * @return new list with PSF spread over different frames
	 */
	private static List<float[]> distributePSF(List<float[]> lInp, float frRate, float decTime) {
		float frameTime = 1/frRate;
		
		List<float[]> ret = new ArrayList<float[]>(); //new list
		
		for(int i = 0; i < lInp.size(); i++) {
			//simulation of a random beginning time
			float beginningTime = (float) (Math.random()/frRate);
			float t = beginningTime + decTime;
			//spreads the intensity of the PSF on arbitrarily many frames
			if(t > frameTime) {
				float d = decTime;
				float overlap1 = (frameTime - beginningTime)/decTime; // fraction of decay time in original frame
				float[] val1 = {lInp.get(i)[0], lInp.get(i)[1], lInp.get(i)[2], lInp.get(i)[3],
						lInp.get(i)[4]*overlap1};
				d -= frameTime; 
				d += beginningTime;
				ret.add(val1);
				float overlap2 = frameTime/decTime; //fraction full frame over decay time
				int n = 0;
				while (d > frameTime) {
					float[] val2 = {lInp.get(i)[0], lInp.get(i)[1], lInp.get(i)[2], lInp.get(i)[3] + n + 1,
							lInp.get(i)[4]*overlap2};
					d = d - frameTime;
					n++;
					ret.add(val2);
				}
				float overlap3 = d/decTime; //fraction of the rest
				float[] val3 = {lInp.get(i)[0], lInp.get(i)[1], lInp.get(i)[2], lInp.get(i)[3] + n + 1,
						lInp.get(i)[4]*overlap3};
				ret.add(val3);
			}
		}
		return ret;
	}
	

}
