package model;

import java.awt.Color;
import java.io.Serializable;

public class ParameterSet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Float loa;	
    public Float aoa; 
    public Float bspnm;
    public Float pabs; 				
    public Float abpf;		
    public Float rof;		
    public Float fpab; 
    public int[] colorEM; 
    public int[] colorSTORM;
    public int[] colorAB; 
    public Float sxy; 
    public Float sz; 
    public Float doc; 
    public Float nocpsmm; 
    public Float docpsnm;
    public Float bd;
    public Float bspsnm;
    public int frames;
    public Float kOn;
    public Float kOff;
    public int meanPhotonNumber;
    
    public Boolean generalVisibility;
    public Boolean emVisibility;
    public Boolean stormVisibility;
    public Boolean antibodyVisibility;
    
    public Float ilpmm3;
	public Float psfwidth;
	public Boolean mergedPSF;
	public Boolean coupleSigmaIntensity;
	
	public Float pointSize;
	public Float lineWidth;
	
	public Color emColor;
	public Color stormColor;
	public Color antibodyColor;
    
    
	public ParameterSet(Float loa, Float aoa, Float bspnm, Float pabs,
			Float abpf, Float rof, Float fpab, int[] colorEM, int[] colorSTORM,
			int[] colorAB, Float sxy, Float sz, Float doc, Float nocpsmm,
			Float docpsnm, Float bd, Float bspsnm, int frames, Float kOn, Float kOff, int meanPhotonNumber,
			Boolean generalVisibility, Boolean emVisibility, Boolean stormVisibility, Boolean antibodyVisibility, 
			Float ilpmm3, Float psfwidth, Boolean mergedPSF, Boolean coupleSigmaIntensity, Float pointSize, Float lineWidth
			, Color emColor, Color stormColor, Color antibodyColor) {
		super();
		this.loa = loa;
		this.aoa = aoa;
		this.bspnm = bspnm;
		this.pabs = pabs;
		this.abpf = abpf;
		this.rof = rof;
		this.fpab = fpab;
		this.colorEM = colorEM;
		this.colorSTORM = colorSTORM;
		this.colorAB = colorAB;
		this.sxy = sxy;
		this.sz = sz;
		this.doc = doc;
		this.nocpsmm = nocpsmm;
		this.docpsnm = docpsnm;
		this.bd = bd;
		this.bspsnm = bspsnm;
		this.frames = frames;
		this.kOff = kOff;
		this.kOn = kOn;
		this.meanPhotonNumber = meanPhotonNumber;
		
		this.generalVisibility = generalVisibility; 
		this.emVisibility = emVisibility;      
		this.stormVisibility = stormVisibility;   
		this.antibodyVisibility = antibodyVisibility;
		
		this.ilpmm3 = ilpmm3;
		this.psfwidth = psfwidth;
		
		this.mergedPSF = mergedPSF;
		this.coupleSigmaIntensity = coupleSigmaIntensity;
		
		this.pointSize = pointSize;
		this.lineWidth = lineWidth;
		
		this.emColor = emColor;          
		this.stormColor = stormColor;       
		this.antibodyColor = antibodyColor;    
	} 
    
    public ParameterSet() {
    	super();
    	this.loa = new Float(16.f); 	
        this.aoa = new Float((float) (90./180.*Math.PI));
        this.bspnm = new Float(13.f/8.f);
        this.pabs = new Float(0.1f); 				
        this.abpf = new Float(14);		
        this.rof = new Float(12.5f);		
        this.fpab = new Float(1.f); 
        this.colorEM = new int[]{0,0,0}; 
        this.colorSTORM = new int[]{1,0,0};
        this.colorAB = new int[]{0,1,0}; 
        this.sxy = new Float(10.0f); 
        this.sz = new Float(30.0f); 
        this.doc = new Float(0.f); 
        this.nocpsmm = new Float(1.f); 
        this.docpsnm = new Float(0.01f);
        this.bd = new Float((float) (3*5*1e-7));
        this.bspsnm = new Float(10/600.f);
        this.frames = 10000;
        this.kOn = 1.f;
        this.kOff = 2000.f;
        this.meanPhotonNumber = 4000;
        
        this.generalVisibility = Boolean.FALSE; 
		this.emVisibility = Boolean.TRUE;      
		this.stormVisibility = Boolean.TRUE;   
		this.antibodyVisibility = Boolean.TRUE;
		
		this.ilpmm3 = new Float(50.f);
		this.psfwidth = new Float(200.f);
		
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
	
	public Float getKOn(){
		return kOn;
	}
	
	public void setKOn(Float kOn){
		this.kOn = kOn;
	}
	
	public Float getKOff(){
		return kOff;
	}
	
	public void setKOff(Float kOff){
		this.kOff = kOff;
	}

	public int[] getColorEM() {
		return colorEM;
	}

	public void setColorEM(int[] colorEM) {
		this.colorEM = colorEM;
	}

	public int[] getColorSTORM() {
		return colorSTORM;
	}

	public void setColorSTORM(int[] colorSTORM) {
		this.colorSTORM = colorSTORM;
	}

	public int[] getColorAB() {
		return colorAB;
	}

	public void setColorAB(int[] colorAB) {
		this.colorAB = colorAB;
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
	
    
    /**
     * 
     * @return default parameterSet with hard-coded values
     */
//    public static ParameterSet defaultParameterSet() {
//    	Float loa = new Float(1.f); 	
//        Float aoa = new Float((float) (90./180.*Math.PI));
//        Float bspnm = new Float(1/2.75f);
//        Float pabs = new Float(0.1f); 				
//        Float abpf = new Float(14);		
//        Float rof = new Float(3.5f);		
//        Float fpab = new Float(1.5f); 
//        int[] colorEM = new int[]{0,0,0}; 
//        int[] colorSTORM = new int[]{1,0,0};
//        int[] colorAB = new int[]{0,1,0}; 
//        Float sxy = new Float(1.0f); 
//        Float sz = new Float(1.0f); 
//        Float doc = new Float(0.f); 
//        Float nocpsmm = new Float(1.f); 
//        Float docpsnm = new Float(0.01f);
//        Float bd = new Float((float) (3*5*1e-7));
//        Float bspsnm = new Float(10/600.f); 
//        
//        Boolean generalVisibility = Boolean.FALSE; 
//        Boolean emVisibility = Boolean.FALSE;      
//        Boolean stormVisibility = Boolean.FALSE;   
//        Boolean antibodyVisibility = Boolean.FALSE;
//        
//        Float ilpmm3 = new Float(50.f);
//    	return new ParameterSet(loa, aoa, bspnm,pabs, abpf, rof, fpab, colorEM, colorSTORM, colorAB, sxy, sz, doc, nocpsmm, docpsnm, bd, bspsnm, generalVisibility, emVisibility, stormVisibility, antibodyVisibility,ilpmm3);
//    }
    
    
    
}