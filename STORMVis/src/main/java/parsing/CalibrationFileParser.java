package parsing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import calc.Calc;

public class CalibrationFileParser {
	String path;
	InputStream is = null;
	BufferedReader br;
	public CalibrationFileParser(String path){
		this.path = path;
	}
	
	public float[][] parse() throws IOException{
		return parse(path);
	}
	
	private float[][] parse(String path) throws IOException{
		try {
			br = new BufferedReader(new FileReader(path));
		}
		catch(Exception e){
			is = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
		    br = new BufferedReader(new InputStreamReader(is));
		}
        String line;
        List<float[]> calib = new ArrayList<float[]>();
        line = br.readLine(); //skip header
        while ((line = br.readLine()) != null) {
        	String[] parts = line.split(" ");
        	if (parts.length == 3){
        		float[] tmp = new float[3];
        		tmp[0] = Float.valueOf(parts[0]);
        		tmp[1] = Float.valueOf(parts[1]);
        		tmp[2] = Float.valueOf(parts[2]);
        		calib.add(tmp);
        	}
        	else{
        		System.err.println("Data formate of calibration file not understood. A Z calibration file is a plain text file with three whitespace-separated columns. "
        				+ "The first column gives an emitter's Z coordinate in nanometres, and the second and third columns give the standard deviation of a Gaussian "
        				+ "describing the PSF for an emitter at the given Z coordinate.");
        	}
        	
        }
		return Calc.toFloatArray(calib);
        
	}
}
