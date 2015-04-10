package calc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javatuples.Pair;
import org.jzy3d.maths.Coord3d;

/**
 * 
 * Finds Antibodies/lines/ep/ap
 *
 */
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
	
	public static Pair<float[][],float[][]> findLines(List<ArrayList<Coord3d>> lines, float bspnm, float pabs,float aoa,float loa, float rof) {
		int objectNumber = lines.size();
		List<ArrayList<float[]>> points = new ArrayList<ArrayList<float[]>>();
		
		for(ArrayList<Coord3d> line : lines) {
			ArrayList<float[]> newList = new ArrayList<float[]>();
			for(Coord3d coord : line) {
				float[] newCoord = new float[]{coord.x,coord.y,coord.z};
				newList.add(newCoord);
			}
			points.add(newList);
		}
		
		List<float[]> listStartPoints = new ArrayList<float[]>();
		List<float[]> listEndPoints = new ArrayList<float[]>();
		for(int i = 0; i < objectNumber; i++) {
			if(points.get(i).size() > 0) { 
				Pair<Float,float[]> lengthAndCummulativeLength = getLengthOfStructure(points.get(i));
				float lengthOfStructure = lengthAndCummulativeLength.getValue0().floatValue();
				float[] cummulativeLengths = lengthAndCummulativeLength.getValue1();
				for(int j = 1; j <= Math.floor(bspnm*lengthOfStructure); j++) {
					float randomNumber = (float) Math.random();
					
					if(randomNumber < pabs) {
						int idx = 0;
						for(int c = 0; c < cummulativeLengths.length; c++) {
							if(cummulativeLengths[c] <= (((float) j)/bspnm)) {
								idx = c;
							}
						}
						float x = points.get(i).get(idx+1)[0] - points.get(i).get(idx)[0];
						float y = points.get(i).get(idx+1)[1] - points.get(i).get(idx)[1];
						float z = points.get(i).get(idx+1)[2] - points.get(i).get(idx)[2];
						float[] lineVec = new float[]{x,y,z};

						float alpha = (float) (Math.random()*2*Math.PI);

						float[] vecOrth = Calc.getVector(aoa, rof,alpha);
						float[] vec = Calc.getVector(aoa, loa,alpha);

						float[][] point = new float[2][3];
						point[0] = points.get(i).get(idx);
						point[1] = points.get(i).get(idx+1);
						float[] rotVec = findRotation(vec, point);
						float[] rotVecOrth = findRotation(vecOrth, point);

						float[] lineVecNorm = Calc.scaleToOne(lineVec);
						float multi = ((float)j-1.f)/bspnm - cummulativeLengths[idx];
						float[] lineVecNormMulti = Calc.multiplyVector(lineVecNorm, multi);
						float[] startPoint = Calc.vectorAddition(points.get(i).get(idx+1), Calc.vectorAddition(lineVecNormMulti, rotVecOrth));
						float[] endPoint = Calc.vectorAddition(points.get(i).get(idx+1), Calc.vectorAddition(lineVecNormMulti, Calc.vectorAddition(rotVecOrth, rotVec)));
						listStartPoints.add(startPoint);
						listEndPoints.add(endPoint);
					}
				}
			}
		}
		return new Pair<float[][], float[][]>(Calc.toFloatArray(listStartPoints), Calc.toFloatArray(listEndPoints));
	}
	
	public static Pair<Float,float[]> getLengthOfStructure(List<float[]> lines) {
		float[] cummulativeLengths = new float[lines.size()-1];
		Float length = null;
		for(int i = 0; i < (lines.size() -1); i++) {
			if(i > 0) {
				cummulativeLengths[i] = Calc.getNorm(new float[]{lines.get(i)[0]-lines.get(i+1)[0],lines.get(i)[1]-lines.get(i+1)[1], lines.get(i)[2]-lines.get(i+1)[2]}) + cummulativeLengths[i-1];
			}
			else {
				cummulativeLengths[i] = Calc.getNorm(new float[]{lines.get(i)[0]-lines.get(i+1)[0],lines.get(i)[1]-lines.get(i+1)[1], lines.get(i)[2]-lines.get(i+1)[2]});
				length = new Float(cummulativeLengths[0]);
			}
		}
		length = new Float(cummulativeLengths[cummulativeLengths.length-1]);
		return new Pair<Float, float[]>(length, cummulativeLengths);
	}
	
	public static float[][] getEndpoints(float[][] basepoints,float[][][] tr,int[] idx,float loa,float aoa) {
		Pair<float[][],float[][]> vecPair = Calc.getVertices(tr);
		float[][] vec1 = vecPair.getValue0();
		float[][] vec2 = vecPair.getValue1();
		float[][] ep = new float[basepoints.length][3];
		for(int i = 0; i < basepoints.length; i++) {
			float[] vec = Calc.getVectorTri(aoa,loa);
			float[] normTri = Calc.getCross(vec1[idx[i]],vec2[idx[i]]);
			float[] finVec = findRotationTri(vec, normTri);
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
	
	public static float[] findRotationTri(float[] vec, float[] normVec) {
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
	
	public static float[] findRotation(float[] vec, float[][] point) {
		float[] unityVec = {1,0,0};
		float[] rotVec = null;
		float[] targetVec = null;
		
		targetVec = new float[]{point[1][0] - point[0][0],point[1][1]-point[0][1],point[1][2]-point[0][2]};
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
        rotVec = Calc.applyMatrix(R, vec);
		return rotVec;
	}
}
