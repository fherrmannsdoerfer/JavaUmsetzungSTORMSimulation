package calc;


import gnu.trove.list.array.TIntArrayList;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;

import org.jzy3d.maths.Array;

import model.DataSet;
import model.ParameterSet;

/**
 * @brief finding stormPoints from endPoints
 *
 */
public class StormPointFinder {
	public static float[][] findStormPoints(float[][] listEndPoints, DataSet ds, STORMCalculator calc) {
		JProgressBar progressBar = ds.getProgressBar();
		ParameterSet ps = ds.getParameterSet();
		float sxy = ps.getSxy();
		float sz = ps.getSz();
		float fpab = ps.getFpab();
		boolean background = true;
		float psfWidth = ps.getPsfwidth();
		float ilpmm3 = ps.getIlpmm3();
		boolean mergedPSFs = ps.getMergedPSF();
		boolean applyBleaching = ps.getApplyBleaching();
		
		if (background) { //unspecific labeling
			listEndPoints = addBackground(listEndPoints, ilpmm3);	
    	}
		if (fpab != 1){
			listEndPoints = addMultipleFluorophoresPerAntibody(listEndPoints, fpab);
		}
		float[][] stormPoints;
		if (applyBleaching){
			stormPoints = createStormPointsRealisticBleaching(listEndPoints, ps, sxy, sz,  psfWidth, progressBar, 
					calc,ps.getFrames(), ps.getMeanPhotonNumber(), ps.getBleachConst(), applyBleaching);
		}
		else{
			stormPoints = createStormPoints(listEndPoints, ps, sxy, sz, psfWidth, progressBar,
					calc, ps.getFrames(), ps.getMeanPhotonNumber());
		}
		if (mergedPSFs){
			stormPoints = mergeOverlappingPSFs(stormPoints, psfWidth, progressBar, calc);
		}
		return stormPoints;
		
		
		//stormPoints = assignFrameAndIntensity(stormPoints, ps.getFrames(), ps.getMeanPhotonNumber());
		//return new float[2][2];
	}
	
	//model for frequency of intensities is exponential decay p(I) = exp(-kI)
	//no intensities under 1000 photons are assumed to be fitted
	private static float[][] assignFrameAndIntensity(float[][] listEndPoints,
			int frames, int meanPhotonNumber) {
		float[][] augmentEp = new float[listEndPoints.length][5];
		double k = -Math.log(0.5)/(meanPhotonNumber-1000);
		double factor = 500;
		ArrayList<Float> intensities = new ArrayList<Float>();
		for (int i = 1000; i<7*meanPhotonNumber; i++){
			for (int j = 0; j<Math.ceil(factor * Math.exp(-k*i)); j++){
				//System.out.println(Math.ceil(factor * Math.exp(-k*i)));
				intensities.add((float) i);
			}
		}
		int numberCoordinats = listEndPoints.length;
		for (int i = 0; i< numberCoordinats; i++){
			augmentEp[i][0] = listEndPoints[i][0];
			augmentEp[i][1] = listEndPoints[i][1];
			augmentEp[i][2] = listEndPoints[i][2];
			augmentEp[i][3] = (int) (Math.random()*frames);
			augmentEp[i][4] = intensities.get((int) (Math.random()*intensities.size()-2));
		}
		return augmentEp;
	}

	//creates multiple blinking events based on the enpoints of the antibodies
	//also multiple fluorophores per antibody are taken into account
	private static float[][] createStormPoints(float[][] listEndPoints, ParameterSet ps, 
			float sxy, float sz, float psfWidth, JProgressBar progressBar,
			STORMCalculator calc, int frames, int meanPhotonNumber) {
		double k = -Math.log(0.5)/(meanPhotonNumber-1000);
		double factor = 500;
		ArrayList<Float> intensities = new ArrayList<Float>();
		for (int i = 1000; i<2000*meanPhotonNumber; i++){
			for (int j = 0; j<Math.ceil(factor * Math.exp(-k*i)); j++){
				//System.out.println(Math.ceil(factor * Math.exp(-k*i)));
				intensities.add((float) i);
			}
		}
		
			
		float[][] stormPoints = null;
		//individual number of blinking events per fluorophore
		float[] nbrBlinkingEvents = new float[listEndPoints.length];
		float abpf = ps.getFrames() * ps.getKOn() / ps.getKOff(); //average blinking per fluorophore
		for (int i = 0; i < listEndPoints.length; i++) {
			nbrBlinkingEvents[i] = (float) (Calc.randn() * Math.sqrt(abpf) + abpf);
			if(nbrBlinkingEvents[i] < 0) {
				nbrBlinkingEvents[i] = 0;
			}
		}
		float[][] stormPointsTemp = null;
		List<float[]> allStormPoints = new ArrayList<float[]>();
		System.out.println("floor: "+ Math.floor(Calc.max(nbrBlinkingEvents)));
		progressBar.setString("Create Localizations");
		for (int i = 1; i <= Math.floor(Calc.max(nbrBlinkingEvents)); i++) {
			//if (i%(Math.floor(Calc.max(nbrBlinkingEvents))/100)==0) {
				calc.publicSetProgress((int) (1.*i/Math.floor(Calc.max(nbrBlinkingEvents))*100.));
			//}
			List<Integer> idxArray = new ArrayList<Integer>();
			int countOne = 0;
			for (int j = 0; j < nbrBlinkingEvents.length; j++) {
				if(nbrBlinkingEvents[j] >= i) {
					idxArray.add(new Integer(j));
					countOne++;
				}
			}
			float[] x = new float[countOne];
			float[] y = new float[countOne];
			float[] z = new float[countOne];
			int[] frame = new int[countOne];
			float[] intensity = new float[countOne];
			
			float[][] listEndPointsTranspose = Calc.transpose(listEndPoints);
			
			//detections in higher frames might already be bleached idxList contains only valid indices
			ArrayList<Integer> idxList= new ArrayList<Integer>();
			
			for (int k1 = 0; k1 < idxArray.size(); k1++) {
				intensity[k1] = intensities.get((int) (Math.random()*intensities.size()-1));
				frame[k1] = (int) (Math.random()*frames);
				idxList.add(k1);
				if (ps.getCoupleSigmaIntensity()){
					x[k1] = (float) (listEndPointsTranspose[0][idxArray.get(k1).intValue()] + Calc.randn()*(sxy/Math.sqrt(intensity[k1]/meanPhotonNumber)));
					y[k1] = (float) (listEndPointsTranspose[1][idxArray.get(k1).intValue()] + Calc.randn()*(sxy/Math.sqrt(intensity[k1]/meanPhotonNumber)));
					z[k1] = (float) (listEndPointsTranspose[2][idxArray.get(k1).intValue()] + Calc.randn()*(sz/Math.sqrt(intensity[k1]/meanPhotonNumber)));
				}
				else {
					x[k1] = (listEndPointsTranspose[0][idxArray.get(k1).intValue()] + Calc.randn()*(sxy));
					y[k1] = (listEndPointsTranspose[1][idxArray.get(k1).intValue()] + Calc.randn()*(sxy));
					z[k1] = (listEndPointsTranspose[2][idxArray.get(k1).intValue()] + Calc.randn()*(sz));
				}
				
			}
			
			// TODO: intensity distribution
			stormPointsTemp = new float[idxList.size()][5];
						
			for(int j = 0; j < idxList.size(); j++) {
				stormPointsTemp[j][0] = x[idxList.get(j)];
				stormPointsTemp[j][1] = y[idxList.get(j)];
				stormPointsTemp[j][2] = z[idxList.get(j)];
				stormPointsTemp[j][3] = frame[idxList.get(j)];
				stormPointsTemp[j][4] = intensity[idxList.get(j)];
			
				allStormPoints.add(stormPointsTemp[j]);
			}
		}
		stormPoints = Calc.toFloatArray(allStormPoints);
		System.out.println("Number localizations: "+ allStormPoints.size());
		calc.publicSetProgress((int) (100));
		return stormPoints;
	}

	
	private static float[][] createStormPointsRealisticBleaching(float[][] listEndPoints, ParameterSet ps, 
			float sxy, float sz, float psfWidth, JProgressBar progressBar,
			STORMCalculator calc, int frames, int meanPhotonNumber,float kBleach, boolean applyBleaching) {
		progressBar.setString("Create Localizations");
		double k = -Math.log(0.5)/(meanPhotonNumber-1000);
		double factor = 500;
		ArrayList<Float> intensities = new ArrayList<Float>();
		for (int i = 1000; i<20*meanPhotonNumber; i++){
			for (int j = 0; j<Math.ceil(factor * Math.exp(-k*i)); j++){
				//System.out.println(Math.ceil(factor * Math.exp(-k*i)));
				intensities.add((float) i);
			}
		}
		factor = 1000;
		ArrayList<Integer> maxFrames = new ArrayList<Integer>();
		for (int i = 0; i<listEndPoints.length; i++){
			while (true){
				int maxFrame =(int) (Math.random() * frames);
				if (applyBleaching){ //with bleaching activated higher frames have a lower probability to be populated
					double randomNumber = Math.random(); //random number is equally distributed between 0 and 1 and it is used
					double tmp = Math.exp(-kBleach*maxFrame);
					if (randomNumber < tmp){ //to be tested for the probability that this frame gets this 
						maxFrames.add(maxFrame); //localization. If it is smaller maxFrame is stored and the while loop is left
						break;
					}		//if it is to large a new maxFrame is determined.
				}
				else{ //without bleaching each frame is as likely for a certain localization
					maxFrames.add(maxFrame);
					break;
				}
			}
		}
				
		List<float[]> allStormPoints = new ArrayList<float[]>();
		float[][] stormPoints = null;
		float kOn = ps.getKOn();
		float kOff = ps.getKOff();
		float x;
		float y;
		float z;
		for (int i = 0; i< listEndPoints.length; i++){
			calc.publicSetProgress((int) (1.*i/listEndPoints.length*100.));
			for (int frame = 0; frame < maxFrames.get(i); frame ++){
				double blinkingTest = Math.random();
				if (blinkingTest <= (kOn/kOff)){
					float intensity = intensities.get((int) (Math.random()*intensities.size()-1));
					if (ps.getCoupleSigmaIntensity()){
						x = (float) (listEndPoints[i][0] + Calc.randn()*(sxy/Math.sqrt(intensity/meanPhotonNumber)));
						y = (float) (listEndPoints[i][1] + Calc.randn()*(sxy/Math.sqrt(intensity/meanPhotonNumber)));
						z = (float) (listEndPoints[i][2] + Calc.randn()*(sz/Math.sqrt(intensity/meanPhotonNumber)));
					}
					else {
						x = (float) (listEndPoints[i][0] + Calc.randn()*(sxy));
						y = (float) (listEndPoints[i][1] + Calc.randn()*(sxy));
						z = (float) (listEndPoints[i][2] + Calc.randn()*(sz));
					}
					float tmpLoc[] = {x,y,z,frame,intensity};
					allStormPoints.add(tmpLoc);
				}
			}
		}
		stormPoints = Calc.toFloatArray(allStormPoints);
		System.out.println("Number localizations: "+ allStormPoints.size());
		calc.publicSetProgress((int) (100));
		return stormPoints;
	}
	
	private static float[][] mergeOverlappingPSFs(float[][] stormPoints, float psfWidth, JProgressBar progressBar,
			STORMCalculator calc) {
		ArrayList<float[]> returnList = new ArrayList<float[]>();
		float affectingFactor = (float) 1.25;
    	int maxInFrameNumbers = (int) Calc.max(stormPoints,3); 
    	System.out.println("maxInFrameNumbers: " + maxInFrameNumbers);
    	long loopStart = System.nanoTime();
    	progressBar.setString("Merge Near Localizations");
    	for (int i = 1; i <= maxInFrameNumbers; i++) {
    		//if (i%(maxInFrameNumbers/100)==0) {
				calc.publicSetProgress((int) (1.*i/maxInFrameNumbers*100.));
			//}
    		long start = System.nanoTime();
//        		System.out.println("progress: i = " + i);
    		ArrayList<Integer> idxArray = new ArrayList<Integer>(); //idxArray stores all indices of the current frame
			int countOne = 0;
//    			Calc.printVector(col);
//    			System.out.println("Check1");
			
			for (int j = 0; j < stormPoints.length; j++) {
				if((int) stormPoints[j][3] == i) {
					idxArray.add(new Integer(j));
					countOne++;
				}
			}
			Float[][] currStormPoints = new Float[idxArray.size()][5];
			for (int j = 0; j<idxArray.size(); j++){
				currStormPoints[j][0] = stormPoints[idxArray.get(j)][0];
				currStormPoints[j][1] = stormPoints[idxArray.get(j)][1];
				currStormPoints[j][2] = stormPoints[idxArray.get(j)][2];
				currStormPoints[j][3] = stormPoints[idxArray.get(j)][3];
				currStormPoints[j][4] = stormPoints[idxArray.get(j)][4];
			}
//    			System.out.println("Check2");
//    			System.out.println("i: "+ i + " | idx array count: " + idxArray.size());
			float[][] stormXY = new float[idxArray.size()][2];
			for (int h = 0; h < idxArray.size(); h++) {
				stormXY[h][0] = currStormPoints[h][0];//stormPointsArrayList.get(idxArray.get(h))[0];
				stormXY[h][1] = currStormPoints[h][1];//stormPointsArrayList.get(idxArray.get(h))[1];
			}
//    			System.out.println("Check2.1");
			float[][] dists = null;
			if(stormXY.length !=0) {
				dists = Calc.pairwiseDistance(stormXY, stormXY);
				dists = Calc.addToLowerTriangle(dists, 9e9f);

				// Find elements where distance is smaller than psfwidth
				List<int[]> locations = new ArrayList<int[]>();
//    				System.out.println("dists l:" + dists.length);
				for (int a = 0; a < dists.length; a++) {
					for (int b = 0; b < dists.length; b++) {
						if(dists[a][b] <affectingFactor* psfWidth) { // < psfwidth // TODO: !!!
							locations.add(new int[]{a,b});
//    							System.out.println("a|b : " + a + " | " + b);
						}
					}
				}
				
						
						
				float[][] meanCoords = new float[locations.size()][5];
				double power = 1.4;
				for (int j = 0; j < locations.size(); j++) {
					float int1 = currStormPoints[(locations.get(j)[0])][4];
					float int2 = currStormPoints[(locations.get(j)[1])][4];
					for (int k = 0; k < 3; k++) {
						if (dists[locations.get(j)[0]][locations.get(j)[1]]<0.75*psfWidth){
							power = 1.4;
						}
						else{
							power = 1.4 + ((dists[locations.get(j)[0]][locations.get(j)[1]]/psfWidth)-0.75)*4;
							System.out.println(power+" dists[][]:"+dists[locations.get(j)[0]][locations.get(j)[1]]);
						}
						meanCoords[j][k] = (float) ((currStormPoints[(locations.get(j)[0])][k]*Math.pow(int1,power)+ currStormPoints[(locations.get(j)[1])][k]*Math.pow(int2,power))/(Math.pow(int1,power)+Math.pow(int2,power)));
					}
					meanCoords[j][3] = currStormPoints[(locations.get(j)[0])][3];
					meanCoords[j][4] = int1 + int2;
				}
				
//    						
				ArrayList<float[]> stormPointsArrayList = new ArrayList<float[]>();
				for (int k = 0; k<currStormPoints.length; k++){
					for (int j = 0; j<locations.size(); j++){
						if (locations.get(j)[0] == k || locations.get(j)[1]==k){
							continue;
						}
					}
					float[] tmp = new float[5];
					tmp[0] = currStormPoints[k][0];
					tmp[1] = currStormPoints[k][1];
					tmp[2] = currStormPoints[k][2];
					tmp[3] = currStormPoints[k][3];
					tmp[4] = currStormPoints[k][4];
					stormPointsArrayList.add(tmp);
					
				}
				
				for(int k = 0; k < meanCoords.length; k++) {
					stormPointsArrayList.add(meanCoords[k]);
				}
				returnList.addAll(stormPointsArrayList);
//    				System.out.println("adding, mergin: " + (System.nanoTime()-addTimeStart)/1e9 + "s");
			}
			
			else {
				continue;
			}
//    			System.out.println("Runtime " + i + " = " + (System.nanoTime()-start)/1e9);
    	}
    	System.out.println("Loop time total: " + (System.nanoTime()-loopStart)/1e9 +" s");
    	calc.publicSetProgress((int) (100));
    	return Calc.toFloatArray(returnList);
	}

	private static float[][] addMultipleFluorophoresPerAntibody(float[][] listEndPoints, float fpab) {
		int[] idx = new int[listEndPoints.length]; 
		for (int i = 0; i<listEndPoints.length;i++) { //get random number of fluorophore for each antibody
			idx[i] = (int) Math.abs(Math.floor(Calc.randn() * Math.sqrt(fpab)+fpab));
		}
		for (int i = 0; i < idx.length; i++) {//make sure that each antibodie has at least 1 fluorophore
			if(idx[i] == 0) {
				idx[i] = 1;
			}
		}
		List<float[]> listEndPointsAugmented = new ArrayList<float[]>();
		for (int i=1; i <= Calc.max(idx);i++) {
			List<float[]> alteredPoints = new ArrayList<float[]>();
			for (int k = 0; k < idx.length; k++) {
				if(idx[k]>=i) {
					alteredPoints.add(Array.clone(listEndPoints[k]));
				}
			}
			
			float[][] altPoints = Calc.toFloatArray(alteredPoints);
			
			for (int c = 0; c < altPoints.length; c++) {
				for (int u = 0; u < 3; u++) {
					//find fluorophores in a sphere around the endpoint of the antibody
					altPoints[c][u] = (float) (altPoints[c][u] + Calc.randn()*1.5);
				}
			}
			
			for (int p = 0; p < altPoints.length; p++) {
				float[] tmp = new float[5];
				tmp = altPoints[p];
				listEndPointsAugmented.add(Array.clone(tmp));
			}
		}
		listEndPoints = Calc.toFloatArray(listEndPointsAugmented);
		return listEndPoints;
	}

	private static float[][] addBackground(float[][] listEndPoints, float ilpmm3) {
		float xmin = Calc.min(listEndPoints, 0);
		float xmax = Calc.max(listEndPoints, 0);
		float ymin = Calc.min(listEndPoints, 1);
		float ymax = Calc.max(listEndPoints, 1);
		float zmin = Calc.min(listEndPoints, 2);
		float zmax = Calc.max(listEndPoints, 2);

		System.out.println(Calc.max(listEndPoints,0) +" | "+ Calc.min(listEndPoints,0));
		System.out.println(Calc.max(listEndPoints,1) +" | "+ Calc.min(listEndPoints,1));
		//ilpmm3: //incorrect localizations per micrometer ^3
		int numberOfIncorrectLocalizations = (int) Math.floor(ilpmm3*(xmax-xmin)/1e3*(ymax-ymin)/1e3*(zmax-zmin)/1e3);
		System.out.println("noil:" + numberOfIncorrectLocalizations);
		float[] x = Calc.randVector(numberOfIncorrectLocalizations, xmin, xmax);
		float[] y = Calc.randVector(numberOfIncorrectLocalizations, ymin, ymax);
		float[] z = Calc.randVector(numberOfIncorrectLocalizations, zmin, zmax);
		if(numberOfIncorrectLocalizations == 0) {
			System.out.println("No coordinates to append.");
		}
		else {
			for (int j = 0; j < numberOfIncorrectLocalizations; j++) {
				listEndPoints = Calc.appendLine(listEndPoints, new float[]{x[j],y[j],z[j]});
			}
		}
		return listEndPoints;
	}
}
