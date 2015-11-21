package gui;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.SwingWorker;

public class CreatePlot  extends SwingWorker<Void, Void>{
	Plot3D plot;
	public CreatePlot(Plot3D plot){
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

	  public  void notifyListeners() {
	    for (ThreadCompleteListener listener : listeners) {
	      listener.notifyOfThreadComplete(this);
	    }
	  }
	  
	 
	  @Override
	  public Void doInBackground() {
		  setProgress(0);
		  long start = System.nanoTime();
	      plot.createChart(this);
	      notifyListeners();
	      System.out.println("Time for visualization: "+ (System.nanoTime()-start)/1e9 +"s");
		  System.out.println("-------------------------------------");
		return null;
	  }
	  @Override
	  public void done(){
		  setProgress(100);
	  }
	  
	  public void publicSetProgress(int prog){
		  setProgress(prog);
	  }
}