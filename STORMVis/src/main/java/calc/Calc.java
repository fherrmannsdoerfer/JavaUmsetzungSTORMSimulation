package calc;
import gnu.trove.list.array.TFloatArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.vecmath.Vector3d;

import model.DataSet;
import model.LineDataSet;
import model.TriangleDataSet;

import org.apache.commons.lang3.ArrayUtils;
import org.javatuples.Pair;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;


public class Calc { 

	/**
	 * @param args
	 */
	public Calc() {
		
	}
	
	/**
	 * @brief converts ArrayList with float[][] to float[][][] (containing triangles)
	 * @param triangles
	 * @return triangles as 3d-matrix
	 */
	
	public static float[][][] getMatrix(List<float[][]> triangles) {
		long start = System.nanoTime();
		float[][][] trMatrix = new float[triangles.size()][3][3];
		for(int j = 0; j < triangles.size();j++) {
			for(int i = 0; i<3;i++) {
				float[][] p = triangles.get(j);
				trMatrix[j][i][0] = p[i][0];
				trMatrix[j][i][1] = p[i][1];
				trMatrix[j][i][2] = p[i][2];
			}
		}
		long time = System.nanoTime() - start;
//		System.out.println("Converting to new matrix form: " + time/1e9 +"s");
		return trMatrix;
	}
	
	/**
	 * function to print a 3d matrix to the console
	 * @param array - 3d matrix
	 * prints a 3d matrix, iteration by "page"
	 */
	public static void printMatrix(float[][][] array) {
		System.out.println("-- matrix --");
		int size = array.length;
		System.out.println("total size: "+size);
		for (int i = 0; i<size; i++) {
			System.out.println("i: " + i);
			for (int j = 0; j<3;j++) {
				for(int c = 0; c<3;c++) {
					System.out.print(array[i][j][c]+" ");
				}
				System.out.print("\n");
			}
			System.out.print("\n");
		}
	}
	
	/**
	 * 
	 * @param vec - vector
	 * prints the column/row vector
	 */
	
	public static void printVector(float[] vec) {
		System.out.println("-- vector --");
		for(int i = 0; i<vec.length;i++) {
			System.out.println(vec[i]);
		}
		System.out.println("");
	}
	
	/**
	 * 
	 * @param array - 2d matrix
	 * prints the rows and columns of a 2d matrix
	 */
	
	public static void print2dMatrix(float[][] array) {
		System.out.println("-- matrix --");
		int size = array.length;
		int size2 = array[0].length;
		System.out.println("total size: "+size);
		for (int j = 0; j<size;j++) {
			for(int c = 0; c<size2;c++) {
				System.out.print(array[j][c]+" ");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
	
	/**
	 * 
	 * @param tr - 3d matrix with triangles
	 * @return area of all triangles
	 */
	
	public static float[] getAreas(float[][][] tr) {
		long start = System.nanoTime();
		int size = tr.length;
		float[] areas = new float[size];
		for (int i = 0; i<size; i++) {
			float[] vec1 = new float[3];
			float[] vec2 = new float[3];
			vec1[0] = tr[i][1][0] - tr[i][0][0];
			vec1[1] = tr[i][1][1] - tr[i][0][1];
			vec1[2] = tr[i][1][2] - tr[i][0][2];
			
			vec2[0] = tr[i][2][0] - tr[i][0][0];
			vec2[1] = tr[i][2][1] - tr[i][0][1];
			vec2[2] = tr[i][2][2] - tr[i][0][2];
			areas[i] = (float) (getNorm(getCross(vec1,vec2))*0.5);
		}
		long time = System.nanoTime() - start;
		System.out.println("Calculating all areas: " + time/1e9+"s");
		return areas;
	}
	
	/**
	 * 
	 * @param vec
	 * @return |vec|
	 */
	
	public static float getNorm(float[] vec) {
		float sum = 0;
		for (int i = 0; i < vec.length; i++) {
			sum += Math.pow(vec[i], 2);
		}
		if (Float.isNaN(sum)){
			return 0;
		}
		return (float) Math.sqrt(sum);
	}
	
	/**
	 * 
	 * @param vec
	 * @param vec2
	 * @return vec * vec2
	 */
	
	public static float getDot(float[] vec, float[] vec2) {
		return (vec[0]*vec2[0]+vec[1]*vec2[1]+vec[2]*vec2[2]);
	}
	
	/**
	 * 
	 * @param vec
	 * @return -vec
	 */
	
	public static float[] getNegativeVec(float[] vec) {
		float[] negVec = new float[vec.length];
		for(int c = 0; c < vec.length;c++) {
			negVec[c] = -vec[c];
		}
		return negVec;
	}
	
	/**
	 * @param vec
	 * @return vec/|vec|
	 */
	
	public static float[] scaleToOne(float[] vec) {
		float[] normVec = new float[vec.length];
		float norm = getNorm(vec);
		for(int c = 0; c < vec.length;c++) {
			normVec[c] = vec[c]/norm;
		}
		return normVec;
	}
	
	/**	
	 * 
	 * @param m1
	 * @param m2
	 * @return m1 + m2
	 */
	public static float[] vectorAddition(float[] m1, float[] m2) {
		float[] result = new float[m1.length];
		for (int i = 0; i < m1.length;i++) {
				result[i] = m1[i] + m2[i];
				if(Float.isNaN(m1[i]) || Float.isNaN(m2[i])) {
					System.out.println("NAN in vector addition");
				}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param vec1 
	 * @param vec2
	 * @return vec1 x vec2
	 */
	public static float[] getCross(float[] vec1,float[] vec2) {
		Vector3d v1 = new Vector3d();
		v1.x = (double) vec1[0];
		v1.y = (double) vec1[1];
		v1.z = (double) vec1[2];
		
		Vector3d v2 = new Vector3d();
		v2.x = (double) vec2[0];
		v2.y = (double) vec2[1];
		v2.z = (double) vec2[2];
		
		v1.cross(v1, v2);
		float[] result = {(float) v1.x,(float) v1.y,(float) v1.z};
		v1 = null;
		v2 = null;
		return result;
	}
	
	/**
	 * 
	 * @param v1 - first vector
	 * @param v2 - second vector
	 * @return difference between both vertices
	 */
	public static float[] difference(float[] v1, float[] v2) {
		float[] result = new float[v1.length];
		for(int i = 0; i < v1.length; i++) {
			result[i] = v2[i]- v1[i];
		}
		return result;
	}
	
	/**
	 * 
	 * @param array
	 * @return sum of array elements
	 */
	public static float sum(float[] array) {
		float sum = 0;
		for (int i = 0;i < array.length;i++) {
			sum += array[i];
		}
		return sum;
	}
	
	/**
	 * @brief triangle vertices
	 * 
	 * Calculates and returns the border vertices of every triangle in tr
	 * @param tr
	 * @return Pair with first and second border vertex
	 */
	
	public static Pair<float[][],float[][]> getVertices(float[][][] tr) {
		float[][] vec1 = new float[tr.length][3];
		float[][] vec2 = new float[tr.length][3];
		
		for (int i = 0; i < tr.length; i++) {
			vec1[i][0] = tr[i][1][0] - tr[i][0][0];
			vec1[i][1] = tr[i][1][1] - tr[i][0][1];
			vec1[i][2] = tr[i][1][2] - tr[i][0][2];

			vec2[i][0] = tr[i][2][0] - tr[i][0][0];
			vec2[i][1] = tr[i][2][1] - tr[i][0][1];
			vec2[i][2] = tr[i][2][2] - tr[i][0][2];
		}	
		return new Pair<float[][], float[][]>(vec1,vec2);
	}
	
	/**
	 * function finds vector for surface data input. The angle between the
	 * surface normal and this vector can be specified. The additional 
	 * degree of freedom is defined by the third parameter.
	 * @param aoa
	 * @param length
	 * @return vector with antibody angle and length for triangles and lines
	 */
	
	public static float[] getVectorTri(float aoa, float length, float alpha) {
		double x = Math.cos(aoa)*Math.cos(alpha);
		double y = Math.cos(aoa)*Math.sin(alpha);
		double z = Math.sin(aoa);
		float[] vec = {(float) (x*length),(float) (y*length),(float) (z*length)};
		return vec;
	}
	
	/**
	 * function finds vector for line data input. The angle between the
	 * surface normal and this vector can be specified. The additional 
	 * degree of freedom is defined by the third parameter.
	 * @param aoa
	 * @param length
	 * @param alpha
	 * @return
	 */
	public static float[] getVectorLine(float aoa, float length, float alpha) {
		double z = Math.sin(aoa)*Math.sin(alpha);
		double y = Math.sin(aoa)*Math.cos(alpha);
		double x = 0;
		if (aoa > Math.PI/2){
			x= -Math.sqrt(1-y*y-z*z);
		}
		else {
			x = Math.sqrt(1-y*y-z*z);
		}
		float[] vec = {(float) (x*length),(float) (y*length),(float) (z*length)};
		return vec;
	}

	
	/**
	 * Matrix multiplication with vector
	 * @param m : matrix
	 * @param vec : vector
	 * @return m x vec
	 */
	public static float[] applyMatrix(float[][] m, float[] vec) {
		float[] result = new float[vec.length];
		for (int i = 0; i < m.length;i++) {
			for (int j = 0; j < m.length; j++) {
				result[i] += vec[j]*m[i][j];
				if(Float.isNaN(result[i])) {
					System.out.println("NAN in matrix multiplication");
				}
			}
		}
		return result;
	}
	
	/**
	 * Simple matrix multiplication
	 * @param m1 : first matrix
	 * @param m2 : second matrix
	 * @return m1 x m2
	 */
	
	public static float[][] matrixMultiply(float[][] m1, float[][] m2) {
		float[][] result = new float[m1.length][m1.length];
		for (int i = 0; i < m1.length;i++) {
			for (int j = 0; j < m1.length; j++) {
				float sum = 0;
				for (int c = 0; c < m1.length; c++) {
					sum += m1[i][c]*m2[c][j];
				}
				result[i][j] = sum;
			}
		}
		return result;
	}
	
	/**
	 * Matrix addition
	 * @param m1 : first matrix
	 * @param m2 : second matrix
	 * @return m1 + m2
	 */
	
	public static float[][] matrixAddition(float[][] m1, float[][] m2) {
		float[][] result = new float[m1.length][m1.length];
		for (int i = 0; i < m1.length;i++) {
			for (int j = 0; j < m1.length; j++) {
				result[i][j] = m1[i][j] + m2[i][j];
			}
		}
		return result;
	}
	
	/**
	 * Division of matrix by real number
	 * @param m1 : matrix
	 * @param div : real number
	 * @return result of devision m1/div
	 */
	
	public static float[][] matrixDivide(float[][] m1, float div) {
		float[][] result = new float[m1.length][m1.length];
		for (int i = 0; i < m1.length;i++) {
			for (int j = 0; j < m1.length; j++) {
				result[i][j] = m1[i][j]/div;
			}
		}
		return result;
	}
	
	/**
	 * function multiplies vector with real value
	 * @param vec
	 * @param multi
	 * @return scaled vector
	 */
	public static float[] multiplyVector(float[] vec, float multi) {
		float[] result = new float[vec.length];
		for(int i = 0; i < vec.length; i++) {
			result[i] = vec[i] *multi;
		}
		return result;
	}
	
	/**
	 * Transpose matrix
	 * @param original
	 * @return original^t
	 */
	
	public static float[][] transpose(float[][] original) {
		float[][] result = new float[original[0].length][original.length];
        if (original.length > 0) {
            for (int i = 0; i < original[0].length; i++) {
                for (int j = 0; j < original.length; j++) {
                    result[i][j] = original[j][i];
                }
            }
        }
        return result;
    }
	
			
	/**
	 * Random vector multiplied with multiplier
	 * @param dimension
	 * @param multiplier
	 * @return random Vector * multiplier
	 */
	
	public static float[] randVector(int dimension, float min, float max,STORMCalculator calc) {
		float[] result = new float[dimension];
		for(int i = 0; i < dimension; i++) {
			result[i] = (float) calc.random.nextFloat() * (max - min) + min;
		}
		return result;
	}
	
	/*
	 * STORM helper
	 * 
	 */
	
	/**
	 * 
	 * @param f - matrix
	 * @param coord - x,y,z = 0,1,2 etc.
	 * @return minimum in f
	 */
	
	public static float min(float[][] f, int coord) {
		f = Calc.transpose(f);
		List<Float> list = Arrays.asList(ArrayUtils.toObject(f[coord]));
		Float min = Collections.min(list);
		return min.floatValue();
	}
	
	/**
	 * 
	 * @param f - matrix 
	 * @param coord - x,y,z, etc.
	 * @return
	 */
	
	public static float max(float[][] f, int coord) {
		f = Calc.transpose(f);
//		List<Float> list = Arrays.asList(ArrayUtils.toObject(f[coord]));
//		Float min = Collections.max(list);
		float[] array = f[coord];
		float max = array[0];
	      for (int j = 1; j < array.length; j++) {
	          if (Float.isNaN(array[j])) {
	        	  System.out.println("NAN FOUND!");
	              return Float.NaN;
	          }
	          if (array[j] > max) {
	              max = array[j];
	          }
	      }
		return max;
	}
	
	/**
	 * determine largest value of int array
	 * @param f : int array
	 * @return max value of given array
	 */
	public static float max(int[] f) {
		List<Integer> list = Arrays.asList(ArrayUtils.toObject(f));
		Integer min = Collections.max(list);
		return min.intValue();
	}
	
	/**
	 * determine largest value of float array
	 * @param f : float array
	 * @return largest value of given array
	 */
	public static float max(float[] f) {
		List<Float> list = Arrays.asList(ArrayUtils.toObject(f));
		Float max = Collections.max(list);
		return max.floatValue();
	}
	
	/**
	 * 
	 * @param m - initial matrix
	 * @param line - new line
	 * @return m with appended line
	 */
	
	public static float[][] appendLine(float[][] m, float[] line) {
		if (m.length == 0){
			float[][] copy = new float[1][line.length];
			copy[0] = line;
			return copy;
		}
		float[][] copy = new float[m.length+1][m[0].length];
    	System.arraycopy(m, 0, copy, 0, m.length);
    	copy[m.length] = line;
    	m = copy;
		return m;
	}
	
	/**
	 * 
	 * @param m - initial matrix
	 * @param col - new column
	 * @return m with appended col
	 */
	
	public static float[][] appendColumn(float[][] m, float[] col) {
		m = Calc.transpose(m);
		float[][] copy = new float[m.length+1][m[0].length];
    	System.arraycopy(m, 0, copy, 0, m.length);
    	copy[m.length] = col;
    	m = Calc.transpose(copy);
		return m;
	}
	
	public static float[][] removeDeletedLines(float[][] m) {
		List<float[]> list = new ArrayList<float[]>();
		for (int i = 0; i < m.length; i++) {
			if(m[i][0] != -1 && m[i][1] != -1) {
				list.add(m[i]);
			}
		}
		float[][] result = toFloatArray(list);
		return result;
	}
		
	/**
	 * returns column specified in second argument from 2d float array
	 * @param m : float array
	 * @param col : index of column to return
	 * @return column to return
	 */
	public static float[] getColumn(float[][] m, int col) {
		m = Calc.transpose(m);
		return m[col];
	}
	
	/**
	 * converts list of float arrays to 2d float array
	 * @param f : list of 1d float arrays
	 * @return 2d float array
	 */
	
	public static float[][] toFloatArray(List<float[]> f) {
		if (f.size()==0){
			return new float[0][0];
		}
		float[][] result = new float[f.size()][f.get(0).length];
		for (int i = 0; i < result.length; i++) {
			result[i] = f.get(i);
		}
		return result;
	}
	
	/**
	 * fills the three individual rgb channels of the color bar in a continuous fashion
	 * @param colorBar : Arraylist of 2d float arrays representing the 3 color channels
	 * @param zmin : minimal z value represented by the color bar
	 * @param zmax : maximal z value represented by the color bar
	 * @param colorProof : boolean value to chose between colors with improved contrast for red/green blind people
	 * @return filled color bar
	 */
		
	public static ArrayList<float[][]> fillColorBar(ArrayList<float[][]> colorBar, 
			float zmin, float zmax, boolean colorProof){
		int width = colorBar.get(0)[0].length;
		int height = colorBar.get(0).length;
		float interval = (zmax-zmin)/height;
		for (int i = 0; i<height; i++){
			for (int j =0; j<width; j++){
				for (int k = 0; k<3; k++){
					colorBar.get(k)[i][j] = getColor(i*interval, zmax-zmin,k, colorProof);
				}
			}
		}
		return colorBar;
	}
	
	/**
	 * this function samples a Gaussian with custom width at the center of each localization
	 * @param coloredImage : List of 2d float arrays representing the rgb channels
	 * @param sigma : width of the Gaussian in nm
	 * @param filterwidth : width of the region in which the Gaussian is sampled in pixels.
	 * @param pixelsize : pixelsize in nm for the output image
	 * @param sd : 2d array of the localizations
	 * @param mode : defines which projection is rendered
	 * @param borders : borders defining which part of the data is rendered
	 * @param dims : dimension of the data (xmin, xmax, ymin,..., zmax)
	 * @param colorProof : boolean value to chose between colors with improved contrast for red/green blind people
	 * @return list of 2d float arrays representing the rgb channels
	 */
	public static ArrayList<float[][]> addFilteredPoints3D(ArrayList<float[][]> coloredImage, double sigma, int filterwidth, 
			double pixelsize, float[][] sd, int mode, ArrayList<Float> borders, ArrayList<Float> dims, boolean colorProof){
		if (filterwidth %2 == 0) {System.err.println("filterwidth must be odd");}
		double factor = 10000*1/(2*Math.PI*sigma*sigma);
		double factor2 = -0.5/sigma/sigma;

		float xMin = dims.get(0);
		float xMax = dims.get(1);
		float yMin = dims.get(2);
		float yMax = dims.get(3);
		float zMin = dims.get(4);
		float zMax = dims.get(5);

		zMax = zMax - zMin;
		
		System.out.println("zMax: "+zMax+" zMin: "+zMin);
		float[][] redChannel = coloredImage.get(0);
		float[][] greenChannel = coloredImage.get(1);
		float[][] blueChannel = coloredImage.get(2);
		for (int i = 1; i<sd.length; i++){
			float[] sl = sd[i];//.get(i);
			if (sl[0]>borders.get(0)&&sl[0]<borders.get(1)&&sl[1]>borders.get(2)&&sl[1]<borders.get(3)&&sl[2]>borders.get(4)&&sl[2]<borders.get(5)){
				double posX = 0;
				double posY = 0;
				double posZ = 0;
				switch (mode){
					case 1:
						posX = (sl[0]-xMin)/pixelsize; //position of current localization
						posY = (sl[1]-yMin)/pixelsize;
						posZ = (sl[2])-zMin;
						break;
					case 2:
						posX = (sl[0]-xMin)/pixelsize; //position of current localization
						posY = (sl[2]-zMin)/pixelsize;
						posZ = (sl[1])-zMin;
						break;
					case 3:
						posX = (sl[1]-yMin)/pixelsize; //position of current localization
						posY = (sl[2]-zMin)/pixelsize;
						posZ = (sl[0])-zMin;
						break;
				}
							
				int pixelXStart = (int)Math.floor(posX) - (filterwidth-1)/2;
				int pixelYStart = (int)Math.floor(posY) - (filterwidth-1)/2;
				float intensity = sl[4];
				for (int k = pixelXStart; k<pixelXStart+ filterwidth;k++){
					for(int l= pixelYStart; l<pixelYStart+ filterwidth;l++){
						double kk = 1;
						try{
							
							float weight = (float) (factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							redChannel[k][l] = redChannel[k][l] + getColor(posZ,zMax,0,colorProof) * weight;
							greenChannel[k][l] = greenChannel[k][l] +getColor(posZ,zMax,1,colorProof) * weight;
							blueChannel[k][l] = blueChannel[k][l] +getColor(posZ,zMax,2,colorProof) * weight;
							
							
						} catch(IndexOutOfBoundsException e){e.toString();}
					}
				}
			}
		}
		removeBrightestSpots(redChannel,greenChannel,blueChannel,0.9999);
		float max= 0;
		float min = (float) 1e199;
		for (int i = 0; i<redChannel.length;i++){
			for(int j = 0; j<redChannel[0].length; j++){
				max = Math.max(redChannel[i][j],max);
				max = Math.max(greenChannel[i][j],max);
				max = Math.max(blueChannel[i][j],max);
				min = Math.min(redChannel[i][j],min);
				min = Math.min(greenChannel[i][j],min);
				min = Math.min(blueChannel[i][j],min);
			}
		}
		for (int i = 0; i<redChannel.length;i++){
			for(int j = 0; j<redChannel[0].length; j++){
				redChannel[i][j] = (redChannel[i][j]-min)/(max-min) *65535.0f;
				greenChannel[i][j] = (greenChannel[i][j]-min)/(max-min) *65535.0f;
				blueChannel[i][j] = (blueChannel[i][j]-min)/(max-min) *65535.0f;
			}
		}

	
		return coloredImage;
	}
	
	/**
	 * function which overrides the largest intensity values based on given percentile
	 * @param redChannel : 2d array representing the red channel of the image
	 * @param greenChannel : 2d array representing the green channel of the image
	 * @param blueChannel : 2d array representing the blue channel of the image
	 * @param percentile : percentile defining how many percent of the intensities stay unaltered
	 */
	private static void removeBrightestSpots(float[][] redChannel,
			float[][] greenChannel, float[][] blueChannel, double percentile) {
		ArrayList<Float> allPoints = new ArrayList<Float>();
		for (int i = 0; i<redChannel.length;i++){
			for(int j = 0; j<redChannel[0].length; j++){
				if (redChannel[i][j]>0){
					allPoints.add(redChannel[i][j]);
				}
				if (greenChannel[i][j]>0){
					allPoints.add(greenChannel[i][j]);
				}
				if (blueChannel[i][j]>0){
					allPoints.add(blueChannel[i][j]);
				}
			}
		}
		Collections.sort(allPoints);
		if ((int)(allPoints.size()*percentile)<allPoints.size()){
			float newMaxVal = allPoints.get(((int)(allPoints.size()*percentile)));
			for (int i = 0; i<redChannel.length;i++){
				for(int j = 0; j<redChannel[0].length; j++){
					redChannel[i][j] = Math.min(redChannel[i][j], newMaxVal);
					greenChannel[i][j] = Math.min(greenChannel[i][j], newMaxVal);
					blueChannel[i][j] = Math.min(blueChannel[i][j], newMaxVal);
				}
			}
		}
		else {
			//do nothing
		}
		
		
	}
	
	/**
	 * returns intensity value based on z position and specified color channel
	 * @param posZ : z value of current localization
	 * @param zMax : maximal z value(0 is assumed to be the lower bound)
	 * @param color : 0 = red channel, 1 = green, 2 = blue
	 * @return intensity value based on z position and specified color
	 */
	private static float getColor(double posZ, float zMax, int color) {
		return getColor(posZ, zMax,color, false);
	}
	
	/**
	 * returns intensity value based on z position and specified color channel
	 * @param posZ : z value of current localization
	 * @param zMax : maximal z value(0 is assumed to be the lower bound)
	 * @param color : 0 = red channel, 1 = green, 2 = blue
	 * @param redGreenBlindProof : boolean value to chose between colors with improved contrast for red/green blind people
	 * @return intensity value based on z position and specified color
	 */
	private static float getColor(double posZ, float zMax, int color, boolean redGreenBlindProof) {

		boolean switchColorProof = false;
		if (redGreenBlindProof){
			if (switchColorProof){
				if (posZ < 0.5* zMax){
					//green rises from 0 to 1 blue stays one
					if (color == 1){
						return (float)(2*posZ/zMax);
					}
					if (color == 2){
						return (float) 1;//(1-3*(posZ/zMax));// 1;//(2 - 4*posZ/zMax)	;
					}
				}
				else {
					//green stays one, blue goes to zero again
					if (color == 1){
						return (float) 1;//(4*posZ/zMax - 2);
					}
					if (color == 2){
						return (float) (2 - 2*posZ/zMax);
					}
				}
			}
			else{
				if (color == 0){
					return (float) (posZ/zMax *(86-230) + 230);
				}
				if (color == 1){
					return (float) (posZ/zMax *(180-159) + 159);
				}
				if (color == 2){
					return (float) (posZ/zMax *(233-0) + 0);
				}
			}
		}
		else{
			if (posZ < 0.3333333* zMax){
				//green rises from 0 to 1 blue stays one
				if (color == 1){
					return (float)(3*posZ/zMax);
				}
				if (color == 2){
					return (float) 1;//(1-3*(posZ/zMax));// 1;//(2 - 4*posZ/zMax)	;
				}
			}
			else if (posZ < 0.6666666* zMax){
				//green stays one, blue goes to zero again
				if (color == 1){
					return (float) 1;//(4*posZ/zMax - 2);
				}
				if (color == 2){
					return (float) (2 - 3*posZ/zMax);
				}
			}
			else {
				//green goes to zero red rises
				if (color == 0){
					return (float) (3*posZ/zMax - 2);
				}
				if (color == 1){
					return (float) (3-3*posZ/zMax);
				}
				if (color == 2){
					return (float) (3*posZ/zMax - 2); //changes red to magenta
				}
			}
		}
		return 0;
	}

	/**
	 * this function samples a Gaussian with custom width at the center of each localization
	 * @param image : float array representing one channel of a gray scale image
	 * @param @param sigma : width of the Gaussian in nm
	 * @param filterwidth : width of the region in which the Gaussian is sampled in pixels.
	 * @param pixelsize : pixelsize in nm for the output image
	 * @param sd : 2d array of the localizations
	 * @param mode : defines which projection is rendered
	 * @param borders : borders defining which part of the data is rendered
	 * @param dims : dimension of the data (xmin, xmax, ymin,..., zmax)
	 * @param shifts : used to ensure the same coordinate system for all rendered images 
	 * @return rendered image
	 */
	public static float[][] addFilteredPoints(float[][] image, double sigma, int filterwidth, 
			double pixelsize, float[][] sd, int mode, ArrayList<Float> dims,
			ArrayList<Float> borders, float[] shifts){
		if (filterwidth %2 == 0) {System.err.println("filterwidth must be odd");}
		
		double factor = 100*1/(2*Math.PI*sigma*sigma);
		double factor2 = -0.5/sigma/sigma;
		//System.out.println(sd.getSize());
		for (int i = 1; i<sd.length; i++){
			float[] sl = sd[i];//.get(i);
			double posX = 0;
			double posY = 0;
			switch (mode){
				case 1:
					posX = (sl[0]+shifts[0])/pixelsize+filterwidth; //position of current localization
					posY = (sl[1]+shifts[1])/pixelsize+filterwidth;
					break;
				case 2:
					posX = (sl[0]+shifts[0])/pixelsize+filterwidth; //position of current localization
					posY = (sl[2]+shifts[2])/pixelsize+filterwidth;
					break;
				case 3:
					posX = (sl[1]+shifts[1])/pixelsize+filterwidth; //position of current localization
					posY = (sl[2]+shifts[2])/pixelsize+filterwidth;
					break;
			}
			
			int pixelXStart = (int)Math.floor(posX) - (filterwidth-1)/2;
			int pixelYStart = (int)Math.floor(posY) - (filterwidth-1)/2;
			for (int k = pixelXStart; k<pixelXStart+ filterwidth;k++){
				for(int l= pixelYStart; l<pixelYStart+ filterwidth;l++){
					try{
						if (sl[0]>borders.get(0)&&sl[0]<borders.get(1)&&sl[1]>borders.get(2)&&sl[1]<borders.get(3)&&sl[2]>borders.get(4)&&sl[2]<borders.get(5)){
							image[k][l] = image[k][l] + (float)(factor* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
						}
							//System.out.println("factor: "+factor+" k: "+k+" l: "+l+"posX: "+posX+"posY: "+posY+" image[k][l]" +image[k][l]+" res: "+(float)(factor*intensity* Math.exp(-0.5/sigma/sigma*(Math.pow((k-posX),2)+Math.pow((l-posY),2)))));
					} catch(IndexOutOfBoundsException e){e.toString();}
				}
			}
		}
		return image;
	}

	/**
	 * function to calculate how much the coordinates of the given line data set have to be shifted to be centered
	 * @param data : filamentous data 
	 * @return list of shifts
	 */
	public static ArrayList<Float> findShiftLines(List<ArrayList<Coord3d>> data) {
		ArrayList<Float> shifts = new ArrayList<Float>();
		ArrayList<Float> dims = new ArrayList<Float>();
		dims = findDimsLines(data);
		shifts.add((dims.get(1)+dims.get(0))/2.f);
		shifts.add((dims.get(3)+dims.get(2))/2.f);
		shifts.add((dims.get(5)+dims.get(4))/2.f);
		return shifts;
	}
	
	/**
	 * function to find the bounding box for filamentous input
	 * @param data list of list of points representing a list of lines given by points
	 * @return arraylist containing the min/max values for all dimensions
	 */
	public static ArrayList<Float> findDimsLines(List<ArrayList<Coord3d>> data){
		ArrayList<Float> dims = new ArrayList<Float>();
		float minx = Float.MAX_VALUE;
		float maxx = -Float.MAX_VALUE;
		float miny = Float.MAX_VALUE;
		float maxy = -Float.MAX_VALUE;
		float minz = Float.MAX_VALUE;
		float maxz = -Float.MAX_VALUE;
		for (ArrayList<Coord3d> list: data){
			for (Coord3d cord:list){
				if (cord.x<minx){
					minx = cord.x;
				}
				if (cord.x>maxx){
					maxx = cord.x;
				}
				if (cord.y<miny){
					miny = cord.y;
				}
				if (cord.y>maxy){
					maxy = cord.y;
				}
				if (cord.z<minz){
					minz = cord.z;
				}
				if (cord.z>maxz){
					maxz = cord.z;
				}
			}
		}
		dims.add(minx);
		dims.add(maxx);
		dims.add(miny);
		dims.add(maxy);
		dims.add(minz);
		dims.add(maxz);
		return dims;
	}

	/**
	 * function finds how surface models have to be shifted to be centered 
	 * @param primitives list of 2d float arrays representing individual triangles
	 * @return list containing the amount of shift for each dimension
	 */
	public static ArrayList<Float> findShiftTriangles(List<float[][]> primitives) {
		ArrayList<Float> dims = findDimsTriangles(primitives);
		ArrayList<Float> shifts = new ArrayList<Float>();
		shifts.add((dims.get(1)+dims.get(0))/2.f);
		shifts.add((dims.get(3)+dims.get(2))/2.f);
		shifts.add((dims.get(5)+dims.get(4))/2.f);
		return shifts;
	}
	
	/**
	 * function to find bounding box of surface model input data
	 * @param primitives : list of 2d float arrays representing individual triangles 
	 * @return bounding box containing all triangles
	 */
	public static ArrayList<Float> findDimsTriangles(List<float[][]> primitives){
		ArrayList<Float> dims = new ArrayList<Float>();
		float minx = Float.MAX_VALUE;
		float maxx = -Float.MAX_VALUE;
		float miny = Float.MAX_VALUE;
		float maxy = -Float.MAX_VALUE;
		float minz = Float.MAX_VALUE;
		float maxz = -Float.MAX_VALUE;
		for(float[][] prim:primitives){
			for(int i =0; i<prim.length; i++){
				if (prim[i][0]<minx){
					minx = prim[i][0];
				}
				if (prim[i][0]>maxx){
					maxx = prim[i][0];
				}
				if (prim[i][1]<miny){
					miny = prim[i][1];
				}
				if (prim[i][1]>maxy){
					maxy = prim[i][1];
				}
				if (prim[i][2]<minz){
					minz = prim[i][2];
				}
				if (prim[i][2]>maxz){
					maxz = prim[i][2];
				}
			}
		}
		dims.add(minx);
		dims.add(maxx);
		dims.add(miny);
		dims.add(maxy);
		dims.add(minz);
		dims.add(maxz);
		return dims;
	}
	
	/**
	 * function to find bounding box for 2d float array
	 * @param arrayToFindBounds
	 * @return arraylist containing the min/max values for all dimensions
	 */
	public static ArrayList<Float> findDims(float[][] arrayToFindBounds){
		ArrayList<Float> dims = new ArrayList<Float>();
		float minx = Float.MAX_VALUE;
		float maxx = -Float.MAX_VALUE;
		float miny = Float.MAX_VALUE;
		float maxy = -Float.MAX_VALUE;
		float minz = Float.MAX_VALUE;
		float maxz = -Float.MAX_VALUE;
		for(float[] prim:arrayToFindBounds){
			for(int i =0; i<prim.length; i++){
				if (prim[0]<minx){
					minx = prim[0];
				}
				if (prim[0]>maxx){
					maxx = prim[0];
				}
				if (prim[1]<miny){
					miny = prim[1];
				}
				if (prim[1]>maxy){
					maxy = prim[1];
				}
				if (prim[2]<minz){
					minz = prim[2];
				}
				if (prim[2]>maxz){
					maxz = prim[2];
				}
			}
		}
		dims.add(minx);
		dims.add(maxx);
		dims.add(miny);
		dims.add(maxy);
		dims.add(minz);
		dims.add(maxz);
		return dims;
	}
	
	
	/**
	 * function that finds all localizations within the specified borders
	 * @param stormData : list of localizations
	 * @param borders : borders given by arraylist of floats
	 * @return indices of points which lie inside the borders
	 */

	public static ArrayList<Integer> findStormDataInRange(float[][] stormData,
			ArrayList<Float> borders) {
		ArrayList<Integer> retList = new ArrayList<Integer>();
		for (int i = 0; i < stormData.length; i++) {
			Coord3d coord = new Coord3d(stormData[i][0], stormData[i][1], stormData[i][2]);
			if (coord.x<borders.get(0)||coord.x>borders.get(1)||coord.y<borders.get(2)||coord.y>borders.get(3)||coord.z<borders.get(4)||coord.z>borders.get(5)){
				
			}
			else{
				retList.add(i);
			}
		}
		return retList;
	}

	/**
	 * function counting the number of visible localizations. All localizations are visible that lie within the borders.
	 * @param borders : borders, only the inner part is counted
	 * @param dataSet : list of localizations
	 * @return number of visible localizations
	 */
	public static int countVisibleLocs(ArrayList<Float> borders,
			DataSet dataSet) {
		int counter = 0;
		float[][] stromPoints = dataSet.stormData;
		for (int i = 0; i<dataSet.stormData.length; i++){
			if (isInRange(stromPoints[i],borders)){
				counter += 1;
			}
		}
		return counter;
	}
	/**
	 * helper function to determine if a given point lies within the borders
	 * @param tmp
	 * @param borders
	 * @return
	 */
	public static boolean isInRange(float[] tmp, ArrayList<Float> borders) {
		// TODO Auto-generated method stub
		if (borders.isEmpty()){
			return true;
		}
		return (tmp[0]>borders.get(0)&&tmp[0]<borders.get(1)&&tmp[1]>borders.get(2)&&tmp[1]<borders.get(3)&&tmp[2]>borders.get(4)&&tmp[2]<borders.get(5));
	}
}
