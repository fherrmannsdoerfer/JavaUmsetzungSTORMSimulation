package calc;

import java.util.List;

public class Finder {
	
	public static void findAntibodiesTri(List<float[][]> trList, float bspsnm, float pabs,float loa,float aoa,float doc,float nocpsmm,float docpsnm) {
		float[][][] triangles = Calc.getMatrix(trList);
		float[] areas = Calc.getAreas(triangles);
		int numberOfFluorophores = (int) Math.floor(Calc.sum(areas)*bspsnm*pabs);
		System.out.println("fluorophore  number: "+ numberOfFluorophores);
		getRandomTriangles(areas, numberOfFluorophores);
	}
	
	public static void findBasePoints() {
		
	}
	
	public static int[] getRandomTriangles(float[] areas, int nbrFluorophores) {
		float tot = Calc.sum(areas);
		int[] idx = new int[0];
		int[] startingindices = new int[areas.length];
		float[] startingsum = new float[areas.length];
		float partsum = 0;
	    int counter = 0;
	    int parts = 10000;
	    for (int i = 0;i<areas.length;i++) {
	    	partsum = partsum + areas[i];
	    	if (partsum > tot/parts*counter) {
	    		startingindices[counter] = i;
	    		startingsum[counter] = partsum;
	    		counter = counter + 1;
	    	}
	    }
	    float[] randd = new float[nbrFluorophores];
	    // random number initialization
	    for (int i = 0;i < nbrFluorophores;i++) {
	    	randd[i] = (float) Math.random();
	    }
	    int startidx = 0;
	    for (int i = 0; i < nbrFluorophores; i++) {
	    	float randD = randd[i]*tot;
	        for (int k = 1; k < startingsum.length;k++) {
	            if (randD>startingsum[k]) {
	                startidx = k-1;
	        	}
	        }
	        partsum = startingsum[startidx];
	        for (int j = startingindices[startidx]; j < areas.length ;j++) {
	            partsum = partsum + areas[j];
	            if (randD<partsum) { 
	            	int[] idxcopy = new int[idx.length+1];
	            	System.arraycopy(idx, 0, idxcopy, 0, idx.length);
	            	idxcopy[idx.length] = j-1;
	            	idx = idxcopy;
	                break;
	    		}
	    	}
	    }
	    for(int i = 0; i < idx.length; i++) {
	    	System.out.println(idx[i]);
	    }
	    System.out.println("length idx: "+ idx.length);
		return null;
	}
}
