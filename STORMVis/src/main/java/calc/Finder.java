package calc;

import java.util.List;

import org.javatuples.Pair;


public class Finder {
	
	public static void findAntibodiesTri(List<float[][]> trList, float bspsnm, float pabs,float loa,float aoa,float doc,float nocpsmm,float docpsnm) {
		float[][][] triangles = Calc.getMatrix(trList);
		float[] areas = Calc.getAreas(triangles);
		int numberOfFluorophores = (int) Math.floor(Calc.sum(areas)*bspsnm*pabs);
		System.out.println("fluorophore  number: "+ numberOfFluorophores);
		Pair<float[][],int[]> basePointPair = findBasePoints(numberOfFluorophores, triangles, areas);
		Calc.print2dMatrix(basePointPair.getValue0());
	}
	
	public static Pair<float[][],int[]> findBasePoints(int nbrFluorophores,float[][][] tr,float[] areas) {
		float[][] points = new float[nbrFluorophores][3];
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
		int[] idx = getRandomTriangles(areas, nbrFluorophores);
		
		for (int f = 0; f < nbrFluorophores; f++) {
			while(true) {
				double randx = Math.random();
				double randy = Math.random();
				
				for (int i = 0; i < 3; i++) {
					points[f][i] = tr[idx[f]][0][i] + (float) randx*vec1[idx[f]][i] + (float) randy*vec2[idx[i]][i];
				}
				
				if ((randx + randy)<1) {
				//                figure
				//                plot3(triang(idx(i),:,1),triang(idx(i),:,2),triang(idx(i),:,3))
				//                hold on
				//                plot3(p(1),p(2),p(3),'r*')
				}
				else break;
			}
		}
		return new Pair<float[][], int[]>(points, idx);
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
		return idx;
	}
}
