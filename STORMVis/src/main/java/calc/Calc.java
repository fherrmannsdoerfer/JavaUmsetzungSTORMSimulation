package calc;
import gnu.trove.list.array.TFloatArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.vecmath.Vector3d;

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
	 * 
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
	 * @param aoa
	 * @param length
	 * @return vector with antibody angle and length for triangles and lines
	 */
	
	public static float[] getVectorTri(float aoa, float length) {
		double alpha = Math.random()*2*Math.PI;
		double x = Math.cos(aoa)*Math.cos(alpha);
		double y = Math.cos(aoa)*Math.sin(alpha);
		double z = Math.sin(aoa);
		float[] vec = {(float) (x*length),(float) (y*length),(float) (z*length)};
		return vec;
	}
	
	public static float[] getVector(float aoa, float length, float alpha) {
		double x = Math.cos(aoa)*Math.cos(alpha);
		double z = Math.sin(aoa);
		double y = Math.cos(aoa)*Math.sin(alpha);
		float[] vec = {(float) (x*length),(float) (y*length),(float) (z*length)};
		return vec;
	}
	public static float[] getVectorLine(float aoa, float length, float alpha) {
		double z = Math.sin(aoa)*Math.sin(alpha);
		double y = Math.sin(aoa)*Math.cos(alpha);
		double x = Math.sqrt(1-y*y-z*z);
		float[] vec = {(float) (x*length),(float) (y*length),(float) (z*length)};
		return vec;
	}

	
	/**
	 * Matrix multiplication
	 * @param m
	 * @param vec
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
	 * @param m1
	 * @param m2
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
	 * @param m1
	 * @param m2
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
	 * @param m1
	 * @param div
	 * @return m1/div
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
	 * 
	 * Distances of all vertices in m1 and m2 (used for mergePSF)
	 * @param m1
	 * @param m2
	 * @return distance
	 */
	
	public static float[][] pairwiseDistance(float[][] m1, float[][] m2) {
		float[][] result = new float[m1.length][m1.length];
		for(int i = 0; i < m1.length; i++) {
			for(int j = 0; j < m1.length; j ++) {
				float[] vec1 = m1[i];
				float[] vec2 = m2[j];
				float difference = getNorm(difference(vec1, vec2));
				result[i][j] = difference;
			}
		}
		return result;
	}
	
	/**
	 * Addition of add to lower triangle of an n x n matrix m
	 * @param m
	 * @param add
	 * @return m(lower triangle) + add
	 */
	
	public static float[][] addToLowerTriangle(float[][] m, float add) {
		float[][] result = m;
		for (int i = 0; i < m.length; i++) {
			for (int j = i; j < m.length; j ++) {
				result[j][i] += add;
			}
		}
		return m;
	}
	
	/**
	 * Random number generator
	 * @param high
	 * @return float in [0,high];
	 */
	
	public static float rand(float high) {
		return (float) (Math.random() * high);
	}
	
	/**
	 * Random vector multiplied with multiplier
	 * @param dimension
	 * @param multiplier
	 * @return random Vector * multiplier
	 */
	
	public static float[] randVector(int dimension, float min, float max) {
		float[] result = new float[dimension];
		for(int i = 0; i < dimension; i++) {
			result[i] = (float) Math.random() * (max - min) + min;
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
	
	public static float max(int[] f) {
		List<Integer> list = Arrays.asList(ArrayUtils.toObject(f));
		Integer min = Collections.max(list);
		return min.intValue();
	}
	
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
	
	public static List<float[]> removeDeletedLinesToArrayList(List<float[]> m) {
		List<float[]> list = new ArrayList<float[]>();
		for (int i = 0; i < m.size(); i++) {
			if(m.get(i)[0] != -1 && m.get(i)[1] != -1) {
				list.add(m.get(i));
			}
		}
		return list;
	}
	
	
	
	public static float[] getColumn(float[][] m, int col) {
		m = Calc.transpose(m);
		return m[col];
	}
	
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
	
	public static TFloatArrayList getColumnOfArrayList(List<float[]> list, int column) {
		TFloatArrayList result = new TFloatArrayList();
		for(int i = 0; i < list.size(); i++) {
			result.add(list.get(i)[column]);
		}
		return result;
	}
	
	public static float[] getColumnOfArrayListToFloatArray(List<float[]> list, int column) {
		float[] result = new float[list.size()];
		for(int i = 0; i < list.size(); i++) {
			result[i]= list.get(i)[column];
		}
		return result;
	}
	
	// only for nx2 vertices
	public static float[] getLengthDiffVec(float[][] m) {
		float[] result = new float[m.length];
		for(int i = 0; i < m.length; i++) {
			result[i] = getNorm(m[i]);
		}
		return result;
	}
	
	private static Random generator = new Random(System.currentTimeMillis());
	// Normally distributed rnd numbers
	public static float randn() {
		double result = generator.nextGaussian();
		return (float) result;
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	public static ArrayList<float[][]> addFilteredPoints3D(ArrayList<float[][]> coloredImage, double sigma, int filterwidth, 
			double pixelsize, float[][] sd, int mode){
	//ArrayList<float[][]> addFilteredPoints(ArrayList<float[][]> coloredImage, double sigma, 
	//		int filterwidth, double pixelsize, ArrayList<StormLocalization> sd){
		if (filterwidth %2 == 0) {System.err.println("filterwidth must be odd");}
		double factor = 10000*1/(2*Math.PI*sigma*sigma);
		double factor2 = -0.5/sigma/sigma;
		//ArrayList<Double> dims = getDimensions();
		sd[sd.length-2][2] = -450;
		sd[sd.length-1][2] = 450;

		float xMin = Calc.min(sd, 0);
		float xMax = Calc.max(sd, 0);
		float yMin = Calc.min(sd, 1);
		float yMax = Calc.max(sd, 1);
		float zMin = Calc.min(sd, 2);
		float zMax = Calc.max(sd, 2);

		zMax = zMax - zMin;
		
		System.out.println("zMax: "+zMax+" zMin: "+zMin);
		float[][] redChannel = coloredImage.get(0);
		float[][] greenChannel = coloredImage.get(1);
		float[][] blueChannel = coloredImage.get(2);
		for (int i = 1; i<sd.length; i++){
			float[] sl = sd[i];//.get(i);
			
			double posX = 0;
			double posY = 0;
			double posZ = 0;
			switch (mode){
				case 1:
					posX = (sl[0]-xMin)/pixelsize; //position of current localization
					posY = (sl[1]-yMin)/pixelsize;
					posZ = (sl[2])-zMin;;
					break;
				case 2:
					posX = (sl[0]-xMin)/pixelsize; //position of current localization
					posY = (sl[2]-zMin)/pixelsize;
					posZ = (sl[1])-zMin;;
					break;
				case 3:
					posX = (sl[1]-yMin)/pixelsize; //position of current localization
					posY = (sl[2]-zMin)/pixelsize;
					posZ = (sl[0])-zMin;
					break;
			}
			boolean inverted = false;
						
			int pixelXStart = (int)Math.floor(posX) - (filterwidth-1)/2;
			int pixelYStart = (int)Math.floor(posY) - (filterwidth-1)/2;
			float intensity = sl[4];
			for (int k = pixelXStart; k<pixelXStart+ filterwidth;k++){
				for(int l= pixelYStart; l<pixelYStart+ filterwidth;l++){
					double kk = 1;
					try{
						if (inverted){
							if (posZ < 0.25* zMax){
								redChannel[k][l] = redChannel[k][l] - (float)((4*posZ / zMax)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								greenChannel[k][l] = greenChannel[k][l] - (float)((4*posZ / zMax)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));	
							}
							else if (posZ < 0.5* zMax){
								redChannel[k][l] = redChannel[k][l] -(float)((4*posZ/zMax - 1)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								blueChannel[k][l] = blueChannel[k][l] -(float)((4*posZ/zMax - 1)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								greenChannel[k][l] = greenChannel[k][l] - (float)((2 - 4*posZ/zMax)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								redChannel[k][l] = redChannel[k][l] - (float)((2 - 4*posZ/zMax)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								//blueChannel[k][l] = blueChannel[k][l] + (float)((1)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
							else if (posZ < 0.75* zMax){
								
								redChannel[k][l] = redChannel[k][l] - (float)((4*posZ/zMax - 2)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								blueChannel[k][l] = blueChannel[k][l] - (float)((4*posZ/zMax - 2)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								
								redChannel[k][l] = redChannel[k][l] - (float)((3 - 4*posZ/zMax)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								greenChannel[k][l] = greenChannel[k][l] - (float)((3 - 4*posZ/zMax)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
							else {
								greenChannel[k][l] = greenChannel[k][l] - (float)((4*posZ/zMax - 3)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								blueChannel[k][l] = blueChannel[k][l] - (float)((4*posZ/zMax - 3)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								blueChannel[k][l] = blueChannel[k][l] - (float)((4-4*posZ/zMax)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								redChannel[k][l] = redChannel[k][l] - (float)((4-4*posZ/zMax)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
						}
						else{
							if (posZ < 0.25* zMax){
								//redChannel[k][l] = redChannel[k][l] + (float)((0)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								//greenChannel[k][l] = greenChannel[k][l] + (float)((posZ)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								//blue rises from 0 to 1
								blueChannel[k][l] = blueChannel[k][l] + (float)((4*posZ / zMax)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								
							}
							else if (posZ < 0.5* zMax){
								//redChannel[k][l] = redChannel[k][l] + (float)((0)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								//green rises from 0 to 1 blue stays one
								greenChannel[k][l] = greenChannel[k][l] + (float)((4*posZ/zMax - 1)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								blueChannel[k][l] = blueChannel[k][l] + (float)((2 - 4*posZ/zMax)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
							else if (posZ < 0.75* zMax){
								//green stays one, blue goes to zero again
								//redChannel[k][l] = redChannel[k][l] + (float)((0)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								greenChannel[k][l] = greenChannel[k][l] + (float)((4*posZ/zMax - 2)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								blueChannel[k][l] = blueChannel[k][l] + (float)((3 - 4*posZ/zMax)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
							else {
								//green goes to zero red rises
								redChannel[k][l] = redChannel[k][l] + (float)((4*posZ/zMax - 3)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								greenChannel[k][l] = greenChannel[k][l] + (float)((4-4*posZ/zMax)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								//blueChannel[k][l] = blueChannel[k][l] + (float)((0)*factor*intensity* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
						}
						
					} catch(IndexOutOfBoundsException e){e.toString();}
				}
			}
		}
		float max= 0;
		float min = (float) 1e19;
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
				redChannel[i][j] = (redChannel[i][j]-min);
				greenChannel[i][j] = (greenChannel[i][j]-min);
				blueChannel[i][j] = (blueChannel[i][j]-min);
			}
		}
		//ArrayList<float[][]> normalizedChannels = normalizeChannels(redChannel, greenChannel, blueChannel);
		//coloredImage.clear();
		//coloredImage.add(normalizedChannels.get(0));
		//coloredImage.add(normalizedChannels.get(1));
		//coloredImage.add(normalizedChannels.get(2));
		return coloredImage;
	}
	
	public static float[][] addFilteredPoints(float[][] image, double sigma, int filterwidth, 
			double pixelsize, float[][] sd, int mode, double xmin, double ymin, double zmin){
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
					posX = (sl[0]-xmin)/pixelsize; //position of current localization
					posY = (sl[1]-ymin)/pixelsize;
					break;
				case 2:
					posX = (sl[0]-xmin)/pixelsize; //position of current localization
					posY = (sl[2]-zmin)/pixelsize;
					break;
				case 3:
					posX = (sl[1]-ymin)/pixelsize; //position of current localization
					posY = (sl[2]-zmin)/pixelsize;
					break;
			}
			
			int pixelXStart = (int)Math.floor(posX) - (filterwidth-1)/2;
			int pixelYStart = (int)Math.floor(posY) - (filterwidth-1)/2;
			for (int k = pixelXStart; k<pixelXStart+ filterwidth;k++){
				for(int l= pixelYStart; l<pixelYStart+ filterwidth;l++){
					try{
						image[k][l] = image[k][l] + (float)(factor* Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
						//System.out.println("factor: "+factor+" k: "+k+" l: "+l+"posX: "+posX+"posY: "+posY+" image[k][l]" +image[k][l]+" res: "+(float)(factor*intensity* Math.exp(-0.5/sigma/sigma*(Math.pow((k-posX),2)+Math.pow((l-posY),2)))));
					} catch(IndexOutOfBoundsException e){e.toString();}
				}
			}
		}
		return image;
	}

	public static ArrayList<Float> findShiftLines(List<ArrayList<Coord3d>> data) {
		ArrayList<Float> shifts = new ArrayList<Float>();
		ArrayList<Float> dims = new ArrayList<Float>();
		dims = findDimsLines(data);
		shifts.add((dims.get(1)+dims.get(0))/2.f);
		shifts.add((dims.get(3)+dims.get(2))/2.f);
		shifts.add((dims.get(5)+dims.get(4))/2.f);
		return shifts;
	}
	
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

	public static ArrayList<Float> findShiftTriangles(List<float[][]> primitives) {
		ArrayList<Float> dims = findDimsTriangles(primitives);
		ArrayList<Float> shifts = new ArrayList<Float>();
		shifts.add((dims.get(1)+dims.get(0))/2.f);
		shifts.add((dims.get(3)+dims.get(2))/2.f);
		shifts.add((dims.get(5)+dims.get(4))/2.f);
		return shifts;
	}
	
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
	
	public static ArrayList<Float> findDims(float[][] antiBodyEndPoints){
		ArrayList<Float> dims = new ArrayList<Float>();
		float minx = Float.MAX_VALUE;
		float maxx = -Float.MAX_VALUE;
		float miny = Float.MAX_VALUE;
		float maxy = -Float.MAX_VALUE;
		float minz = Float.MAX_VALUE;
		float maxz = -Float.MAX_VALUE;
		for(float[] prim:antiBodyEndPoints){
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
	
	


	public static float[][] findStormDataInRange(float[][] stormData,
			ArrayList<Float> borders) {
		ArrayList<float[]> retList = new ArrayList<float[]>();
		for (int i = 0; i < stormData.length; i++) {
			Coord3d coord = new Coord3d(stormData[i][0], stormData[i][1], stormData[i][2]);
			if (coord.x<borders.get(0)||coord.x>borders.get(1)||coord.y<borders.get(2)||coord.y>borders.get(3)||coord.z<borders.get(4)||coord.z>borders.get(5)){
				
			}
			else{
				float[] tmp = {stormData[i][0],stormData[i][1],stormData[i][2],stormData[i][3],stormData[i][4]};
				retList.add(tmp);
			}
		}
		return toFloatArray(retList);
	}


}