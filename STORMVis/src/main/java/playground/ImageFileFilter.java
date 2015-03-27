package playground;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ImageFileFilter extends FileFilter {

	@Override
	public String getDescription() {
		return "Images";
	}
	
	private final String[] okFileExtensions = 
			new String[] {"jpg", "png", "gif","bmp"};

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
