package de.joglearth.async;


/**
 * Listener interface for notification when a task passed to an {@link Invoker} completes.
 */
public interface RunnableResultListener {
    /**
     * Called when an Runnable terminates successfully.
     * @param result The Runnable's result, if it is a {@link RunnableWithResult}, else null
     */
    public void runnableCompleted(Object result);
}
