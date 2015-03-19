package calc;
import java.util.List;

import javax.vecmath.Vector3d;

import org.javatuples.Pair;

public class Calc { 

	/**
	 * @param args
	 */
	public Calc() {
		
	}
	
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
		System.out.println("Converting to new matrix form: " + time/1e9 +"s");
		return trMatrix;
	}
	
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
	
	public static void printVector(float[] vec) {
		System.out.println("-- vector --");
		for(int i = 0; i<vec.length;i++) {
			System.out.println(vec[i]);
		}
		System.out.println("");
	}
	
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
	
	public static float getNorm(float[] vec) {
		float sum = 0;
		for (int i = 0; i < vec.length; i++) {
			sum += Math.pow(vec[i], 2);
		}
		return (float) Math.sqrt(sum);
	}
	
	public static float getDot(float[] vec, float[] vec2) {
		return (vec[0]*vec2[0]+vec[1]*vec2[1]+vec[2]*vec2[2]);
	}
	
	public static float[] getNegativeVec(float[] vec) {
		float[] negVec = new float[vec.length];
		for(int c = 0; c < vec.length;c++) {
			negVec[c] = -vec[c];
		}
		return negVec;
	}
	
	public static float[] scaleToOne(float[] vec) {
		float[] normVec = new float[vec.length];
		float norm = getNorm(vec);
		for(int c = 0; c < vec.length;c++) {
			normVec[c] = vec[c]/norm;
		}
		return normVec;
	}
	
	public static float[] vectorAddition(float[] m1, float[] m2) {
		float[] result = new float[m1.length];
		for (int i = 0; i < m1.length;i++) {
				result[i] = m1[i] + m2[i];
		}
		return result;
	}
	
	
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
	
	public static float[] difference(float[] v1, float[] v2) {
		float[] result = new float[v1.length];
		for(int i = 0; i < v1.length; i++) {
			result[i] = v2[i]- v1[i];
		}
		return result;
	}
	
	public static float sum(float[] array) {
		float sum = 0;
		for (int i = 0;i < array.length;i++) {
			sum += array[i];
		}
		return sum;
	}
	
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
	
	public static float[] getVectorTri(float aoa, float length) {
		double alpha = Math.random()*2*Math.PI;
		double x = Math.cos(aoa)*Math.cos(alpha);
		double z = Math.sin(aoa);
		double y = Math.cos(aoa)*Math.sin(alpha);
		float[] vec = {(float) (x*length),(float) (y*length),(float) (z*length)};
		return vec;
	}
	
	public static float[] applyMatrix(float[][] m, float[] vec) {
		float[] result = new float[vec.length];
		for (int i = 0; i < m.length;i++) {
			for (int j = 0; j < m.length; j++) {
				result[i] += vec[j]*m[i][j];
			}
		}
		return result;
	}
	
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
	
	public static float[][] matrixAddition(float[][] m1, float[][] m2) {
		float[][] result = new float[m1.length][m1.length];
		for (int i = 0; i < m1.length;i++) {
			for (int j = 0; j < m1.length; j++) {
				result[i][j] = m1[i][j] + m2[i][j];
			}
		}
		return result;
	}
	
	
	public static float[][] matrixDivide(float[][] m1, float div) {
		float[][] result = new float[m1.length][m1.length];
		for (int i = 0; i < m1.length;i++) {
			for (int j = 0; j < m1.length; j++) {
				result[i][j] = m1[i][j]/div;
			}
		}
		return result;
	}
	
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
	
	public static float[][] addToLowerTriangle(float[][] m, float add) {
		float[][] result = m;
		for (int i = 0; i < m.length; i++) {
			for (int j = i; j < m.length; j ++) {
				result[j][i] += add;
			}
		}
		return m;
	}
	
	public static float rand(float high) {
		return (float) (Math.random() * high);
	}
}