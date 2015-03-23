package common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Speed {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int limit = 120000;
		
		float[][] m = new float[limit][5];
		
		for(int i = 0; i< limit; i++) {
			for(int j = 0; j < 5; j++) {
				m[i][j] = (float) Math.random();
			}
		}
		
		long start = System.nanoTime();
		run(m);
		System.out.println("time: " + (System.nanoTime()-start)/1e9 +" s");
	}
	
	
	private static float[][] removeDeletedLines(float[][] m) {
		List<float[]> list = new ArrayList<float[]>();
		long start = System.nanoTime();
		for (int i = 0; i < m.length; i++) {
			if(m[i][0] != -1 && m[i][1] != -1) {
				list.add(m[i]);
			}
		}
		float[][] result = toFloatArray(list);
		return result;
	}
	
	private static float[][] toFloatArray(List<float[]> f) {
		float[][] result = new float[f.size()][f.get(0).length];
		for (int i = 0; i < result.length; i++) {
			result[i] = f.get(i);
		}
		return result;
	}
	
	private static void run(float[][] m) {
		for(int i = 0; i < 10000; i++) {
			m = removeDeletedLines(m);
		}
	}
}
