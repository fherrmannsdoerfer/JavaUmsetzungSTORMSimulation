package inout;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ProjectFileFilter extends FileFilter {

	@Override
	public String getDescription() {
		return "Storm projects";
	}
	
	private final String[] okFileExtensions = 
			new String[] {"storm"};

	public boolean accept(File file)
	{
		for (String extension : okFileExtensions)
		{
			if (file.getName().toLowerCase().endsWith(extension))
			{
				return true;
			}
		}
		return false;
	}

}
