package gui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DataTypeDetector {
	public enum DataType {
	    NFF, WIMP, UNKNOWN
	}
	
	public static DataType getDataType(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        int lineNumber = 0;
        while ((line = br.readLine()) != null && lineNumber < 50) {
        	lineNumber++;
            if (line.contains("Object #:")) {
            	return DataType.WIMP;
            }
            else if (line.contains("pp 3")) {
            	return DataType.NFF;
            }
        }
        return DataType.UNKNOWN;
	}
}


