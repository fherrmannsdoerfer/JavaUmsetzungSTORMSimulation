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
	
	public static int[] getRandomTriangles(float[] areas, int nbrFluorophores) {
		float tot = Calc.sum(areas);
		int[] idx = new int[nbrFluorophores];
		int[] startingindices = new int[areas.length];
		float[] startingsum = new float[areas.length];
		float partsum = 0;
	    int counter = 1;
	    int parts = 10000;
	    
	    for (int i = 0;i<areas.length;i++) {
	    	partsum = partsum + areas[i];
	    	if (partsum > tot/parts*counter) {
	    		startingindices[counter] = i;
	    		startingsum[counter] = partsum;
	    		counter = counter + 1;
	    	}
	    }
		
		return null;
	}
	
	
}
