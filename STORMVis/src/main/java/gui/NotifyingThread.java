package gui;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class NotifyingThread implements Runnable {
	  private final Set<ThreadCompleteListener> listeners
	                   = new CopyOnWriteArraySet<ThreadCompleteListener>();
	  public final void addListener(final ThreadCompleteListener listener) {
	    listeners.add(listener);
	  }
	  public final void removeListener(final ThreadCompleteListener listener) {
	    listeners.remove(listener);
	  }
	  private final void notifyListeners() {
	    for (ThreadCompleteListener listener : listeners) {
	      listener.notifyOfThreadComplete(this);
	    }
	  }
	  @Override
	  public final void run() {
	    try {
	      doRun();
	    } finally {
	      notifyListeners();
	      System.out.println("Notification sent");
	    }
	  }
	  public abstract void doRun();
	}