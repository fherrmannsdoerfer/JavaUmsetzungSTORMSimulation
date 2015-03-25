package model;

import java.io.Serializable;

public class ParameterSet implements Serializable {
	Float loa;	
    Float aoa; 
    Float bspnm;
    Float pabs; 				
    Float abpf;		
    Float rof;		
    Float fpab; 
    int[] colorEM; 
    int[] colorSTORM;
    int[] colorAB; 
    Float sxy; 
    Float sz; 
    Float doc; 
    Float nocpsmm; 
    Float docpsnm;
    Float bd;
    Float bspsnm;
	public ParameterSet(Float loa, Float aoa, Float bspnm, Float pabs,
			Float abpf, Float rof, Float fpab, int[] colorEM, int[] colorSTORM,
			int[] colorAB, Float sxy, Float sz, Float doc, Float nocpsmm,
			Float docpsnm, Float bd, Float bspsnm) {
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
	} 
    
    public ParameterSet() {
    	super();
    	this.loa = new Float(1.f); 	
        this.aoa = new Float((float) (90./180.*Math.PI));
        this.bspnm = new Float(1/2.75f);
        this.pabs = new Float(0.1f); 				
        this.abpf = new Float(14);		
        this.rof = new Float(3.5f);		
        this.fpab = new Float(1.5f); 
        this.colorEM = new int[]{0,0,0}; 
        this.colorSTORM = new int[]{1,0,0};
        this.colorAB = new int[]{0,1,0}; 
        this.sxy = new Float(1.0f); 
        this.sz = new Float(1.0f); 
        this.doc = new Float(0.f); 
        this.nocpsmm = new Float(1.f); 
        this.docpsnm = new Float(0.01f);
        this.bd = new Float((float) (3*5*1e-7));
        this.bspsnm = new Float(10/600.f);
    }
    
    public static ParameterSet defaultParameterSet() {
    	Float loa = new Float(1.f); 	
        Float aoa = new Float((float) (90./180.*Math.PI));
        Float bspnm = new Float(1/2.75f);
        Float pabs = new Float(0.1f); 				
        Float abpf = new Float(14);		
        Float rof = new Float(3.5f);		
        Float fpab = new Float(1.5f); 
        int[] colorEM = new int[]{0,0,0}; 
        int[] colorSTORM = new int[]{1,0,0};
        int[] colorAB = new int[]{0,1,0}; 
        Float sxy = new Float(1.0f); 
        Float sz = new Float(1.0f); 
        Float doc = new Float(0.f); 
        Float nocpsmm = new Float(1.f); 
        Float docpsnm = new Float(0.01f);
        Float bd = new Float((float) (3*5*1e-7));
        Float bspsnm = new Float(10/600.f); 
    	return new ParameterSet(loa, aoa, bspnm,pabs, abpf, rof, fpab, colorEM, colorSTORM, colorAB, sxy, sz, doc, nocpsmm, docpsnm, bd, bspsnm);
    }
}
