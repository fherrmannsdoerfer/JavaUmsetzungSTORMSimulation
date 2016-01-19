package model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParameterSet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Float loa;	
    private Float aoa; 
    private Float soa;
    private Float bspnm;
    private Float pabs; 				
    private Float abpf;		
    private Float rof;		
    private Float fpab; 
    
    private Float sxy; 
    private Float sz; 
    private Float doc; 
    private Float nocpsmm; 
    private Float docpsnm;
    private Float bd;
    private Float bspsnm;
    private int frames;
    private Float dutyCycle;
    private Float kOff;
    private Float deff;
    private Float bleachConst;
    private int meanPhotonNumber;
    
    private Boolean generalVisibility;
    private Boolean emVisibility;
    private Boolean stormVisibility;
    private Boolean antibodyVisibility;
    
    private Float ilpmm3;
	
	private Boolean applyBleaching;
	private Boolean mergedPSF;
	private Boolean coupleSigmaIntensity;
	
	private Float pointSize;
	private Float lineWidth;
	
	private Color emColor;
	private Color stormColor;
	private Color antibodyColor;
	
	private double pixelsize;
	private double sigmaRendering;
	
	private Float pixelToNmRatio;
	private Float frameRate;
	private Float sigmaBg;
	private Float constOffset;
	private Float emGain;
	private Float quantumEfficiency;
	private Float deadTime;
	

	private int windowsizePSF;
	private int emptyPixelsOnRim;
	private Float na;
	private Float psfwidth;
	private Float fokus;
	private Float defokus;
	private boolean twoDPSF;
	private float[][] calibrationFile =  {{0,131.6016f,299.7855f},{101.111f,124.3521f,247.8447f},
			{202.222f,121.4928f,206.5095f},{303.333f,126.1539f,177.7527f},{404.444f,134.6805f, 157.5747f},
			{505.556f,152.1423f, 148.3749f},{606.667f,176.9409f, 145.7982f},{707.778f,212.3208f, 152.4042f},
			{808.889f,252.4194f,164.9916f},{910f,308.4156f,188.8461f}};//rescaled

	private float electronPerAdCount;
	private float meanBlinkingTime;
	
	private boolean ensureSinglePSF;
	private boolean distributePSFoverFrames;
	private int minIntensity;
	private List<Float> borders;
    
	

	public ParameterSet(Float loa, Float aoa, Float soa, Float bspnm, Float pabs,
			Float abpf, Float rof, Float fpab, Float sxy, Float sz, Float doc, Float nocpsmm,
			Float docpsnm, Float bd, Float bspsnm, int frames, Float dutyCycle, Float deff, Float bleachConst, int meanPhotonNumber,
			Boolean generalVisibility, Boolean emVisibility, Boolean stormVisibility, Boolean antibodyVisibility, 
			Float ilpmm3, Float psfwidth, Boolean applyBleaching, Boolean mergedPSF, Boolean coupleSigmaIntensity, 
			Float pointSize, Float lineWidth, Color emColor, Color stormColor, Color antibodyColor, Float pixelToNmRatio,
			Float frameRate, Float sigmaBg, Float constOffset, Float emGain, Float quantumEfficiency, 
			int windowsizePSF, int emptyPixelsOnRim, Float na, Float fokus, Float defokus, boolean twoDPSF,
			float[][] calibrationFile, float electronPerAdCount,float meanBlinkingTime, boolean ensureSinglePSF, 
			boolean distributePSFoverFrames, int minIntensity, List<Float> borders, Float deadTime) {
		super();
		this.loa = loa;
		this.aoa = aoa;
		this.soa = soa;
		this.bspnm = bspnm;
		this.pabs = pabs;
		this.abpf = abpf;
		this.rof = rof;
		this.fpab = fpab;
		this.sxy = sxy;
		this.sz = sz;
		this.doc = doc;
		this.nocpsmm = nocpsmm;
		this.docpsnm = docpsnm;
		this.bd = bd;
		this.bspsnm = bspsnm;
		this.frames = frames;
		this.dutyCycle = dutyCycle;
		this.deff = deff;
		this.bleachConst = bleachConst;
		this.meanPhotonNumber = meanPhotonNumber;
		
		this.generalVisibility = generalVisibility; 
		this.emVisibility = emVisibility;      
		this.stormVisibility = stormVisibility;   
		this.antibodyVisibility = antibodyVisibility;
		
		this.ilpmm3 = ilpmm3;
		this.psfwidth = psfwidth;
		
		this.applyBleaching = applyBleaching;
		this.mergedPSF = mergedPSF;
		this.coupleSigmaIntensity = coupleSigmaIntensity;
		
		this.pointSize = pointSize;
		this.lineWidth = lineWidth;
		
		this.emColor = emColor;          
		this.stormColor = stormColor;       
		this.antibodyColor = antibodyColor;  
		this.pixelToNmRatio = pixelToNmRatio;
		this.frameRate = frameRate;
		this.sigmaBg = sigmaBg;
		this.constOffset = constOffset;
		this.emGain = emGain;
		this.quantumEfficiency =quantumEfficiency;
		this.windowsizePSF = windowsizePSF;
		this.emptyPixelsOnRim = emptyPixelsOnRim;
		this.na = na;
		this.fokus = fokus;
		this.defokus = defokus;
		this.twoDPSF = twoDPSF;
		this.calibrationFile = calibrationFile;
		this.electronPerAdCount=electronPerAdCount;
		this.meanBlinkingTime =meanBlinkingTime;
		this.ensureSinglePSF = ensureSinglePSF;
		this.distributePSFoverFrames = distributePSFoverFrames;
		this.minIntensity = minIntensity;
		this.setBorders(borders);
		this.deadTime = deadTime;
		
	} 
    
    public ParameterSet() {
    	super();
    	this.loa = new Float(16.f); 	
        this.aoa = new Float((float) (90./180.*Math.PI));
        this.soa = new Float(0.f);
        this.bspnm = new Float(13.f/8.f);
        this.pabs = new Float(0.1f); 				
        this.abpf = new Float(14);		
        this.rof = new Float(12.5f);		
        this.fpab = new Float(1.f); 
        this.sxy = new Float(10.0f); 
        this.sz = new Float(30.0f); 
        this.doc = new Float(0.f); 
        this.nocpsmm = new Float(1.f); 
        this.docpsnm = new Float(0.01f);
        this.bd = new Float((float) (3*5*1e-7));
        this.bspsnm = new Float(10/600.f);
        this.frames = 10000;
        this.dutyCycle = 0.0005f;
        this.kOff = dutyCycle * 2000;
        this.deff = 1f;
        this.bleachConst = 2.231e-5f; //corresponds to 80 % after 10000 frames
        this.meanPhotonNumber = 4000;
        
        this.generalVisibility = Boolean.FALSE; 
		this.emVisibility = Boolean.TRUE;      
		this.stormVisibility = Boolean.TRUE;   
		this.antibodyVisibility = Boolean.TRUE;
		
		this.ilpmm3 = new Float(50.f);
		this.psfwidth = new Float(647.f);
		
		this.applyBleaching= Boolean.FALSE;
		this.mergedPSF = Boolean.FALSE;
		this.coupleSigmaIntensity = Boolean.TRUE;
		
		this.pointSize = new Float(2.f);
		this.lineWidth = new Float(2.f);
		
		/**
		 * default colors for visualization
		 */
		this.emColor = new Color(204,0,51);
		this.stormColor = new Color(204,204,255);
		this.antibodyColor = new Color(255,204,102);
		
		this.pixelsize = 10;
		this.sigmaRendering = 20;
		
		this.pixelToNmRatio = 133.f;
		this.frameRate = 30.f;
		this.sigmaBg = 22f;
		this.constOffset = 200.f;
		this.emGain = 10.f;
		this.quantumEfficiency = 1.f;
		this.windowsizePSF = 10;
		this.emptyPixelsOnRim = 5;
		this.na = 1.45f;
		this.fokus = 400.f;
		this.defokus = 800.f;
		this.twoDPSF = true;
		this.electronPerAdCount = 4.81f;
		this.meanBlinkingTime = 0.05f;
		this.ensureSinglePSF = false;
		this.distributePSFoverFrames = true;
		this.minIntensity = 1000;
		this.setBorders((List<Float>) Arrays.asList(-9e10f, 9e10f,-9e10f,9e10f,-9e10f,9e10f));
		this.deadTime = 0f;
    }

    public Float getDeadTime(){
    	return deadTime;
    }
    
    public void setDeadTime(Float deadTime){
    	this.deadTime = deadTime;
    }
    
	public Float getLoa() {
		return loa;
	}

	public void setLoa(Float loa) {
		this.loa = loa;
	}

	public Float getAoa() {
		return aoa;
	}

	public void setAoa(Float aoa) {
		this.aoa = aoa;
	}
	
	public Float getSoa(){
		return this.soa;
	}
	
	public void setSoa(Float soa){
		this.soa = soa;
	}

	public Float getBspnm() {
		return bspnm;
	}

	public void setBspnm(Float bspnm) {
		this.bspnm = bspnm;
	}

	public Float getPabs() {
		return pabs;
	}

	public void setPabs(Float pabs) {
		this.pabs = pabs;
	}

	public Float getAbpf() {
		return abpf;
	}

	public void setAbpf(Float abpf) {
		this.abpf = abpf;
	}

	public Float getRof() {
		return rof;
	}

	public void setRof(Float rof) {
		this.rof = rof;
	}

	public Float getFpab() {
		return fpab;
	}

	public void setFpab(Float fpab) {
		this.fpab = fpab;
	}
	
	public int getFrames(){
		return frames;
	}
	
	public void setFrames(int frames){
		this.frames = frames;
	}
	
	public Float getDutyCycle(){
		return dutyCycle;
	}
	
	public void setDutyCycle(Float dutyCycle){
		this.dutyCycle = dutyCycle;
	}
	
	public Float getDeff(){
		return deff;
	}
	
	public void setDeff(Float deff){
		this.deff = deff;
	}
	
	public Float getBleachConst(){
		return this.bleachConst;
	}
	
	public void setBleachConst(Float bleachConst){
		this.bleachConst = bleachConst;
	}

	public Float getSxy() {
		return sxy;
	}

	public void setSxy(Float sxy) {
		this.sxy = sxy;
	}

	public Float getSz() {
		return sz;
	}

	public void setSz(Float sz) {
		this.sz = sz;
	}

	public Float getDoc() {
		return doc;
	}

	public void setDoc(Float doc) {
		this.doc = doc;
	}

	public Float getNocpsmm() {
		return nocpsmm;
	}

	public void setNocpsmm(Float nocpsmm) {
		this.nocpsmm = nocpsmm;
	}

	public Float getDocpsnm() {
		return docpsnm;
	}

	public void setDocpsnm(Float docpsnm) {
		this.docpsnm = docpsnm;
	}

	public Float getBd() {
		return bd;
	}

	public void setBd(Float bd) {
		this.bd = bd;
	}

	public Float getBspsnm() {
		return bspsnm;
	}

	public void setBspsnm(Float bspsnm) {
		this.bspsnm = bspsnm;
	}

	public Boolean getGeneralVisibility() {
		return generalVisibility;
	}

	public void setGeneralVisibility(Boolean generalVisibility) {
		this.generalVisibility = generalVisibility;
	}

	public Boolean getEmVisibility() {
		return emVisibility;
	}

	public void setEmVisibility(Boolean emVisibility) {
		this.emVisibility = emVisibility;
	}

	public Boolean getStormVisibility() {
		return stormVisibility;
	}

	public void setStormVisibility(Boolean stormVisibility) {
		this.stormVisibility = stormVisibility;
	}

	public Boolean getAntibodyVisibility() {
		return antibodyVisibility;
	}

	public void setAntibodyVisibility(Boolean antibodyVisibility) {
		this.antibodyVisibility = antibodyVisibility;
	}

	public Float getIlpmm3() {
		return ilpmm3;
	}

	public void setIlpmm3(Float ilpmm3) {
		this.ilpmm3 = ilpmm3;
	}

	public Float getPsfwidth() {
		return psfwidth;
	}

	public void setPsfwidth(Float psfwidth) {
		this.psfwidth = psfwidth;
	}

	public Boolean getMergedPSF() {
		return mergedPSF;
	}

	public void setMergedPSF(Boolean mergedPSF) {
		this.mergedPSF = mergedPSF;
	}
	
	public void setCoupleSigmaIntensity(Boolean coupleSigmaIntensity){
		this.coupleSigmaIntensity = coupleSigmaIntensity;
	}
	
	public Boolean getCoupleSigmaIntensity(){
		return this.coupleSigmaIntensity;
	}

	public Float getPointSize() {
		return pointSize;
	}

	public void setPointSize(Float pointSize) {
		this.pointSize = pointSize;
	}

	public Color getEmColor() {
		return emColor;
	}

	public void setEmColor(Color emColor) {
		this.emColor = emColor;
	}

	public Color getStormColor() {
		return stormColor;
	}

	public void setStormColor(Color stormColor) {
		this.stormColor = stormColor;
	}

	public Color getAntibodyColor() {
		return antibodyColor;
	}

	public void setAntibodyColor(Color antibodyColor) {
		this.antibodyColor = antibodyColor;
	}

	public void setLineWidth(Float lineWidth) {
		this.lineWidth = lineWidth;	
	}
	public void setMeanPhotonNumber(int meanPhotonNumber){
		this.meanPhotonNumber = meanPhotonNumber;
	}
	public int getMeanPhotonNumber(){
		return this.meanPhotonNumber;
	}

	public void setApplyBleaching(boolean applyBleaching) {
		this.applyBleaching = applyBleaching;
	}
	
	public Boolean getApplyBleaching(){
		return this.applyBleaching;
	}
	
	public void setStormVisibility(boolean stormVisibility){
		this.stormVisibility = stormVisibility;
	}
	
	public void setEmVisibility(boolean emVisibility){
		this.emVisibility = emVisibility;
	}
	
	public void setAntibodyVisibility(boolean abVisibility){
		this.antibodyVisibility = abVisibility;
	}
	
	public void setGeneralVisibility(boolean generalVisibility){
		this.generalVisibility = generalVisibility;
	}

	public Float getLineWidth() {
		// TODO Auto-generated method stub
		return this.lineWidth;
	}

	public double getPixelsize() {
		return pixelsize;
	}

	public void setPixelsize(double pixelsize) {
		this.pixelsize = pixelsize;
	}

	public double getSigmaRendering() {
		return sigmaRendering;
	}

	public void setSigmaRendering(double sigmaRendering) {
		this.sigmaRendering = sigmaRendering;
	}

	public Float getPixelToNmRatio() {
		return pixelToNmRatio;
	}

	public void setPixelToNmRatio(Float pixelToNmRatio) {
		this.pixelToNmRatio = pixelToNmRatio;
	}

	public Float getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(Float frameRate) {
		this.frameRate = frameRate;
	}

	public Float getSigmaBg() {
		return sigmaBg;
	}

	public void setSigmaBg(Float sigmaBg) {
		this.sigmaBg = sigmaBg;
	}

	public Float getConstOffset() {
		return constOffset;
	}

	public void setConstOffset(Float constOffset) {
		this.constOffset = constOffset;
	}

	public Float getEmGain() {
		return emGain;
	}

	public void setEmGain(Float emGain) {
		this.emGain = emGain;
	}
	public Float getQuantumEfficiency() {
		return quantumEfficiency;
	}

	public void setQuantumEfficiency(Float quantumEfficiency) {
		this.quantumEfficiency = quantumEfficiency;
	}

	public int getWindowsizePSF() {
		return windowsizePSF;
	}

	public void setWindowsizePSF(int windowsizePSF) {
		this.windowsizePSF = windowsizePSF;
	}

	public int getEmptyPixelsOnRim() {
		return emptyPixelsOnRim;
	}

	public void setEmptyPixelsOnRim(int emptyPixelsOnRim) {
		this.emptyPixelsOnRim = emptyPixelsOnRim;
	}

	public Float getNa() {
		return na;
	}

	public void setNa(Float na) {
		this.na = na;
	}

	public float getFokus() {
		return fokus;
	}

	public void setFokus(Float fokus) {
		this.fokus = fokus;
	}

	public Float getDefokus() {
		return defokus;
	}

	public void setDefokus(Float defokus) {
		this.defokus = defokus;
	}

	public boolean isTwoDPSF() {
		return twoDPSF;
	}

	public void setTwoDPSF(boolean twoDPSF) {
		this.twoDPSF = twoDPSF;
	}

	public float[][] getCalibrationFile() {
		return calibrationFile;
	}

	public void setCalibrationFile(float[][] calibrationFile) {
		this.calibrationFile = calibrationFile;
	}

	public float getElectronPerAdCount() {
		return electronPerAdCount;
	}

	public void setElectronPerAdCount(float electronPerAdCount) {
		this.electronPerAdCount = electronPerAdCount;
	}

	public float getMeanBlinkingTime() {
		return meanBlinkingTime;
	}

	public void setMeanBlinkingTime(float meanBlinkingTime) {
		this.meanBlinkingTime = meanBlinkingTime;
	}
	public boolean isEnsureSinglePSF() {
		return ensureSinglePSF;
	}

	public void setEnsureSinglePSF(boolean ensureSinglePSF) {
		this.ensureSinglePSF = ensureSinglePSF;
	}

	public boolean isDistributePSFoverFrames() {
		return distributePSFoverFrames;
	}

	public void setDistributePSFoverFrames(boolean distributePSFoverFrames) {
		this.distributePSFoverFrames = distributePSFoverFrames;
	}

	public int getMinIntensity() {
		return minIntensity;
	}

	public void setMinIntensity(int minIntensity) {
		this.minIntensity = minIntensity;
	}

	public ArrayList<Float> getBorders() {
		return (ArrayList<Float>) borders;
	}

	public void setBorders(List<Float> borders2) {
		this.borders = borders2;
	}
}