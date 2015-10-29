package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;

import parsing.LineObjectParser;
import parsing.TriangleObjectParser;
import model.DataSet;
import model.LineDataSet;
import model.ParameterSet;
import model.TriangleDataSet;

public class ExamplesProvidingClass {
	public static DataSet getDataset(int number){
		DataSet retSet;
		switch (number){
		case 1:
			retSet = importMicrotubules();
			break;
		case 2:
			retSet = importMitochondria();
			break;
		case 3:
			retSet = importCrossingLines2D();
			break;
		case 4:
			retSet = importCrossingLines3D();
			break;
		case 5:
			retSet = importShpere();
			break;
		case 6:
			retSet = importTriangle();
			break;
		default:
			retSet = null;
		}
		return retSet;
	}
	
	private static DataSet importMitochondria() {
		TriangleObjectParser top = new TriangleObjectParser("Mitochondria.nff");
		try {
			top.parse();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TriangleDataSet set = top.wrapParsedObjectsToTriangleDataSet();
		set.name = "Mitochondria";
		return set;
	}

	private static DataSet importMicrotubules() {
		LineDataSet set = new LineDataSet(new ParameterSet());
		LineObjectParser lop = new LineObjectParser("Microtubules.wimp");
		List<ArrayList<Coord3d>> allObjects = null;
		try {
			allObjects= lop.parse("Microtubules.wimp");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		set.data = allObjects;
		set.name = "Microtubules";
		return set;
	}

	private static DataSet importTriangle() {
		TriangleDataSet set = new TriangleDataSet(new ParameterSet());
		List<Polygon> newList = new ArrayList<Polygon>();
    	for (int i = 0; i<4; i++){
    		Polygon pNew = new Polygon();
    		pNew.add(new Point(new Coord3d(0, 0, 0+i*200)));
    		pNew.add(new Point(new Coord3d(200, 0,0+i*200)));
    		pNew.add(new Point(new Coord3d(100, Math.sqrt(40000-10000), 0+i*200)));
    		newList.add(pNew);
    	}
    	set.drawableTriangles=newList;
    	
    	List<float[][]> prim = new ArrayList<float[][]>();
    	for(Polygon p : set.drawableTriangles) {
	    	float[][] tr = new float[3][3];
	    	for(int i = 0; i < p.getPoints().size(); i++) {
		    	tr[i][0] = p.getPoints().get(i).xyz.x;
		    	tr[i][1] = p.getPoints().get(i).xyz.y;
		    	tr[i][2] = p.getPoints().get(i).xyz.z;
	    	}
	    	prim.add(tr);
    	}
		set.primitives = prim;
		set.name = "Triangle";
		return set;
	}

	private static DataSet importShpere() {
		TriangleDataSet set = new TriangleDataSet(new ParameterSet());
		int zsteps = 201;
		int steps = 40;
		float[] z = new float[zsteps];
		
		for (int i=0, val = -100;i<zsteps;i=i+1, val++){
			z[i]=val/100.f;
		}
		float[][] x = new float[zsteps][steps];
		float[][] y = new float[zsteps][steps];
		float[][] zz =new float[zsteps][steps];
		float factor = 20;
		for (int i = 0; i<z.length; i++){
			for (int j=0; j<steps; j++){
				x[i][j] = (float) (Math.pow(1-Math.pow(z[i],2),0.5)*Math.cos(Math.PI*j/(steps/2)))*factor;
				y[i][j] = (float) (Math.pow(1-Math.pow(z[i],2),0.5)*Math.sin(Math.PI*j/(steps/2)))*factor;
				zz[i][j]= z[i]*factor;
			}
		}
		List<Polygon> newList = new ArrayList<Polygon>();
		for (int i = 0; i<z.length-1; i++){
			for (int j = 0; j<steps-1; j++){
				Polygon pNew = new Polygon();
	    		pNew.add(new Point(new Coord3d(x[i][j], y[i][j], zz[i][j])));
	    		pNew.add(new Point(new Coord3d(x[i+1][j+1],y[i+1][j+1],zz[i+1][j+1])));
	    		pNew.add(new Point(new Coord3d(x[i+1][j],y[i+1][j],zz[i+1][j])));
	    		newList.add(pNew);
	    		Polygon pNew2 = new Polygon();
	    		pNew2.add(new Point(new Coord3d(x[i][j], y[i][j], zz[i][j])));
	    		pNew2.add(new Point(new Coord3d(x[i][j+1],y[i][j+1],zz[i][j+1])));
	    		pNew2.add(new Point(new Coord3d(x[i+1][j+1],y[i+1][j+1],zz[i+1][j+1])));
	    		newList.add(pNew2);
			}
			int j = steps - 2;
			Polygon pNew = new Polygon();
    		pNew.add(new Point(new Coord3d(x[i][j+1], y[i][j+1], zz[i][j+1])));
    		pNew.add(new Point(new Coord3d(x[i+1][0],y[i+1][0],zz[i+1][0])));
    		pNew.add(new Point(new Coord3d(x[i+1][j+1],y[i+1][j+1],zz[i+1][j+1])));
    		newList.add(pNew);
    		Polygon pNew2 = new Polygon();
    		pNew2.add(new Point(new Coord3d(x[i][j+1], y[i][j+1], zz[i][j+1])));
    		pNew2.add(new Point(new Coord3d(x[i][0],y[i][0],zz[i][0])));
    		pNew2.add(new Point(new Coord3d(x[i+1][0],y[i+1][0],zz[i+1][0])));
    		newList.add(pNew2);
		}
		
		set.drawableTriangles=newList;
    	
    	List<float[][]> prim = new ArrayList<float[][]>();
    	for(Polygon p : set.drawableTriangles) {
	    	float[][] tr = new float[3][3];
	    	for(int i = 0; i < p.getPoints().size(); i++) {
		    	tr[i][0] = p.getPoints().get(i).xyz.x;
		    	tr[i][1] = p.getPoints().get(i).xyz.y;
		    	tr[i][2] = p.getPoints().get(i).xyz.z;
	    	}
	    	prim.add(tr);
    	}
		set.primitives = prim;
		set.name = "Spheres";
		return set;
	}

	private static DataSet importCrossingLines3D() {
		LineDataSet set = new LineDataSet(new ParameterSet());
		ArrayList<ArrayList<Coord3d>> list = new ArrayList<ArrayList<Coord3d>>();
		
		for (int i = 0; i<2; i++){
			ArrayList<Coord3d> tmp = new ArrayList<Coord3d>(); 
			int nbrPoints = 100;
			float prefac = -.3f;
			float zz = 50;
			if (i == 0){
				prefac = .3f;
				zz = -50;
			}
			for (int j = 0; j<nbrPoints; j++){
				Coord3d tmpCoord = new Coord3d();
				tmpCoord.x = -1000 + (2000/nbrPoints * j);
				tmpCoord.y = prefac*(-1000 + (2000/nbrPoints * j));
				tmpCoord.z = zz;
				tmp.add(tmpCoord);
			}
			list.add(tmp);
		}
	
		set.data = list;
		set.name = "Crossing Lines 3D";
		return set;
	}

	private static DataSet importCrossingLines2D() {
		LineDataSet set = new LineDataSet(new ParameterSet());
		ArrayList<ArrayList<Coord3d>> list = new ArrayList<ArrayList<Coord3d>>();
		for (int i = 0; i<2; i++){
			ArrayList<Coord3d> tmp = new ArrayList<Coord3d>(); 
			int nbrPoints = 100;
			float prefac = -.3f;
			if (i == 0){
				prefac = .3f;
			}
			for (int j = 0; j<nbrPoints; j++){
				Coord3d tmpCoord = new Coord3d();
				tmpCoord.x = -1000 + (2000/nbrPoints * j);
				tmpCoord.y = prefac*(-1000 + (2000/nbrPoints * j));
				tmpCoord.z = 0;
				tmp.add(tmpCoord);
			}
			list.add(tmp);
		}
	
		set.data = list;
		set.name = "Crossing Lines 2D";
		return set;
	}

}
