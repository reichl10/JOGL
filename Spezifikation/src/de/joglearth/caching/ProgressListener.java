package de.joglearth.caching;

/**
 * Listener interface notified on @ref ProgressManager events.
 */
public interface ProgressListener {

    /**
     * Called when the global loading progress changes.
     * 
     * @param prog The progress, where 0.0 equals 0% and 1.0 equals 100%.
     */
    void updateProgress(double prog);

    /**
     * Called when @ref ProgressManager.abortPendingRequests() is invoked. An implementation should
     * attempt to stop any pending asynchronous request.
     */
    void abortPendingRequests();
}
