package createModels;

import inout.FileManager;

import java.util.ArrayList;

import org.jzy3d.maths.Coord3d;

import model.LineDataSet;
import model.ParameterSet;

public class createActinRings {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		createRingsData();
	}

	private static void createRingsData() {
		// TODO Auto-generated method stub
		double radius = 100; //nm
		double dist = 200; //nm
		int nbrElementsPerRing = 50;
		int nbrRings = 12;
		LineDataSet set = new LineDataSet(new ParameterSet());
		
		for (int i = 0; i<nbrRings; i++){
			ArrayList<Coord3d> tmp = new ArrayList<Coord3d>();
			for (int j = 0; j<nbrElementsPerRing; j++){
				double angle = (Math.PI*2)/(nbrElementsPerRing-1.)*j;
				tmp.add(new Coord3d(radius*Math.cos(angle),i*dist, radius*Math.sin(angle)));
			}
			set.data.add(tmp);
		}
		FileManager.writeWIMPFile(set, "c:\\users\\herrmannsdoerfer\\desktop\\test.wimp");
	}
}
