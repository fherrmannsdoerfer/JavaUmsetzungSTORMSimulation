package calc;
import java.util.List;

import javax.vecmath.Vector3d;

import org.jzy3d.plot3d.primitives.Polygon;

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
		for(int i = 0; i<vec.length;i++) {
			System.out.println(vec[i]);
		}
		System.out.println("");
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
		return (float) Math.sqrt(Math.pow(vec[0], 2)+Math.pow(vec[1], 2)+Math.pow(vec[2], 2));
	}
	
	public static float getDot(float[] vec, float[] vec2) {
		return (vec[0]*vec2[0]+vec[1]*vec2[1]+vec[2]*vec2[2]);
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
	
	public static float sum(float[] array) {
		float sum = 0;
		for (int i = 0;i < array.length;i++) {
			sum += array[i];
		}
		return sum;
	}
	
}