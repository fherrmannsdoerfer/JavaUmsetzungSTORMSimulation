package inout;

import gui.TriangleLineFilter.Utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ImageFileFilter extends FileFilter {
	private final String[] okFileExtensions = 
			new String[] {"jpg", "png", "gif","bmp"};

	@Override
	public boolean accept(File f) {
	    if (f.isDirectory()) {
	        return true;
	    }

	    for (String extension : okFileExtensions)
		{
			if (f.getName().toLowerCase().endsWith(extension))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return ".jpg, .png, .gif,.bmp";
	}
	

	
	public boolean accep22t(File file)
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
