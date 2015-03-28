package inout;

import gui.DataTypeDetector.DataType;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import model.DataSet;
import model.LineDataSet;
import model.LineDataSetSerializable;
import model.ParameterSet;
import model.Project;

public class FileManager {
	
	public static void writeProjectToFile(Project p, String path) {
		for(DataSet set : p.dataSets) {
			if(set.dataType == DataType.LINES) {
				LineDataSetSerializable serial = new LineDataSetSerializable(set.parameterSet, (LineDataSet) set);
				p.dataSets.set(p.dataSets.indexOf(set), serial);
			}
		}
		
		try (
				OutputStream file = new FileOutputStream(path);
				OutputStream buffer = new BufferedOutputStream(file);
				ObjectOutput output = new ObjectOutputStream(buffer);
				)
				{
			output.writeObject(p);
				}  
		catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static Project openProjectFromFile(String path) {
		Project p = null;
		try(
				InputStream file = new FileInputStream(path);
				InputStream buffer = new BufferedInputStream(file);
				ObjectInput input = new ObjectInputStream (buffer);
		)
		{
			p = (Project) input.readObject();
			
		}
		catch(ClassNotFoundException ex){
		}
		catch(IOException ex){
		}
		
		for(DataSet set : p.dataSets) {
			if(set.dataType == DataType.LINES) {
				LineDataSet serial = new LineDataSet(set.parameterSet, (LineDataSetSerializable) set);
				p.dataSets.set(p.dataSets.indexOf(set), serial);
			}
		}
		
		return p;
	}
} 
