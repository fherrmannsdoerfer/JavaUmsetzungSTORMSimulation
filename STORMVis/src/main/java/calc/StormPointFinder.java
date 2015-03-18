package calc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

public class StormPointFinder {
	private static Random generator = new Random(System.currentTimeMillis());
	
	public static void findStormPoints(float[][] listEndPoints, float abpf, float sxy, float sz, float bd, float fpab, boolean background ) {
		float[][] stormPoints = null;
		//idxF = []; %idxF contains the information to which structure the fluorophore belongs
	    //idxST = []; %idxST contains the information to which structure each localization belongs
		// not set in following code!
		
		if (background) { //unspecific labeling
        float ilpmm3 = 50; //incorrect localizations per micrometer ^3
        float xmin = min(listEndPoints, 0);
        float xmax = max(listEndPoints, 0);
        float ymin = min(listEndPoints, 1);
        float ymax = max(listEndPoints, 1);
        float zmin = min(listEndPoints, 2);
        float zmax = max(listEndPoints, 2);
        int numberOfIncorrectLocalizations = (int) Math.floor(ilpmm3*(xmax-xmin)/1e3*(ymax-ymin)/1e3*(zmax-zmin)/1e3);
        System.out.println("noil:" + numberOfIncorrectLocalizations);
        // x,y,z multidimensional!!! // TODO: fix!
        float x = Calc.rand(numberOfIncorrectLocalizations) * (xmax -xmin) + xmin;
        float y = Calc.rand(numberOfIncorrectLocalizations) * (ymax -ymin) + ymin;
        float z = Calc.rand(numberOfIncorrectLocalizations) * (zmax -zmin) + zmin;
        if(numberOfIncorrectLocalizations == 0) {
        		x = 0;
        		y = 0;
        		z = 0;
        		System.out.println("No coordinates to append.");
        }
        else {
        	listEndPoints = appendLine(listEndPoints, new float[]{x,y,z});
        }
        //System.out.println("x: "+x+" | y: "+y +" | z: "+z);
        Calc.print2dMatrix(listEndPoints);
    	}
		
		
		if (fpab != 1) {
			int[] idx = new int[listEndPoints.length]; 
			for (int i = 0; i<listEndPoints.length;i++) {
				idx[i] = (int) Math.abs(Math.floor(randn() * fpab+fpab));
				System.out.println("idx: " + idx[i]);
			}
			for (int i = 0; i < idx.length; i++) {
				if(idx[i] == 0) {
					idx[i] = 1;
				}
			}
			List<float[]> listEndPointsAugmented = new ArrayList<float[]>();
			for (int i=1; i <= max(idx);i++) {
				System.out.println("i: "+i);
				List<float[]> alteredPoints = new ArrayList<float[]>();
				for (int k = 0; k < idx.length; k++) {
					if(idx[k]>=i) {
						alteredPoints.add(listEndPoints[k]);
					}
				}
				System.out.println("alt. point size: "+ alteredPoints.size());
				
				float[][] altPoints = toFloatArray(alteredPoints);
				Calc.print2dMatrix(altPoints);
				
				for (int c = 0; c < altPoints.length; c++) {
					for (int u = 0; u < altPoints[0].length; u++) {
						altPoints[c][u] = altPoints[c][u] + randn()*3;
					}
				}
				
				for (int p = 0; p < altPoints.length; p++) {
					listEndPointsAugmented.add(altPoints[p]);
				}
			}
			listEndPoints = toFloatArray(listEndPointsAugmented);
			System.out.println("--- List End Points ---");
			//Calc.print2dMatrix(listEndPoints);
		}   
		
		float[] nbrBlinkingEvents = new float[listEndPoints.length];
		for (int i = 0; i < listEndPoints.length; i++) {
			nbrBlinkingEvents[i] = (float) (randn() * Math.sqrt(abpf) + abpf);
			if(nbrBlinkingEvents[i] < 0) {
				nbrBlinkingEvents[i] = 0;
			}
		}
		
		float[][] stormPointsTemp = null;
		
		for (int i = 1; i <= Math.floor(max(nbrBlinkingEvents)); i++) {
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
				x[k] = listEndPointsTranspose[0][idxArray.get(k).intValue()] + randn()*sxy;
				y[k] = listEndPointsTranspose[1][idxArray.get(k).intValue()] + randn()*sxy;
				z[k] = listEndPointsTranspose[2][idxArray.get(k).intValue()] + randn()*sz;
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
			}
//			System.out.println("dim(x): " + x.length);
//			System.out.println("dim(y): " + y.length);
//			System.out.println("dim(z): " + z.length);
//			System.out.println("---");
		}
		if (stormPointsTemp.length != 0) {
			
		}
		
	}
	
	public static float min(float[][] f, int coord) {
		f = Calc.transpose(f);
		List<Float> list = Arrays.asList(ArrayUtils.toObject(f[coord]));
		Float min = Collections.min(list);
		return min.floatValue();
	}
	
	public static float max(float[][] f, int coord) {
		f = Calc.transpose(f);
		List<Float> list = Arrays.asList(ArrayUtils.toObject(f[coord]));
		Float min = Collections.max(list);
		return min.floatValue();
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
	
	public static float[][] appendLine(float[][] m, float[] line) {
		float[][] copy = new float[m.length+1][m[0].length];
    	System.arraycopy(m, 0, copy, 0, m.length);
    	copy[m.length] = line;
    	m = copy;
		return m;
	}
	
	public static float[][] toFloatArray(List<float[]> f) {
		float[][] result = new float[f.size()][f.get(0).length];
		for (int i = 0; i < result.length; i++) {
			result[i] = f.get(i);
		}
		return result;
	}
	
	// Normally distributed rnd numbers
	public static float randn() {
		double result = generator.nextGaussian();
		//System.out.println("rnd: " + result);
		return (float) result;
	}
		
}