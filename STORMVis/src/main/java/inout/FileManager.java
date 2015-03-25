package inout;

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
import model.ParameterSet;
import model.Project;

public class FileManager {
	public static void main(String... aArguments) {

		List<Project> projects = new ArrayList<Project>();
		
		Project p = new Project();
		p.setProjectName("ouput project");
		DataSet s = new DataSet(new ParameterSet());
		s.setName("output set");
		p.addDataSet(s);
		projects.add(p);
		//serialize the List
		try (
			OutputStream file = new FileOutputStream("test.storm");
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
		)
		{
			output.writeObject(projects);
		}  
		catch(IOException ex){

		}

		try(
				InputStream file = new FileInputStream("test.storm");
				InputStream buffer = new BufferedInputStream(file);
				ObjectInput input = new ObjectInputStream (buffer);
		)
		{
			List<Project> recoveredQuarks = (List<Project>) input.readObject();
			for(Project quark: recoveredQuarks){
				System.out.println("Recovered Project: " + quark.getProjectName());
				System.out.println("data: " + quark.getDataSets().get(0).getName());
			}
		}
		catch(ClassNotFoundException ex){
		}
		catch(IOException ex){
		}
	}
} 
