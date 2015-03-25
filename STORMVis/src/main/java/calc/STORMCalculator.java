package calc;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;

import common.ScatterDemo;
import parsing.TriangleObjectParser;

public class STORMCalculator {

	/**
	 * @param args
	 */
	
	static String fileName = "mito.nff";
	static String outputname ="out.txt";
	
	//rng(5);	
    float loa = 1.f; 	//length of both antibodies combined
    float aoa = (float) (90./180.*Math.PI);		 //angle of antibody
    //bspnm = 1.65; 					//binding sites per nm  //for microtubules with only alpha tubuli stained it should be like 13/8 1.65
    float bspnm = 1/2.75f;
    float pabs = 0.1f; 				//part of available binding sites
    float abpf = 14;		// average blinking per fluorophor
    float rof = 3.5f;			//radius of filament
    float fpab = 1.5f; //fluorophores per antibody

    int[] colorEM = {0,0,0}; //color of EM data
    int[] colorSTORM = {1,0,0}; //color of STORM result
    int[] colorAB = {0,1,0}; //color of Antibody
    float sxy = 1.0f; //sigma of fitting error in xy direction
    float sz = 1.0f; //sigma of fitting error in z direction
    
    float doc = 0; //degree of clustering, part of all localizations that are clustered
    float nocpsmm = 1; //number of clusters per square micrometer
    float docpsnm = 0.01f; //denstiy of clusters in antibodies per square nm
    float bd = (float) (3*5*1e-7); //blinking density in number fluorophores per square nm
//    float bd = (float) (5*1e-7);
    float bspsnm = 10/600.f; 
    //.0159/2; //binding sites per square nanometer
    
    List<float[][]> trList;
	
	public STORMCalculator() {
		
	}
	
	public void startCalculation() throws Exception {
		long start = System.nanoTime();
		TriangleObjectParser trParser = new TriangleObjectParser(null);
		trParser.limit = 0;
		try {
			trParser.parse();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		trList = trParser.primitives;
		doSimulation();
		System.out.println("Whole parsing, converting and simulation time: "+ (System.nanoTime()-start)/1e9 +"s");
		System.out.println("-------------------------------------");
	}
	
	public void doSimulation() throws Exception {
		float[][] ep = null;
		try{
			Pair<float[][],float[][]> p = Finder.findAntibodiesTri(trList, bspsnm, pabs, loa, (float) aoa, doc, nocpsmm, docpsnm);
			ep = p.getValue1();
			System.out.println("Antibodies found");
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		System.out.println("ep length:" + ep.length);
		Coord3d[] allObjects = new Coord3d[ep.length];
		Color[] colors = new Color[ep.length];
		for (int i = 0; i < ep.length; i++) {
			Coord3d coord = new Coord3d(ep[i][0], ep[i][1], ep[i][2]);
			allObjects[i] = coord;
			colors[i] = new Color(coord.x/255.f, coord.y/255.f, coord.z/255.f, 1.f);
		}
		ScatterDemo demo = new ScatterDemo();
		demo.stormColors = colors;
		demo.stormPoints = allObjects;
		demo.STORM = true;
		Coord3d vp = demo.getChart().getViewPoint();
//		AnalysisLauncher.open(demo);
		
		//Calc.print2dMatrix(ep);
		float[][] result = StormPointFinder.findStormPoints(ep, abpf, sxy, sz, bd, fpab, true);
		System.out.println("result length: "+ result.length);
		Coord3d[] allObjects2 = new Coord3d[result.length];
		Color[] colors2 = new Color[result.length];
		for (int i = 0; i < result.length; i++) {
			Coord3d coord = new Coord3d(result[i][0], result[i][1], result[i][2]);
			allObjects2[i] = coord;
			colors2[i] = new Color(coord.x/255.f, coord.y/255.f, coord.z/255.f, 1.f);
		}
		ScatterDemo demo2 = new ScatterDemo();
		demo2.stormColors = colors2;
		demo2.stormPoints = allObjects2;
		demo2.STORM = true;
		
//		demo2.getChart().setViewPoint(vp);
		
		AnalysisLauncher.open(demo2);
		
	}
}
