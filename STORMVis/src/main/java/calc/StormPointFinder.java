package calc;

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
        // x,y,z multidimensional?
        float x = Calc.rand(numberOfIncorrectLocalizations) * (xmax -xmin) + xmin;
        float y = Calc.rand(numberOfIncorrectLocalizations) * (ymax -ymin) + ymin;
        float z = Calc.rand(numberOfIncorrectLocalizations) * (zmax -zmin) + zmin;
        if(numberOfIncorrectLocalizations == 0) {
        	x = 0;
        	y = 0;
        	z = 0;
        	System.out.println("No coordinates to append");
        }
        else {
        	listEndPoints = appendLine(listEndPoints, new float[]{x,y,z});
        }
        System.out.println("x: "+x+" | y: "+y +" | z: "+z);
        Calc.print2dMatrix(listEndPoints);
    	}
		
		
		if (fpab != 1) {
			int[] idx = new int[listEndPoints.length]; 
//			abs(floor(randn(size(listEndPoints,1),1)*fpab+fpab));
//			idx(idx==0) = 1;
//			listEndPointsAugmented=[];
//			for i=1:max(idx)
//					alteredPoints = listEndPoints(idx>=i,:);
//			alteredPoints = alteredPoints+randn(size(alteredPoints))*3;
//			listEndPointsAugmented = [listEndPointsAugmented;alteredPoints];
//			end
//			listEndPoints = listEndPointsAugmented;
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
	
	public static float[][] appendLine(float[][] m, float[] line) {
		float[][] copy = new float[m.length+1][m[0].length];
    	System.arraycopy(m, 0, copy, 0, m.length);
    	copy[m.length] = line;
    	m = copy;
		return m;
	}
	
	// Normally distributed rnd numbers
	public static float randn() {
		return (float) generator.nextGaussian();
	}
	
}
