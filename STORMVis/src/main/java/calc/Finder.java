package calc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JProgressBar;

import model.DataSet;
import model.ParameterSet;

import org.javatuples.Pair;
import org.jzy3d.maths.Coord3d;

/**
 * 
 * Finds Antibodies/lines/ep/ap
 *
 */
public class Finder {
	// fluorophore = binding site
	public static Pair<float[][],float[][]> findAntibodiesTri(List<float[][]> trList, 
			DataSet parameter, STORMCalculator calc) {
		ParameterSet ps = parameter.getParameterSet();
		float bspsnm = ps.getBspsnm();
		float pabs = ps.getPabs();
		float loa = ps.getLoa();
		float aoa = ps.getAoa();
		float soa = ps.getSoa();
		float doc = ps.getDoc();
		float nocpsmm = ps.getNocpsmm();
		float docpsnm = ps.getDocpsnm();
		float[][][] triangles = Calc.getMatrix(trList);
		float[] areas = Calc.getAreas(triangles);
		int numberOfFluorophores = (int) Math.floor(Calc.sum(areas)*bspsnm*pabs);
		System.out.println("fluorophore  number: "+ numberOfFluorophores);
		if(numberOfFluorophores == 0) {
			return null;
		}
		Pair<float[][],int[]> basePointPair = findBasePoints((int) Math.ceil(numberOfFluorophores * (1-doc)), triangles, areas, parameter.getProgressBar(), calc);
//		Calc.print2dMatrix(basePointPair.getValue0());
		float[][] basepoints = basePointPair.getValue0();
		int[] idx = basePointPair.getValue1();
		float[][] ep = getEndpoints(basepoints, triangles, idx, loa, aoa, soa,calc);
		return new Pair<float[][],float[][]>(basepoints,ep);
	}
	
	public static Pair<float[][],int[]> findBasePoints(int nbrFluorophores,float[][][] tr,
			float[] areas, JProgressBar progressBar, STORMCalculator calc) {
		float[][] points = new float[nbrFluorophores][3];
		Pair<float[][],float[][]> vecPair = Calc.getVertices(tr);
		float[][] vec1 = vecPair.getValue0();
		float[][] vec2 = vecPair.getValue1();
		int[] idx = getRandomTriangles(areas, nbrFluorophores,progressBar,calc);
		progressBar.setString("Finding Basepoints");
		for (int f = 0; f < nbrFluorophores; f++) {
			while(true) {
				double randx = calc.random.nextDouble();
				double randy = calc.random.nextDouble();
				//if (f%(nbrFluorophores/100)==0) {
					calc.publicSetProgress((int) (1.*f/nbrFluorophores*100.));
				//}
				for (int i = 0; i < 3; i++) {
					points[f][i] = tr[idx[f]][0][i] + (float) randx*vec1[idx[f]][i] + (float) randy*vec2[idx[f]][i];
				}
				
				if ((randx + randy)<1) {
					break;
				}
				
			}
		}
		return new Pair<float[][], int[]>(points, idx);
	}
	
	public static Pair<float[][],float[][]> findAntibodiesLines(List<ArrayList<Coord3d>> lines, 
			DataSet ds, STORMCalculator calc) {
		JProgressBar progressBar = ds.getProgressBar();
		ParameterSet ps = ds.getParameterSet();
		float bspnm = ps.getBspnm();
		float pabs = ps.getPabs();
		float rof = ps.getRof();
		float aoa = ps.getAoa();
		float soa = ps.getSoa();
		float loa = ps.getLoa();
		//finds start- and endpoint of antibodies for filamentous structures
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
		progressBar.setString("Finding Labels;"); //Start and endpoint of the labels are determined
		for(int i = 0; i < objectNumber; i++) {
			try{
				//if (i%(objectNumber/100)==0) {
				calc.publicSetProgress((int) (1.*i/objectNumber*100.));
				//}
				if(points.get(i).size() > 0) { 
					Pair<Float,float[]> lengthAndCummulativeLength = getLengthOfStructure(points.get(i));
					float lengthOfStructure = lengthAndCummulativeLength.getValue0().floatValue();
					float[] cummulativeLengths = lengthAndCummulativeLength.getValue1();
					for(int j = 1; j <= Math.floor(bspnm*lengthOfStructure); j++) { //bindingsites per nanometer times the length of the structure determines the number of epitopes
						float randomNumber = calc.random.nextFloat();
						
						if(randomNumber < pabs) {//based on the labeling efficiency certain epitopes are rejected
							int idx = 0;
							for(int c = 0; c < cummulativeLengths.length; c++) {//determines current filament
								if(cummulativeLengths[c] >= (((float) j)/bspnm)) {
									idx = c;
									break;
								}
							}
							float x = points.get(i).get(idx+1)[0] - points.get(i).get(idx)[0];
							float y = points.get(i).get(idx+1)[1] - points.get(i).get(idx)[1];
							float z = points.get(i).get(idx+1)[2] - points.get(i).get(idx)[2];
							float[] lineVec = new float[]{x,y,z};
	
							float alpha = (float) (calc.random.nextDouble()*2*Math.PI);//angle which is always orthortogonal to the line
	
							float[] vecOrth = Calc.getVectorLine((float) (90./180.*Math.PI), rof,alpha); //vector from center to the surface of the filament
							float angleDeviation = (float) (calc.random.nextGaussian() * soa);
							float[] vec = Calc.getVectorLine(aoa+angleDeviation, loa,alpha);
	
							float[][] point = new float[2][3];
							point[0] = points.get(i).get(idx);
							point[1] = points.get(i).get(idx+1);
							float[] rotVec = findRotation(vec, lineVec);
							float[] rotVecOrth = findRotation(vecOrth, lineVec);
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
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return new Pair<float[][], float[][]>(Calc.toFloatArray(listStartPoints), Calc.toFloatArray(listEndPoints));
	}
	

	
	public static Pair<Float,float[]> getLengthOfStructure(List<float[]> lines) {
		if (lines.size()<=1){
			float [] cl = new float[0];
			return new Pair<Float, float[]>(0.f,cl);
		}
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
	
	public static float[][] getEndpoints(float[][] basepoints,float[][][] tr,
			int[] idx,float loa,float aoa, float soa, STORMCalculator calc) {
		Pair<float[][],float[][]> vecPair = Calc.getVertices(tr);
		float[][] vec1 = vecPair.getValue0();
		float[][] vec2 = vecPair.getValue1();
		float[][] ep = new float[basepoints.length][3]; //x,y,z,frame,intensity
		for(int i = 0; i < basepoints.length; i++) {
			float angleDeviation = (float) (calc.random.nextGaussian()*soa);
			float alpha =  (float) (calc.random.nextFloat() *2*Math.PI);
			float[] vec = Calc.getVectorTri(aoa+angleDeviation,loa,alpha);
			float[] normTri = Calc.getCross(vec1[idx[i]],vec2[idx[i]]);
			float[] vec3 = {0,1,0};
			float[] normTri3 = {0,1,1};
			//float[] finvectest = findRotationTri(normTri3,vec3);
			//System.out.println("["+finvectest[0]+","+finvectest[1]+","+finvectest[2]);
			float[] finVec = findRotationTri(normTri,vec);
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
	
	public static int[] getRandomTriangles(float[] areas, int nbrFluorophores, JProgressBar progressBar, STORMCalculator calc) {
		progressBar.setString("Finding Random Triangles");
		float tot = Calc.sum(areas);
		int[] idx = new int[0];
		int[] startingindices = new int[areas.length];
		float[] startingsum = new float[areas.length];
		float partsum = 0;
	    int counter = 0;
	    int parts = 10000;
	    for (int i = 0;i<areas.length;i++) { //for faster calculation 
	    	if (partsum >= tot/parts*counter) {
	    		startingindices[counter] = i;
	    		startingsum[counter] = partsum;
	    		counter = counter + 1;
	    	}
	    	partsum = partsum + areas[i];
	    }
	    float[] randd = new float[nbrFluorophores];
	    // random number initialization used to determine on which triangle the antibody binds
	    for (int i = 0;i < nbrFluorophores;i++) {
	    	randd[i] = (float) calc.random.nextFloat();
	    }
	    int startidx = 0;
	    for (int i = 0; i < nbrFluorophores; i++) {
	    	//if (i%(nbrFluorophores/100)==0) {
				calc.publicSetProgress((int) (1.*i/nbrFluorophores*100.));
			//}
	    	float randD = randd[i]*tot; //randD is a number between 0 and the total area. Later the triangle will be found up to which the cumulative area is smaller but the cumulative area of the next triangle would be larger with this method antibodies are placed based on the are since larger triangles fulfill this criteria more often than smaller triangles 
	        for (int k = 0; k < startingsum.length;k++) {
	            if (randD>startingsum[k]) {//startingindices carries the information up to which triangle the cumulative sum has the corresponding cumulative sum stored in startingsum
	                startidx = k;
	        	}
	        }
	        partsum = startingsum[startidx];
	        for (int j = startingindices[startidx]; j < areas.length ;j++) {
	            partsum = partsum + areas[j];
	            if (randD<partsum) { 
	            	int[] idxcopy = new int[idx.length+1];
	            	System.arraycopy(idx, 0, idxcopy, 0, idx.length);
	            	idxcopy[idx.length] = j;
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
	
	public static float[] findRotationTri(float[] normVec, float[] abVec) {
		float[] unityVec = {0,0,1};
		float[] rotVec = null;
		float[] targetVec = null;
		float[] negNormVec = Calc.getNegativeVec(normVec);
				
		if (normVec[0] == 0 && normVec[1] == 0){
	        rotVec = abVec;
		}
		else if (Arrays.equals(unityVec, negNormVec)){
			rotVec = Calc.getNegativeVec(abVec);
		}
		else {
	        targetVec = normVec;
	        targetVec = Calc.scaleToOne(targetVec);
	        float[] v = Calc.getCross(unityVec,targetVec);
	        float s = Calc.getNorm(v);
	        if(s == 0.0) {
	        	return abVec;
	        }
	        float c = Calc.getDot(unityVec, targetVec);
	        float[][] vx = {{0,-v[2], v[1]},{v[2],0,-v[0]},{-v[1],v[0],0}};
	        float[][] R = {{1,0,0},{0,1,0},{0,0,1}}; //+vx+ // vx*vx*(1-c)/s^2;
	        R = Calc.matrixAddition(R, vx);
	        float[][] vxSquared = Calc.matrixMultiply(vx, vx);
	        if (Float.isNaN((float) ((float) (1-c)/(Math.pow(s, 2))))) {
	        	System.out.println("div is NAN");
	        }
	        vxSquared = Calc.matrixDivide(vxSquared,(float) ((float) (Math.pow(s, 2))/(1-c)));
	        
	        R = Calc.matrixAddition(R, vxSquared);
	        rotVec = Calc.applyMatrix(R, abVec);	        
		}
		return rotVec;
	}
	
	public static float[] findRotation(float[] vec, float[] targetVec) {
		float[] unityVec = {1,0,0};
		float[] rotVec = null;
		
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
        vxSquared = Calc.matrixDivide(vxSquared,(float) (Math.pow(s, 2)/(1-c)));
        R = Calc.matrixAddition(R, vxSquared);
        rotVec = Calc.applyMatrix(R, vec);
		return rotVec;
	}
}
