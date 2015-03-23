package calc;

import java.util.List;
import java.util.Arrays;

import org.javatuples.Pair;


public class Finder {
	// fluorophore = binding site
	public static Pair<float[][],float[][]> findAntibodiesTri(List<float[][]> trList, float bspsnm, float pabs,float loa,float aoa,float doc,float nocpsmm,float docpsnm) {
		float[][][] triangles = Calc.getMatrix(trList);
		float[] areas = Calc.getAreas(triangles);
		int numberOfFluorophores = (int) Math.floor(Calc.sum(areas)*bspsnm*pabs);
		System.out.println("fluorophore  number: "+ numberOfFluorophores);
		if(numberOfFluorophores == 0) {
			return null;
		}
		Pair<float[][],int[]> basePointPair = findBasePoints((int) Math.ceil(numberOfFluorophores * (1-doc)), triangles, areas);
//		Calc.print2dMatrix(basePointPair.getValue0());
		float[][] basepoints = basePointPair.getValue0();
		int[] idx = basePointPair.getValue1();
		float[][] ep = getEndpoints(basepoints, triangles, idx, loa, aoa);
		return new Pair<float[][],float[][]>(basepoints,ep);
	}
	
	public static Pair<float[][],int[]> findBasePoints(int nbrFluorophores,float[][][] tr,float[] areas) {
		float[][] points = new float[nbrFluorophores][3];
		Pair<float[][],float[][]> vecPair = Calc.getVertices(tr);
		float[][] vec1 = vecPair.getValue0();
		float[][] vec2 = vecPair.getValue1();
		int[] idx = getRandomTriangles(areas, nbrFluorophores);
		
		for (int f = 0; f < nbrFluorophores; f++) {
			while(true) {
				double randx = Math.random();
				double randy = Math.random();
				
				for (int i = 0; i < 3; i++) {
					points[f][i] = tr[idx[f]][0][i] + (float) randx*vec1[idx[f]][i] + (float) randy*vec2[idx[f]][i];
				}
				
				if ((randx + randy)<1) {
					// remove ? // edit <=
				}
				else break;
			}
		}
		return new Pair<float[][], int[]>(points, idx);
	}
	
	public static float[][] getEndpoints(float[][] basepoints,float[][][] tr,int[] idx,float loa,float aoa) {
		Pair<float[][],float[][]> vecPair = Calc.getVertices(tr);
		float[][] vec1 = vecPair.getValue0();
		float[][] vec2 = vecPair.getValue1();
		float[][] ep = new float[basepoints.length][3];
		for(int i = 0; i < basepoints.length; i++) {
			float[] vec = Calc.getVectorTri(aoa,loa);
			float[] normTri = Calc.getCross(vec1[idx[i]],vec2[idx[i]]);
			float[] finVec = findRotation(vec, normTri);
			ep[i] = Calc.vectorAddition(basepoints[i], finVec);
			if(Float.isNaN(ep[i][0]) || Float.isNaN(ep[i][1]) || Float.isNaN(ep[i][2])) {
				System.out.println("NAN FOUND EP");
			}
		}
//		System.out.println("EP");
//		Calc.print2dMatrix(ep);
		System.out.println("Rotations completed");
		return ep;
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
//	    for(int i = 0; i < idx.length; i++) {
//	    	System.out.println(idx[i]);
//	    }
//	    System.out.println("length idx: "+ idx.length);
		return idx;
	}
	
	public static float[] findRotation(float[] vec, float[] normVec) {
		float[] unityVec = {0,1,0};
		float[] rotVec = null;
		float[] targetVec = null;
		float[] negNormVec = Calc.getNegativeVec(normVec);
		
		if (Arrays.equals(unityVec, normVec) || Arrays.equals(unityVec, negNormVec)) {
	        rotVec = vec;
		}
		else {
	        targetVec = normVec;
	        targetVec = Calc.scaleToOne(targetVec);
	        float[] v = Calc.getCross(unityVec,targetVec);
	        float s = Calc.getNorm(v);
	        if(s == 0.0) {
	        	return vec;
	        }
	        float c = Calc.getDot(unityVec, targetVec);
	        float[][] vx = {{0,-v[2], v[1]},{v[2],0,-v[0]},{-v[1],v[0],0}};
	        float[][] R = {{1,0,0},{0,1,0},{0,0,1}}; //+vx+ // vx*vx*(1-c)/s^2;
	        R = Calc.matrixAddition(R, vx);
	        float[][] vxSquared = Calc.matrixMultiply(vx, vx);
	        if (Float.isNaN((float) ((float) (1-c)/(Math.pow(s, 2))))) {
	        	System.out.println("div is NAN");
	        }
	        vxSquared = Calc.matrixDivide(vxSquared,(float) ((float) (1-c)/(Math.pow(s, 2))));
	        
	        R = Calc.matrixAddition(R, vxSquared);
	        //Calc.print2dMatrix(R);
	        rotVec = Calc.applyMatrix(R, vec);
	        //Calc.printVector(rotVec);
		}
		return rotVec;
	}
}
