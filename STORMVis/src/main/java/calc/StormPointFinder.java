package calc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

public class StormPointFinder {
	private static Random generator = new Random(System.currentTimeMillis());
	
	public static float[][] findStormPoints(float[][] listEndPoints, float abpf, float sxy, float sz, float bd, float fpab, boolean background ) {
		float[][] stormPoints = null;
		//idxF = []; %idxF contains the information to which structure the fluorophore belongs
	    //idxST = []; %idxST contains the information to which structure each localization belongs
		// not set in following code!
		
		if (background) { //unspecific labeling
        float ilpmm3 = 50; //incorrect localizations per micrometer ^3
        float xmin = Calc.min(listEndPoints, 0);
        float xmax = Calc.max(listEndPoints, 0);
        float ymin = Calc.min(listEndPoints, 1);
        float ymax = Calc.max(listEndPoints, 1);
        float zmin = Calc.min(listEndPoints, 2);
        float zmax = Calc.max(listEndPoints, 2);
        
		System.out.println(Calc.max(listEndPoints,0) +" | "+ Calc.min(listEndPoints,0));
		System.out.println(Calc.max(listEndPoints,1) +" | " +Calc.min(listEndPoints,1));
        
        
        int numberOfIncorrectLocalizations = (int) Math.floor(ilpmm3*(xmax-xmin)/1e3*(ymax-ymin)/1e3*(zmax-zmin)/1e3);
        System.out.println("noil:" + numberOfIncorrectLocalizations);
        float[] x = Calc.randVector(numberOfIncorrectLocalizations, ((xmax -xmin) + xmin));
        float[] y = Calc.randVector(numberOfIncorrectLocalizations, ((ymax -ymin) + ymin));
        float[] z = Calc.randVector(numberOfIncorrectLocalizations, ((zmax -zmin) + zmin));
        if(numberOfIncorrectLocalizations == 0) {
        		System.out.println("No coordinates to append.");
        }
        else {
        	for (int j = 0; j < numberOfIncorrectLocalizations; j++) {
        		listEndPoints = Calc.appendLine(listEndPoints, new float[]{x[j],y[j],z[j]});
        	}
        }
        //System.out.println("x: "+x+" | y: "+y +" | z: "+z);
//        Calc.print2dMatrix(listEndPoints);
    	}
		
		
		if (fpab != 1) {
			int[] idx = new int[listEndPoints.length]; 
			for (int i = 0; i<listEndPoints.length;i++) {
				idx[i] = (int) Math.abs(Math.floor(Calc.randn() * fpab+fpab));
//				System.out.println("idx: " + idx[i]);
			}
			for (int i = 0; i < idx.length; i++) {
				if(idx[i] == 0) {
					idx[i] = 1;
				}
			}
			List<float[]> listEndPointsAugmented = new ArrayList<float[]>();
			for (int i=1; i <= Calc.max(idx);i++) {
//				System.out.println("i: "+i);
				List<float[]> alteredPoints = new ArrayList<float[]>();
				for (int k = 0; k < idx.length; k++) {
					if(idx[k]>=i) {
						alteredPoints.add(listEndPoints[k]);
					}
				}
//				System.out.println("alt. point size: "+ alteredPoints.size());
				
				float[][] altPoints = Calc.toFloatArray(alteredPoints);
//				Calc.print2dMatrix(altPoints);
				
				for (int c = 0; c < altPoints.length; c++) {
					for (int u = 0; u < altPoints[0].length; u++) {
						altPoints[c][u] = altPoints[c][u] + Calc.randn()*3;
					}
				}
				
				for (int p = 0; p < altPoints.length; p++) {
					listEndPointsAugmented.add(altPoints[p]);
				}
			}
			listEndPoints = Calc.toFloatArray(listEndPointsAugmented);
			System.out.println("--- List End Points ---");
			//Calc.print2dMatrix(listEndPoints);
		}   
		System.out.println("Start blinking");
		float[] nbrBlinkingEvents = new float[listEndPoints.length];
		System.out.println("blinking event number:" + nbrBlinkingEvents);
		for (int i = 0; i < listEndPoints.length; i++) {
			nbrBlinkingEvents[i] = (float) (Calc.randn() * Math.sqrt(abpf) + abpf);
			if(nbrBlinkingEvents[i] < 0) {
				nbrBlinkingEvents[i] = 0;
			}
		}
		
		System.out.println("Start creating stPoints");
		float[][] stormPointsTemp = null;
		List<float[]> allStormPoints = new ArrayList<float[]>();
		System.out.println("floor: "+ Math.floor(Calc.max(nbrBlinkingEvents)));
		int pointCounter = 0;
		for (int i = 1; i <= Math.floor(Calc.max(nbrBlinkingEvents)); i++) {
			List<Integer> idxArray = new ArrayList<Integer>();
			int countOne = 0;
			for (int j = 0; j < nbrBlinkingEvents.length; j++) {
				if(nbrBlinkingEvents[j] >= i) {
					idxArray.add(new Integer(j));
					countOne++;
				}
			}
//			System.out.println(countOne);
//			System.out.println(idxArray.size());
			float[] x = new float[countOne];
			float[] y = new float[countOne];
			float[] z = new float[countOne];
			
			float[][] listEndPointsTranspose = Calc.transpose(listEndPoints);
			
			for (int k = 0; k < idxArray.size(); k++) {
				x[k] = listEndPointsTranspose[0][idxArray.get(k).intValue()] + Calc.randn()*sxy;
				y[k] = listEndPointsTranspose[1][idxArray.get(k).intValue()] + Calc.randn()*sxy;
				z[k] = listEndPointsTranspose[2][idxArray.get(k).intValue()] + Calc.randn()*sz;
			}
			
			// TODO: intensity distribution
			stormPointsTemp = new float[x.length][4];
			float[] intensities = new float[x.length];
			for(int b = 0; b < x.length; b++) {
				intensities[b] = 1.f;
			}
			
			for(int j = 0; j < x.length; j++) {
				stormPointsTemp[j][0] = x[j];
				stormPointsTemp[j][1] = y[j];
				stormPointsTemp[j][2] = z[j];
				stormPointsTemp[j][3] = intensities[j];
				allStormPoints.add(stormPointsTemp[j]);
				pointCounter++;
			}
//			System.out.println("dim(x): " + x.length);
//			System.out.println("dim(y): " + y.length);
//			System.out.println("dim(z): " + z.length);
//			System.out.println("---");
//			System.out.println("stormpoints length: "+ stormPointsTemp.length);
//			pointCounter += stormPointsTemp.length;
//			allStormPoints.add(stormPointsTemp);
		}
		System.out.println("Merging arrays: "+ allStormPoints.size());
		stormPoints = Calc.toFloatArray(allStormPoints);
		System.out.println("All storm points: " + stormPoints.length + " vs counter: " + pointCounter);
//		Calc.print2dMatrix(stormPoints);
		if (stormPoints.length != 0) {
			System.out.println(Calc.max(stormPoints,0) -Calc.min(stormPoints,0));
			System.out.println(Calc.max(stormPoints,1) -Calc.min(stormPoints,1));
			System.out.println(Calc.max(stormPoints,0) +" | "+ Calc.min(stormPoints,0));
			System.out.println(Calc.max(stormPoints,1) +" | " +Calc.min(stormPoints,1));
			float fluorophoresPerFrame = (Calc.max(stormPoints,0) -Calc.min(stormPoints,0)) *(Calc.max(stormPoints,1)-Calc.min(stormPoints,1)) *bd;
			System.out.println("ffpf: "+ fluorophoresPerFrame);
//			fluorophoresPerFrame = 20.f;
			if(fluorophoresPerFrame < 1 || fluorophoresPerFrame == Float.NaN) {
				fluorophoresPerFrame = 1;
			}
			
			float[] frameNumberCol = new float[stormPoints.length];
			int max = (int) Math.ceil(stormPoints.length/fluorophoresPerFrame);
			System.out.println("Max: "+max);
			for (int i = 0; i < stormPoints.length; i++) {
				frameNumberCol[i] = Calc.randInt(0, max);
			}
			stormPoints = Calc.appendColumn(stormPoints, frameNumberCol);
			
			/*
			 * tryout: creating arraylist to append lines faster
			 * 
			 */
			
			System.out.println("Conversion started");
			List<float[]> stormPointsArrayList = new ArrayList<float[]>();
			for(int k = 0; k < stormPoints.length; k++) {
				stormPointsArrayList.add(stormPoints[k]);
			}
			System.out.println("Conversion ended");
//			Calc.print2dMatrix(stormPoints);
			// TODO: boolean or integer?
			boolean mergedPSFs = true;
	        float psfwidth = 200;
	        float affectingFactor = 2;
	        if(mergedPSFs) {
	        	int maxInFrameNumbers = (int) Calc.max(stormPoints,4); 
	        	System.out.println("maxInFrameNumbers: " + maxInFrameNumbers);
	        	for (int i = 1; i <= maxInFrameNumbers; i++) {
	        		long start = System.nanoTime();
//	        		System.out.println("progress: i = " + i);
	        		List<Integer> idxArray = new ArrayList<Integer>();
	    			int countOne = 0;
	    			float[] col = Calc.getColumn(stormPoints, 4);
//	    			Calc.printVector(col);
//	    			System.out.println("Check1");
	    			for (int j = 0; j < stormPoints.length; j++) {
	    				if(col[j] == i) {
	    					idxArray.add(new Integer(j));
	    					countOne++;
	    				}
	    			}
//	    			System.out.println("Check2");
//	    			System.out.println("i: "+ i + " | idx array count: " + idxArray.size());
	    			float[][] stormXY = new float[idxArray.size()][2];
	    			for (int h = 0; h < idxArray.size(); h++) {
	    				stormXY[h][0] = stormPoints[idxArray.get(h).intValue()][0];
	    				stormXY[h][1] = stormPoints[idxArray.get(h).intValue()][1];
	    			}
//	    			System.out.println("Check2.1");
	    			float[][] dists = null;
	    			if(stormXY.length !=0) {
	    				dists = Calc.pairwiseDistance(stormXY, stormXY);
	    				dists = Calc.addToLowerTriangle(dists, 9e9f);

	    				// Find elements where distance is smaller than psfwidth
	    				List<int[]> locations = new ArrayList<int[]>();
//	    				System.out.println("dists l:" + dists.length);
	    				for (int a = 0; a < dists.length; a++) {
	    					for (int b = 0; b < dists.length; b++) {
	    						if(dists[a][b] < psfwidth) {
	    							locations.add(new int[]{a,b});
//	    							System.out.println("a|b : " + a + " | " + b);
	    						}
	    					}
	    				}
	    				
	    				
	    				// Find elements where psfwidth < distance < affecting factor *psfwidth
	    				/*
	    				List<int[]> locations2 = new ArrayList<int[]>();
	    				for (int a = 0; a < dists.length; a++) {
	    					for (int b = 0; b < dists.length; b++) {
	    						if(dists[a][b] > psfwidth && dists[a][b] < affectingFactor*psfwidth) {
	    							locations2.add(new int[]{a,b});
//	    							System.out.println("2--   a|b : " + a + " | " + b);
	    						}
	    					}
	    				}
	    				*/
	    				
	    				float[][] meanCoords = new float[locations.size()][5];
	    				for (int j = 0; j < locations.size(); j++) {
	    					for (int k = 0; k < 5; k++) {
	    						meanCoords[j][k] = (stormPoints[idxArray.get(locations.get(j)[0])][k] + stormPoints[idxArray.get(locations.get(j)[1])][k])/2.f; 
	    					}
	    				}
	    				if(meanCoords.length != 0) {
//	    					Calc.print2dMatrix(meanCoords);
	    				}
	    				
	    				for (int j = 0; j < locations.size(); j++) {
	    					int line = idxArray.get(locations.get(j)[0]);
	    					for (int c = 0; c < stormPoints[line].length; c++) {
	    						stormPoints[line][c] = -1;
	    					}
	    				}
	    				long removeTimeStart = System.nanoTime();
	    				stormPoints = Calc.removeDeletedLines(stormPoints);
	    				System.out.println("deletion time: " + (System.nanoTime()-removeTimeStart)/1e9 + "s");
	    				
	    				long addTimeStart = System.nanoTime();
	    				stormPointsArrayList.clear();
	    				for(int k = 0; k < stormPoints.length; k++) {
	    					stormPointsArrayList.add(stormPoints[k]);
	    				}
	    				
	    				for(int k = 0; k < meanCoords.length; k++) {
//	    					System.out.println("k: "+k);
	    					stormPointsArrayList.add(meanCoords[k]);
	    				}
	    				stormPoints = Calc.toFloatArray(stormPointsArrayList);
	    				System.out.println("adding, mergin: " + (System.nanoTime()-addTimeStart)/1e9 + "s");
	    			}
	    			else {
	    				continue;
	    			}
	    			System.out.println("Runtime " + i + " = " + (System.nanoTime()-start)/1e9);
	        	}
	        }
		}
		
		return stormPoints;
	}
	
	/* really slow - artifact 
	public static float[][] mergeArrays(List<float[][]> list) {
		float[][] result = new float[list.get(0).length][list.get(0)[0].length];
		result = list.get(0);
		for(int i = 1; i < list.size(); i++) {
			System.out.println("merge i: " +i );
			float[][] current = list.get(i);
			for (int k = 0; k < current.length; k++) {
//				System.out.println("k: "+k);
				result = ArrayUtils.addAll(result, current[k]);
			}
		}
		return result;
	}
	*/ 
	
		
}
