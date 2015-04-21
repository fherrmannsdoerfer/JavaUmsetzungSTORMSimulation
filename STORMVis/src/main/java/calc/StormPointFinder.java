package calc;


import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;

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
		return createStormPoints(listEndPoints, ps, sxy, sz, mergedPSFs, psfWidth, progressBar, calc);
	}

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
			if (i%(Math.floor(Calc.max(nbrBlinkingEvents))/100)==0) {
				calc.publicSetProgress((int) (1.*i/Math.floor(Calc.max(nbrBlinkingEvents))*100.));
			}
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
		}
		stormPoints = Calc.toFloatArray(allStormPoints);

		if (stormPoints.length != 0) {
			//append random intensity
			float[] frameNumberCol = new float[stormPoints.length];
			for (int i = 0; i < stormPoints.length; i++) {
				frameNumberCol[i] = Calc.randInt(0, ps.getFrames());
			}
			stormPoints = Calc.appendColumn(stormPoints, frameNumberCol);

			List<float[]> stormPointsArrayList = new ArrayList<float[]>();
			for(int k = 0; k < stormPoints.length; k++) {
				stormPointsArrayList.add(stormPoints[k]);
			}
				
			if (mergedPSFs){
				mergeOverlappingPSFs(stormPoints, stormPointsArrayList, psfWidth, progressBar, calc);
			}
			
	        stormPoints = Calc.toFloatArray(stormPointsArrayList);
		}
		return stormPoints;
	}

	private static void mergeOverlappingPSFs(float[][] stormPoints, 
			List<float[]> stormPointsArrayList, float psfWidth, JProgressBar progressBar,
			STORMCalculator calc) {
		float affectingFactor = 2;
    	int maxInFrameNumbers = (int) Calc.max(stormPoints,4); 
    	System.out.println("maxInFrameNumbers: " + maxInFrameNumbers);
    	long loopStart = System.nanoTime();
    	progressBar.setString("Merge Near Localizations");
    	for (int i = 1; i <= maxInFrameNumbers; i++) {
    		if (i%(maxInFrameNumbers/100)==0) {
				calc.publicSetProgress((int) (1.*i/maxInFrameNumbers*100.));
			}
    		long start = System.nanoTime();
//        		System.out.println("progress: i = " + i);
    		TIntArrayList idxArray = new TIntArrayList();
			int countOne = 0;
			float[] col = Calc.getColumnOfArrayListToFloatArray(stormPointsArrayList,4);
//    			Calc.printVector(col);
//    			System.out.println("Check1");
			for (int j = 0; j < stormPointsArrayList.size(); j++) {
				if(col[j] == i) {
					idxArray.add(new Integer(j));
					countOne++;
				}
			}
//    			System.out.println("Check2");
//    			System.out.println("i: "+ i + " | idx array count: " + idxArray.size());
			float[][] stormXY = new float[idxArray.size()][2];
			for (int h = 0; h < idxArray.size(); h++) {
				stormXY[h][0] = stormPointsArrayList.get(idxArray.get(h))[0];
				stormXY[h][1] = stormPointsArrayList.get(idxArray.get(h))[1];
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
						meanCoords[j][k] = (stormPointsArrayList.get(idxArray.get(locations.get(j)[0]))[k] + stormPointsArrayList.get(idxArray.get(locations.get(j)[1]))[k])/2.f; 
					}
				}
				
//    				long diffVecTime = System.nanoTime();
				float[][] diffVec = new float[locations2.size()][2];
				int locSize = locations2.size();
				for(int k = 0; k < locSize; k++) {
					diffVec[k][0] = stormPointsArrayList.get(idxArray.get(locations2.get(k)[0]))[0] - stormPointsArrayList.get(idxArray.get(locations2.get(k)[1]))[0];
					diffVec[k][1] = stormPointsArrayList.get(idxArray.get(locations2.get(k)[0]))[1] - stormPointsArrayList.get(idxArray.get(locations2.get(k)[1]))[1];
				}
//    				System.out.println("size: "+ diffVec.length);
				int diffVecLength = diffVec.length;
				for(int k = 0; k < diffVecLength; k++) {
					float add = (affectingFactor*psfWidth-Calc.getNorm(diffVec[k]))/(affectingFactor*psfWidth)*0.5f;
					stormPointsArrayList.get(idxArray.get(locations2.get(k)[0]))[0] = stormPointsArrayList.get(idxArray.get(locations2.get(k)[0]))[0] - add*diffVec[k][0];
					stormPointsArrayList.get(idxArray.get(locations2.get(k)[0]))[1] = stormPointsArrayList.get(idxArray.get(locations2.get(k)[0]))[1] - add*diffVec[k][1];
					
					stormPointsArrayList.get(idxArray.get(locations2.get(k)[1]))[0] = stormPointsArrayList.get(idxArray.get(locations2.get(k)[1]))[0] + add*diffVec[k][0];
					stormPointsArrayList.get(idxArray.get(locations2.get(k)[1]))[1] = stormPointsArrayList.get(idxArray.get(locations2.get(k)[1]))[1] + add*diffVec[k][1];
				}
				
				
//    				System.out.println("diff time: " + (System.nanoTime() - diffVecTime)/1e9);
				
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
		for (int i = 0; i<listEndPoints.length;i++) {
			idx[i] = (int) Math.abs(Math.floor(Calc.randn() * fpab+fpab));
		}
		for (int i = 0; i < idx.length; i++) {
			if(idx[i] == 0) {
				idx[i] = 1;
			}
		}
		List<float[]> listEndPointsAugmented = new ArrayList<float[]>();
		for (int i=1; i <= Calc.max(idx);i++) {
			List<float[]> alteredPoints = new ArrayList<float[]>();
			for (int k = 0; k < idx.length; k++) {
				if(idx[k]>=i) {
					alteredPoints.add(listEndPoints[k]);
				}
			}
			
			float[][] altPoints = Calc.toFloatArray(alteredPoints);
			
			for (int c = 0; c < altPoints.length; c++) {
				for (int u = 0; u < altPoints[0].length; u++) {
					//find fluorophores in a sphere around the endpoint of the antibody
					altPoints[c][u] = (float) (altPoints[c][u] + Calc.randn()*1.5);
				}
			}
			
			for (int p = 0; p < altPoints.length; p++) {
				listEndPointsAugmented.add(altPoints[p]);
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
		return listEndPoints;
	}
}
