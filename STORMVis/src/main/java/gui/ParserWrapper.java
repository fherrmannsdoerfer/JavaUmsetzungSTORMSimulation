package gui;

import gui.DataTypeDetector.DataType;

import java.io.IOException;

import model.DataSet;
import parsing.EpitopeObjectParser;
import parsing.LineObjectParser;
import parsing.PointObjectParser;
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
		else if (type.equals(DataType.PLY)) {
			TriangleObjectParser trParser = new TriangleObjectParser(path,type);
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
		else if (type.equals(DataType.POINTS)){
			PointObjectParser pointParser = new PointObjectParser(path);
			try{
				pointParser.parse();
			} catch (IOException e){
				e.printStackTrace();
			}
			return pointParser.wrapParsedObjectsToPointDataSet();
		}
		else if (type.equals(DataType.EPITOPES)){
			EpitopeObjectParser epitopeObjectParser = new EpitopeObjectParser(path);
			try{
				epitopeObjectParser.parse();
			} catch (IOException e){
				e.printStackTrace();
			}
			return epitopeObjectParser.wrapParsedObjectsToPointDataSet();
		}
		else {
			return null;
		}
	}
}
