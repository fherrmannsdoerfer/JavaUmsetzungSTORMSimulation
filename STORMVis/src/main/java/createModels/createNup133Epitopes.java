package createModels;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import calc.Calc;

public class createNup133Epitopes {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		createNupData();
	}
	
	public static void createNupData(){
		int nbrPores = 1000;
		Random random = new Random(2);
		float[][] epPore = {{570,501,100},{530,501,100},{501,529,100},{500,571,100}, {529,600,100},{571,600,100}, {600,571,100},{600,529,100}};
		float[][] apPore = {{570,501,100},{530,501,100},{501,529,100},{500,571,100}, {529,600,100},{571,600,100}, {600,571,100},{600,529,100}};
		ArrayList<Double> shiftX = new ArrayList<Double>();
		ArrayList<Double> shiftY = new ArrayList<Double>();
		
		for ( int i = 0 ; i<nbrPores;i++){
			shiftX.add(random.nextDouble()*16000);
			shiftY.add(random.nextDouble()*16000);
		}
		float meanX = 0;
		float meanY = 0;
		float meanZ= 0;
		for (int i = 0; i<epPore.length;i++){
			meanX+= epPore[i][0];
			meanY+= epPore[i][1];
			meanZ+= epPore[i][2];
		}
		meanX /= 8;
		meanY /= 8;
		meanZ /= 8;
		
		for (int i = 0; i<epPore.length;i++){
			epPore[i][0]-=meanX;
			epPore[i][1]-=meanY;
			epPore[i][2]-=meanZ;
		}
		ArrayList<ArrayList<Float>> epal = new ArrayList<ArrayList<Float>>();//float[nbrPores][3];
		for (int i = 0;i<shiftX.size();i++){
			double phi = random.nextDouble()*2*Math.PI;
			int vielfaches =4;
			for (int j=0;j<8;j++){
				for (int kk = 0;kk<vielfaches;kk++){
					ArrayList<Float> tmp = new ArrayList<Float>();
					tmp.add((float) ((Math.cos(phi)*epPore[j][0]+Math.sin(phi)*epPore[j][1])+shiftX.get(i)));
					tmp.add((float) ((-Math.sin(phi)*epPore[j][0]+Math.cos(phi)*epPore[j][1])+shiftY.get(i)));
					tmp.add((float) 0);
					double rand = random.nextDouble();
					epal.add(tmp);
				}
			}
		}
		writeEpitopeData(epal);
		
	}
	public static void writeEpitopeData(ArrayList<ArrayList<Float>> epBase){
		String basename = "C:\\Users\\herrmannsdoerfer\\Desktop\\SuReSim\\Nup133Epitopes";
		try{
			FileWriter writer = new FileWriter(basename+"Localizations.txt");
			writer.append("Pos_x Pos_y Pos_z n_x n_y n_z\n");
			for (int i = 0; i<epBase.size(); i++){
				writer.append(epBase.get(i).get(0)+" "+epBase.get(i).get(1)+" "+epBase.get(i).get(2)+" "+0+" "+0+" "+ "1\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}
}
