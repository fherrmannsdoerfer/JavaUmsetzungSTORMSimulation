package gui;

import java.util.Random;

import javax.swing.SwingWorker;

import calc.CreateStack;
import model.DataSet;
import model.ParameterSet;

public class CreateTiffStack extends SwingWorker<Void,Void>{
	DataSet set;
	String path;
	Random random;
	Gui guiReference;
	public CreateTiffStack(DataSet set, String path, Random random, Gui guiReference){
		this.set = set;
		this.path = path;
		this.random = random;
		this.guiReference=guiReference;
	}
	@Override
	protected Void doInBackground() throws Exception {
		guiReference.calculate(set,true);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		while (set.isCalculating){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
				
		ParameterSet psSet = set.getParameterSet();
		
		psSet.setMeanBlinkingTime(Float.valueOf(guiReference.meanBlinkingDurationField.getText()));
		int modelNumber = 2;
		if (psSet.isTwoDPSF()){
			modelNumber  = 1;
		}
		guiReference.borders = guiReference.getBorders();
		set.progressBar.setString("Creating Tiff-Stack");
		CreateStack.createTiffStack(set.stormData, 1/psSet.getPixelToNmRatio(),
				psSet.getEmptyPixelsOnRim(),psSet.getEmGain(), guiReference.borders, random,
				psSet.getElectronPerAdCount(), psSet.getFrameRate(), psSet.getMeanBlinkingTime(), psSet.getDeadTime(), psSet.getWindowsizePSF(),
				modelNumber,psSet.getQuantumEfficiency(), psSet.getNa(), psSet.getPsfwidth(), psSet.getFokus(), psSet.getDefokus(), psSet.getSigmaBg(),
				psSet.getConstOffset(), psSet.getCalibrationFile(), path,psSet.isEnsureSinglePSF(), psSet.isDistributePSFoverFrames(),this);
		return null;
	}

	public void publicSetProgress(int i) {
		setProgress(i);
	}
}
