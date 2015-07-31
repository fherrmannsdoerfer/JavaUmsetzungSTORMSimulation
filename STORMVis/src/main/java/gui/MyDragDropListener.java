package gui;



import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.EventListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;


class MyDragDropListener implements DropTargetListener {
	private EventListenerList listenerList = new EventListenerList();
    @Override
    public void drop(DropTargetDropEvent event) {

        // Accept copy drops
        event.acceptDrop(DnDConstants.ACTION_COPY);

        // Get the transfer which can provide the dropped item data
        Transferable transferable = event.getTransferable();

        // Get the data formats of the dropped item
        DataFlavor[] flavors = transferable.getTransferDataFlavors();

        // Loop through the flavors
        for (DataFlavor flavor : flavors) {

            try {

                // If the drop items are files
                if (flavor.isFlavorJavaFileListType()) {

                    // Get all of the dropped files
                    List files = (List) transferable.getTransferData(flavor);

                    // Loop them through
                    for (int i = 0; i<files.size(); i++) {
                    	File file = (File) files.get(i);
                        // Print out the file path
                        //System.out.println("File path is '" + file.getPath() + "'.");
                    	fireFileDragAndDropEvent(new FileDragAndDropEvent(this,file));
                    }

                }

            } catch (Exception e) {

                // Print out the error stack
                e.printStackTrace();

            }
        }

        // Inform that the drop is complete
        event.dropComplete(true);

    }

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}
	public void MyDragAndDropEventOccured(FileDragAndDropEvent event) {
	}

	private void fireFileDragAndDropEvent(FileDragAndDropEvent event){
		Object[] listeners = listenerList.getListenerList();
		for(int i = 0; i< listeners.length; i+= 2){
			if(listeners[i] == FileDragAndDropListener.class) {
				((FileDragAndDropListener)listeners[i+1]).FileDragAndDropEventOccured(event);
			}
		}
		
	}
	public void addFileDragAndDropListener(
			FileDragAndDropListener fileDragAndDropListener) {
		listenerList.add(FileDragAndDropListener.class, fileDragAndDropListener);
		
	}
	public void removeFileDragAndDropListener(
			FileDragAndDropListener fileDragAndDropListener) {
		listenerList.remove(FileDragAndDropListener.class, fileDragAndDropListener);
		
	}
    
}