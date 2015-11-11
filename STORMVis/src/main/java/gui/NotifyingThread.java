package gui;

import java.beans.PropertyChangeListener;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.SwingWorker;

public class NotifyingThread  extends SwingWorker<Void, Void>{
	Plot3D plot;
	public NotifyingThread(Plot3D plot){
		this.plot = plot;
	}
	public void setPlot(Plot3D plot){
		this.plot = plot;
	}
	  private  Set<ThreadCompleteListener> listeners
	                   = new CopyOnWriteArraySet<ThreadCompleteListener>();
	  public  void addListener(final ThreadCompleteListener listener) {
	    listeners.add(listener);
	  }
	  public  void removeListener(final ThreadCompleteListener listener) {
	    listeners.remove(listener);
	  }
	  private  void notifyListeners() {
	    for (ThreadCompleteListener listener : listeners) {
	      listener.notifyOfThreadComplete(this);
	    }
	  }
	  
	  @Override
	  public Void doInBackground() {
	    try {
	      plot.createChart();
	    } finally {
	      notifyListeners();
	      System.out.println("Notification sent");
	    }
		return null;
	  }
	  @Override
	  public void done(){
		  System.out.println("Rendering finished");
	  }
	}