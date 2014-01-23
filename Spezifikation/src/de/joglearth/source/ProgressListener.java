package de.joglearth.source;

/**
 * Listener interface notified on {@link ProgressManager} events.
 */
public interface ProgressListener {

    /**
     * Is called when the global loading progress changes.
     * 
     * @param prog The progress, where 0.0 equals 0% and 1.0 equals 100%
     */
    void updateProgress(double prog);

    /**
     * Is called when {@link ProgressManager#abortPendingRequests()} is invoked. An implementation
     * should attempt to stop any pending asynchronous request.
     */
    void abortPendingRequests();
}
