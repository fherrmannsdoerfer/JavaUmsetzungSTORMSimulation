package gui;

import java.io.File;
import java.util.EventObject;

public class FileDragAndDropEvent extends EventObject{
	private File file;
	public FileDragAndDropEvent(Object arg0, File file) {
		super(arg0);
		this.file = file;
		// TODO Auto-generated constructor stub
	}
	public File getFile(){
		return this.file;
	}

}
