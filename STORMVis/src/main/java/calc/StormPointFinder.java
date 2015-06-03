package calc;


import gnu.trove.list.array.TIntArrayList;

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
		boolean mergedPSFs = ps.mergedPSF;
				
		if (background) { //unspecific labeling
			listEndPoints = addBackground(listEndPoints, ilpmm3);	
    	}
		if (fpab != 1){
			listEndPoints = addMultipleFluorophoresPerAntibody(listEndPoints, fpab);
		}
		float[][] stormPoints = createStormPoints(listEndPoints, ps, sxy, sz, mergedPSFs, psfWidth, progressBar, calc);
		stormPoints = assignFrameAndIntensity(stormPoints, ps.getFrames(), ps.getMeanPhotonNumber());
		if (mergedPSFs){
			mergeOverlappingPSFs(stormPoints, psfWidth, progressBar, calc);
		}
		return stormPoints;
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
			float sxy, float sz, boolean mergedPSFs, float psfWidth, JProgressBar progressBar,
			STORMCalculator calc) {
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
		int pointCounter = 0;
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
			
			float[][] listEndPointsTranspose = Calc.transpose(listEndPoints);
			
			for (int k = 0; k < idxArray.size(); k++) {
				x[k] = listEndPointsTranspose[0][idxArray.get(k).intValue()] + Calc.randn()*sxy;
				y[k] = listEndPointsTranspose[1][idxArray.get(k).intValue()] + Calc.randn()*sxy;
				z[k] = listEndPointsTranspose[2][idxArray.get(k).intValue()] + Calc.randn()*sz;
			}
			
			// TODO: intensity distribution
			stormPointsTemp = new float[x.length][3];
						
			for(int j = 0; j < x.length; j++) {
				stormPointsTemp[j][0] = x[j];
				stormPointsTemp[j][1] = y[j];
				stormPointsTemp[j][2] = z[j];
				allStormPoints.add(stormPointsTemp[j]);
				pointCounter++;
			}
		}
		stormPoints = Calc.toFloatArray(allStormPoints);

		return stormPoints;
	}

	private static void mergeOverlappingPSFs(float[][] stormPoints, float psfWidth, JProgressBar progressBar,
			STORMCalculator calc) {
		float affectingFactor = 2;
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
    		TIntArrayList idxArray = new TIntArrayList();
			int countOne = 0;
//    			Calc.printVector(col);
//    			System.out.println("Check1");
			for (int j = 0; j < stormPoints.length; j++) {
				if((int) stormPoints[j][3] == i) {
					idxArray.add(new Integer(j));
					countOne++;
				}
			}
//    			System.out.println("Check2");
//    			System.out.println("i: "+ i + " | idx array count: " + idxArray.size());
			float[][] stormXY = new float[idxArray.size()][2];
			for (int h = 0; h < idxArray.size(); h++) {
				stormXY[h][0] = stormPoints[h][0];//stormPointsArrayList.get(idxArray.get(h))[0];
				stormXY[h][1] = stormPoints[h][1];//stormPointsArrayList.get(idxArray.get(h))[1];
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
						if(dists[a][b] < psfWidth) { // < psfwidth // TODO: !!!
							locations.add(new int[]{a,b});
//    							System.out.println("a|b : " + a + " | " + b);
						}
					}
				}
				
				// Find elements where psfwidth < distance < affecting factor *psfwidth
				
				List<int[]> locations2 = new ArrayList<int[]>();
				for (int a = 0; a < dists.length; a++) {
					for (int b = 0; b < dists.length; b++) {
						if(dists[a][b] > psfWidth && dists[a][b] < affectingFactor*psfWidth) {
							locations2.add(new int[]{a,b});
//    							System.out.println("2--   a|b : " + a + " | " + b);
						}
					}
				}
				
				float[][] meanCoords = new float[locations.size()][5];
				for (int j = 0; j < locations.size(); j++) {
					for (int k = 0; k < 5; k++) {
						meanCoords[j][k] = (stormPoints[idxArray.get(locations.get(j)[0])][k] + stormPoints[idxArray.get(locations.get(j)[1])][k])/2.f; 
					}
				}
				
//    				long diffVecTime = System.nanoTime();
				float[][] diffVec = new float[locations2.size()][2];
				int locSize = locations2.size();
				for(int k = 0; k < locSize; k++) {
					diffVec[k][0] = stormPoints[idxArray.get(locations2.get(k)[0])][0] - stormPoints[idxArray.get(locations2.get(k)[1])][0];
					diffVec[k][1] = stormPoints[idxArray.get(locations2.get(k)[0])][1] - stormPoints[idxArray.get(locations2.get(k)[1])][1];
				}
//    				System.out.println("size: "+ diffVec.length);
				int diffVecLength = diffVec.length;
				for(int k = 0; k < diffVecLength; k++) {
					float add = (affectingFactor*psfWidth-Calc.getNorm(diffVec[k]))/(affectingFactor*psfWidth)*0.5f;
					stormPoints[idxArray.get(locations2.get(k)[0])][0] = stormPoints[idxArray.get(locations2.get(k)[0])][0] - add*diffVec[k][0];
					stormPoints[idxArray.get(locations2.get(k)[0])][1] = stormPoints[idxArray.get(locations2.get(k)[0])][1] - add*diffVec[k][1];
					
					stormPoints[idxArray.get(locations2.get(k)[1])][0] = stormPoints[idxArray.get(locations2.get(k)[1])][0] + add*diffVec[k][0];
					stormPoints[idxArray.get(locations2.get(k)[1])][1] = stormPoints[idxArray.get(locations2.get(k)[1])][1] + add*diffVec[k][1];
				}
				
				
//    				System.out.println("diff time: " + (System.nanoTime() - diffVecTime)/1e9);
				
				ArrayList<float[]> stormPointsArrayList = new ArrayList<float[]>();
				for (int k = 0; k<stormPoints.length; k++){
					float[] tmp = new float[5];
					tmp[0] = stormPoints[k][0];
					tmp[1] = stormPoints[k][1];
					tmp[2] = stormPoints[k][2];
					tmp[3] = stormPoints[k][3];
					tmp[4] = stormPoints[k][4];
					stormPointsArrayList.add(tmp);
				}
				
				long removeTimeStart = System.nanoTime();
				idxArray.sort();
			    idxArray.reverse();
			    for (int j = 0; j < locations.size(); j++) {
			    	int line = idxArray.get(locations.get(j)[0]);
			    	stormPointsArrayList.remove(line);
			    }
				long addTimeStart = System.nanoTime();
				
				for(int k = 0; k < meanCoords.length; k++) {
					stormPointsArrayList.add(meanCoords[k]);
				}
//    				System.out.println("adding, mergin: " + (System.nanoTime()-addTimeStart)/1e9 + "s");
				stormPoints = Calc.toFloatArray(stormPointsArrayList);
			}
			else {
				continue;
			}
//    			System.out.println("Runtime " + i + " = " + (System.nanoTime()-start)/1e9);
    	}
    	System.out.println("Loop time total: " + (System.nanoTime()-loopStart)/1e9 +" s");
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
