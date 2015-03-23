package gui;

import java.io.IOException;
import java.util.List;

import parsing.LineObjectParser;
import parsing.TriangleObjectParser;
import gui.DataTypeDetector.DataType;

import org.javatuples.Pair;
import org.jzy3d.plot3d.primitives.Polygon;

public class ParserWrapper {
	
	public static Object parseFileOfType(String path, DataType type) {
		if(type.equals(DataType.NFF)) {
			TriangleObjectParser trParser = new TriangleObjectParser(null);
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
			Pair<List<Polygon>, List<float[][]>> pair = new Pair<List<Polygon>, List<float[][]>>(trParser.allTriangles, trParser.primitives);
			return pair;
		}
		else if (type.equals(DataType.WIMP)) {
			LineObjectParser lineParser = new LineObjectParser(path);
			try {
				lineParser.parse();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return lineParser.allObjects;
		}
		else {
			return null;
		}
	}
}
