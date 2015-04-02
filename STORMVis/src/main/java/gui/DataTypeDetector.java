package gui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class DataTypeDetector {
	public enum DataType {
	    TRIANGLES, LINES, UNKNOWN
	}
	
	public static DataType getDataType(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        int lineNumber = 0;
        while ((line = br.readLine()) != null && lineNumber < 50) {
        	lineNumber++;
            if (line.contains("Object #:")) {
            	return DataType.LINES;
            }
            else if (line.contains("pp 3")) {
            	return DataType.TRIANGLES;
            }
        }
        return DataType.UNKNOWN;
	}
}


