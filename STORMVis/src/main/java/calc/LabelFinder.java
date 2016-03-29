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
 * This class manages the simulation of the label positions
 * for surface and line models the first step is to simulate
 *  the user defined number of epitopes, this step is skipped
 * if an epitope model is imported.
 * Then certain epitopes are chosen based on the labeling
 * efficiency. This epitopes are treated as the starting 
 * point of the label. The label length and angle define 
 * the end point of the label which is where the fluorophore is assumed.
 * 
 *
 */
public class LabelFinder {
	/**
	 * Finds label coordinates for surface models
	 * 
	 * @param trList : surface model 
	 * @param parameter : instance of DataSet class containing all necessary parameters like labeling efficiency, label length ...
	 * @param calc : instance of STORMCalculator class containing the random number generator
	 * @return list of starting points and endpoints of the labels
	 */
	public static Pair<float[][],float[][]> findAntibodiesTri(List<float[][]> trList, 
			DataSet parameter, STORMCalculator calc) {
		ParameterSet ps = parameter.getParameterSet();
		float bspsnm = ps.getBspsnm(); //binding sites per square nm
		float pabs = ps.getPabs(); //labeling efficiency
		float aoa = ps.getAoa(); //angle of label
		float soa = ps.getSoa(); //standard deviation of label angle
		float loa = ps.getLoa(); //length of label
		
		float[][][] triangles = Calc.getMatrix(trList);
		float[] areas = Calc.getAreas(triangles);
		int numberOfLabels = (int) Math.floor(Calc.sum(areas)*bspsnm*pabs);
		
		Pair<float[][],int[]> basePointPair = findBasePoints((int) Math.ceil(numberOfLabels * (1)), triangles, areas, parameter.getProgressBar(), calc);
		float[][] basepoints = basePointPair.getValue0();
		int[] idx = basePointPair.getValue1();
		float[][] ep = getEndpoints(basepoints, triangles, idx, loa, aoa, soa,calc);
		return new Pair<float[][],float[][]>(basepoints,ep);
	}
	
	/**
	 * finds starting points for the labels for surface models
	 * @param nbrLabels : number of labels
	 * @param tr : coordinate list defining the triangles the surface consitst of
	 * @param areas : list of surface area for each triangle
	 * @param progressBar : instance of the used progress bar
	 * @param calc : instance of STORMCalculator providing the random number generator
	 * @return list of starting points and corresponding triangles
	 */
	public static Pair<float[][],int[]> findBasePoints(int nbrLabels,float[][][] tr,
			float[] areas, JProgressBar progressBar, STORMCalculator calc) {
		float[][] points = new float[nbrLabels][3];
		Pair<float[][],float[][]> vecPair = Calc.getVertices(tr);
		float[][] vec1 = vecPair.getValue0();
		float[][] vec2 = vecPair.getValue1();
		int[] idx = getRandomTriangles(areas, nbrLabels,progressBar,calc);
		progressBar.setString("Finding Basepoints");
		for (int f = 0; f < nbrLabels; f++) { //find random point on triangle for each label
			while(true) {
				double randx = calc.random.nextDouble();
				double randy = calc.random.nextDouble();
				calc.publicSetProgress((int) (1.*f/nbrLabels*100.));
				
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
	/**
	 * finds starting points and end points of the labels for line models
	 * The possible binding sites for labels on the line model are chosen equidistantly on the filaments
	 * the labels are not placed at the center of the filaments defined by the input model but placed 
	 * with a specified distance (radius of the filament) to the center
	 * @param lines : List of lines defining the filaments
	 * @param parameter : instance of DataSet class containing all necessary parameters like labeling efficiency, label length ...
	 * @param calc : instance of STORMCalculator providing the random number generator
	 * @return list of starting points and list of end points of the labels
	 */
	public static Pair<float[][],float[][]> findLabelsLines(List<ArrayList<Coord3d>> lines, 
			DataSet parameter, STORMCalculator calc) {
		JProgressBar progressBar = parameter.getProgressBar();
		ParameterSet ps = parameter.getParameterSet();
		float bspnm = ps.getBspnm(); //binding sites per nm
		float pabs = ps.getPabs(); //labeling efficiency
		float rof = ps.getRof(); //radius of filament
		float aoa = ps.getAoa(); //angle of label
		float soa = ps.getSoa(); //standard deviation of label angle
		float loa = ps.getLoa(); //length of label
		//finds start- and end points of labels for filamentous structures
		int numberOfFilaments = lines.size();
		List<ArrayList<float[]>> listOfFilaments = new ArrayList<ArrayList<float[]>>();
		
		for(ArrayList<Coord3d> line : lines) {
			ArrayList<float[]> singleFilaments = new ArrayList<float[]>();
			for(Coord3d coord : line) {
				float[] newCoord = new float[]{coord.x,coord.y,coord.z};
				singleFilaments.add(newCoord);
			}
			listOfFilaments.add(singleFilaments);
		}
		List<float[]> listStartPoints = new ArrayList<float[]>();
		List<float[]> listEndPoints = new ArrayList<float[]>();
		progressBar.setString("Finding Labels;"); //Start and end point of the labels are determined
		for(int i = 0; i < numberOfFilaments; i++) {//iterate over all filaments
			try{
				calc.publicSetProgress((int) (1.*i/numberOfFilaments*100.));
				if(listOfFilaments.get(i).size() > 0) { 
					Pair<Float,float[]> lengthAndCummulativeLength = getLengthOfStructure(listOfFilaments.get(i));//get total length and array of cumulative lengths for current filament
					float lengthOfStructure = lengthAndCummulativeLength.getValue0().floatValue();
					float[] cummulativeLengths = lengthAndCummulativeLength.getValue1();
					for(int j = 1; j <= Math.floor(bspnm*lengthOfStructure); j++) {//iterates over all possible binding sites on the current filament
						//bindingsites per nanometer times the length of the structure determines the number of epitopes on the current filament
						float randomNumber = calc.random.nextFloat();
						
						if(randomNumber < pabs) {//based on the labeling efficiency certain epitopes are rejected
							int idx = 0;
							for(int c = 0; c < cummulativeLengths.length; c++) {//determines current segment
								if(cummulativeLengths[c] >= (((float) j)/bspnm)) {
									idx = c;
									break;
								}
							}
							float x = listOfFilaments.get(i).get(idx+1)[0] - listOfFilaments.get(i).get(idx)[0];
							float y = listOfFilaments.get(i).get(idx+1)[1] - listOfFilaments.get(i).get(idx)[1];
							float z = listOfFilaments.get(i).get(idx+1)[2] - listOfFilaments.get(i).get(idx)[2];
							float[] lineVec = new float[]{x,y,z};//vector parallel to the line segment the label will be placed on
	
							float alpha = (float) (calc.random.nextDouble()*2*Math.PI);//angle which is always orthogonal to the line
	
							float[] vecOrth = Calc.getVectorLine((float) (90./180.*Math.PI), rof,alpha); //vector from center to the surface of the filament
							float angleDeviation = (float) (calc.random.nextGaussian() * soa);
							float[] vec = Calc.getVectorLine(aoa+angleDeviation, loa,alpha);//vector describing the label with proper length but not jet rotated based on the line segments angle
	
							float[] rotVec = findRotation(vec, lineVec);//
							float[] rotVecOrth = findRotation(vecOrth, lineVec);//rotVecOrth describes the vector from the center of the line segment to its surface
							float[] lineVecNorm = Calc.scaleToOne(lineVec);//lineVecNorm is the vector parallel to the current line segment
							float multi = ((float)j-1.f)/bspnm - cummulativeLengths[idx];//multi is the distance of the binding site to the first point of the current line segment
							float[] lineVecNormMulti = Calc.multiplyVector(lineVecNorm, multi);//lineVecNormMulti is the vector pointing from the first point of the current line segment to the point where the binding site is located
							float[] startPoint = Calc.vectorAddition(listOfFilaments.get(i).get(idx+1), Calc.vectorAddition(lineVecNormMulti, rotVecOrth));
							float[] endPoint = Calc.vectorAddition(listOfFilaments.get(i).get(idx+1), Calc.vectorAddition(lineVecNormMulti, Calc.vectorAddition(rotVecOrth, rotVec)));
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
	

	/**
	 * function calculates the length of the individual filament and returns a list
	 * with the total length of the filament and a list containing the cumulative length
	 * of the filaments segments. The n-th entry of the cumulative length vector is the sum of the 
	 * lengths of the first n line segments.
	 * @param lines : line data
	 * @return Pair containing the total length and an array with the cumulative length of the 
	 * line segments
	 */
	public static Pair<Float,float[]> getLengthOfStructure(List<float[]> lines) {
		if (lines.size()<=1){
			float [] cl = new float[0];
			return new Pair<Float, float[]>(0.f,cl);
		}
		float[] cumulativeLengths = new float[lines.size()-1];
		Float length = null;
		for(int i = 0; i < (lines.size() -1); i++) {
			if(i > 0) {
				cumulativeLengths[i] = Calc.getNorm(new float[]{lines.get(i)[0]-lines.get(i+1)[0],lines.get(i)[1]-lines.get(i+1)[1], lines.get(i)[2]-lines.get(i+1)[2]}) + cumulativeLengths[i-1];
			}
			else {
				cumulativeLengths[i] = Calc.getNorm(new float[]{lines.get(i)[0]-lines.get(i+1)[0],lines.get(i)[1]-lines.get(i+1)[1], lines.get(i)[2]-lines.get(i+1)[2]});
				length = new Float(cumulativeLengths[0]);
			}
		}
		length = new Float(cumulativeLengths[cumulativeLengths.length-1]);
		return new Pair<Float, float[]>(length, cumulativeLengths);
	}
	
	/**
	 * This function calculates the end point of the labels based on the
	 * chosen angle, label length ...
	 * This is achieved by first creation a vector with the specified angle
	 * relative to the unit vector (0,0,1). 
	 * Later the found vector is rotated in a way that the unit vector (0,0,1) would point
	 * in the direction of the surface normal. This leads to the found vector having the 
	 * proper angle in relation to the surface normal of the triangle.
	 * 
	 * @param basepoints : list of base points
	 * @param tr : 3 dimensional matrix containing the information of the triangles
	 * @param idx : indices of the corresponding triangle for each base point
	 * @param loa : length of label
	 * @param aoa : angle of label in relation to the surface normal of corresponding triangle
	 * @param soa : standard deviation of the angle of label
	 * @param calc : instance of STORMCalculator providing the random number generator
	 * @return end points of all labels
	 */
	public static float[][] getEndpoints(float[][] basepoints,float[][][] tr,
			int[] idx,float loa,float aoa, float soa, STORMCalculator calc) {
		Pair<float[][],float[][]> vecPair = Calc.getVertices(tr);
		float[][] vec1 = vecPair.getValue0();
		float[][] vec2 = vecPair.getValue1();
		float[][] ep = new float[basepoints.length][3]; //x,y,z
		for(int i = 0; i < basepoints.length; i++) {
			float angleDeviation = (float) (calc.random.nextGaussian()*soa);
			float alpha =  (float) (calc.random.nextFloat() *2*Math.PI);
			
			float[] vec = Calc.getVectorTri(aoa+angleDeviation,loa,alpha); //create a vector with proper length and specified angle relative to the surface (an angle of 90 degree leads to a vector like (0,0,1))
			float[] normTri = Calc.getCross(vec1[idx[i]],vec2[idx[i]]); //the surface normal is calculated
			float[] finVec = findRotationTri(normTri,vec); //the vector vec is rotated in a way that the unit vector (0,0,1) would be parallel to the surface normal. This results in the vector finVec to have the proper angle in relation to the given surface of the corresponding triangle
			ep[i] = Calc.vectorAddition(basepoints[i], finVec);
			if(Float.isNaN(ep[i][0]) || Float.isNaN(ep[i][1]) || Float.isNaN(ep[i][2])) {
				System.out.println("NAN FOUND EP");
			}
		}

		return ep;
	}
	
	/**
	 * This function choses triangles to place the base points of the labels based on the 
	 * surface area of the triangles. The probability for a triangle to be the starting 
	 * point of a label is proportional to its surface.
	 * 
	 * @param areas : List of surface areas 
	 * @param nbrLabels : number of labels to be distributed
	 * @param progressBar : instance of progressbar
	 * @param calc : instance of STORMCalculator containing the random number generator
	 * @return list of indices of the chosen labels
	 */
	public static int[] getRandomTriangles(float[] areas, int nbrLabels, JProgressBar progressBar, STORMCalculator calc) {
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
	    float[] randd = new float[nbrLabels];
	    // random number initialization used to determine on which triangle the antibody binds
	    for (int i = 0;i < nbrLabels;i++) {
	    	randd[i] = (float) calc.random.nextFloat();
	    }
	    int startidx = 0;
	    for (int i = 0; i < nbrLabels; i++) {
	    	//if (i%(nbrFluorophores/100)==0) {
				calc.publicSetProgress((int) (1.*i/nbrLabels*100.));
			//}
	    	float randD = randd[i]*tot; //randD is a number between 0 and the total area. Later the triangle will be found up to which the cumulative area is smaller but the cumulative area of the next triangle would be larger with this method antibodies are placed based on the area since larger triangles fulfill this criteria more often than smaller triangles 
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
		return idx;
	}
	
	/**
	 * this function rotates a given vector (labVec) based on the angle between the 
	 * vector given as first argument and the unit vector (0,0,1)
	 * used for surface data
	 * @param normVec : vector defining the rotation to be applied to second argument
	 * @param labVec : vector to be rotated with the same rotation that would rotate the first argument to the unit vector (0,0,1)
	 * @return rotated second argument
	 */
	public static float[] findRotationTri(float[] normVec, float[] labVec) {
		float[] unityVec = {0,0,1};
		float[] rotVec = null;
		float[] targetVec = null;
		float[] negNormVec = Calc.getNegativeVec(normVec);
				
		if (normVec[0] == 0 && normVec[1] == 0){
	        rotVec = labVec;
		}
		else if (Arrays.equals(unityVec, negNormVec)){
			rotVec = Calc.getNegativeVec(labVec);
		}
		else {
	        targetVec = normVec;
	        targetVec = Calc.scaleToOne(targetVec);
	        float[] v = Calc.getCross(unityVec,targetVec);
	        float s = Calc.getNorm(v);
	        if(s == 0.0) {
	        	return labVec;
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
	        rotVec = Calc.applyMatrix(R, labVec);	        
		}
		return rotVec;
	}
	/**
	 * this function rotates a given vector (second argument) based on the angle between the 
	 * vector given as first argument and the unit vector (1,0,0)
	 * used for filamentous data
	 * @param vec : vector defining the rotation to be applied to second argument
	 * @param targetVec : vector to be rotated with the same rotation that would rotate the first argument to the unit vector (0,0,1)
	 * @return rotated second argument
	 */
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
