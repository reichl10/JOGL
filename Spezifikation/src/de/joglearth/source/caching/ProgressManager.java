package de.joglearth.source.caching;

import java.util.LinkedList;
import java.util.List;


/**
 * Singleton class managing the global progress of asynchronous requests handled by
 * RequestDistributors.
 */
public class ProgressManager {

    private static ProgressManager instance = null;

    private List<ProgressListener> listeners;

    private int pending; // Number of unfinished requests
    private int maxPending; // Total number of requests added since the last pending == 0

    // Constructor. Should only called by getInstance().
    private ProgressManager() {
        listeners = new LinkedList<ProgressListener>();
        pending = 0;
        maxPending = 0;
    }

    /**
     * Returns the instance of the singleton, creating it if it does not exist yet.
     * 
     * @return The instance.
     */
    public static synchronized ProgressManager getInstance() {
        if (instance == null)
            instance = new ProgressManager();
        return instance;
    }

    /**
     * Adds a new progress listener which is notified whenever the overall progress changes or the
     * pending requests are to be aborted.
     * 
     * @param l The listener to add.
     */
    public synchronized void addProgressListener(ProgressListener l) {
        listeners.add(l);
    }

    /**
     * Removes an existing ProgressListener from the set of listeners.
     * 
     * @param l The listener to remove.
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
        pending = maxPending = 0;
        updateProgress();
    }

    // Calculates the current progress, notifying all listeners of the change.
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
        assert pending > 0;
        --pending;
        if (pending == 0)
            maxPending = 0;
        updateProgress();
    }
}
