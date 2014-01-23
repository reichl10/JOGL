package de.joglearth.async;


/**
 * An interface similar to {@link Runnable}, with the difference that the <code>run()</code> 
 * method returns a value.
 */
public interface RunnableWithResult {

    /**
     * Performs the Runnable's task, returning a result.
     * @return The result
     */
    Object run();
}
