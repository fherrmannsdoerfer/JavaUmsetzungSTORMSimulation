package gui;

import gui.DataTypeDetector.DataType;

import java.io.IOException;

import model.DataSet;
import parsing.LineObjectParser;
import parsing.TriangleObjectParser;

public class ParserWrapper {
	
	public static DataSet parseFileOfType(String path, DataType type) {
		if(type.equals(DataType.TRIANGLES)) {
			TriangleObjectParser trParser = new TriangleObjectParser(path);
//			trParser.limit = 0;
			try {
				trParser.parse();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return trParser.wrapParsedObjectsToTriangleDataSet();
		}
		else if (type.equals(DataType.LINES)) {
			LineObjectParser lineParser = new LineObjectParser(path);
			try {
				lineParser.parse();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return lineParser.wrapParsedObjectsToLineDataSet();
		}
		else {
			return null;
		}
	}
}
