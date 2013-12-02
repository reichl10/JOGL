package de.joglearth.source;

import java.util.LinkedList;
import java.util.List;


/**
 * Singleton class managing the global progress of asynchronous requests handled by
 * {@link RequestDistributor}s.
 */
public class ProgressManager {

    private static ProgressManager instance = null;
    private List<ProgressListener> listeners;

    /**
     * Number of unfinished requests
     */
    private int pending;

    /**
     * Total number of request added since the last pending == 0
     */
    private int maxPending;


    /**
     * Constructor. Should only called by getInstance()
     */
    private ProgressManager() {
        listeners = new LinkedList<ProgressListener>();
        pending = 0;
        maxPending = 0;
    }

    /**
     * Returns the instance of the singleton, creating it if it does not exist yet.
     * 
     * @return The instance
     */
    public static synchronized ProgressManager getInstance() {
        if (instance == null) {
            instance = new ProgressManager();
        }

        return instance;
    }

    /**
     * Adds a new {@link ProgressListener} which is notified whenever the overall progress changes
     * or the pending requests are to be aborted.
     * 
     * @param l The listener to add
     */
    public synchronized void addProgressListener(ProgressListener l) {
        listeners.add(l);
    }

    /**
     * Removes an existing {@link ProgressListener} from the set of listeners.
     * 
     * @param l The listener to remove
     */
    public synchronized void removeProgressListener(ProgressListener l) {
        listeners.remove(l);
    }

    /**
     * Removes all pending requests and notifies listeners that all remaining requests should be
     * aborted.
     */
    public synchronized void abortPendingRequests() {
        for (ProgressListener l : listeners) {
            l.abortPendingRequests();
        }

        pending = 0;
        maxPending = 0;
        updateProgress();
    }

    /**
     * Calculates the current progress, notifying all listeners of the change.
     */
    private void updateProgress() {
        double progress = 1.0;

        if (maxPending > 0) {
            progress = 1.0 - (double) pending / maxPending;
        }

        for (ProgressListener l : listeners) {
            l.updateProgress(progress);
        }
    }

    /**
     * Adds a pending request, notifying all listeners of the change.
     */
    public synchronized void requestArrived() {
        ++pending;
        ++maxPending;
        updateProgress();
    }

    /**
     * Marks a pending request as completed, notifying all listeners of the change.
     */
    public synchronized void requestCompleted() {
        if (pending > 0) {
            --pending;
        } else {
            maxPending = 0;
        }

        updateProgress();
    }
}
