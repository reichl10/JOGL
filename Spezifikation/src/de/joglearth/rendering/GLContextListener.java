package de.joglearth.rendering;


/**
 * Listener interface for @link{GLContext} events.
 */
public interface GLContextListener {
    /**
     * Called at the very beginning of every rendered frame, before any other listener function is
     * invoked.
     * @param context The calling context
     */
    void beginFrame(GLContext context);
    
    /**
     * Called at the very end of every rendered frame, after all other listener functions have been
     * invoked.
     * @param context The calling context
     */
    void endFrame(GLContext context);
    
    /**
     * Called once when the context is initialized with a drawable, as caused by 
     * GLEventListener.init(). Followed by a call to display().
     * @param context The calling context
     */
    void initialize(GLContext context);
    
    /**
     * Called when the drawable's dimensions change. Caused by GLEventListener.reshape().
     * @param context The calling context
     * @param width The new width, in pixels
     * @param height The new height, in pixels
     */
    void reshape(GLContext context, int width, int height);
    
    /**
     * Called whenever frame should be rendered. Caused by GLEventListener.display().
     * @param context The calling context
     */
    void display(GLContext context);
    
    /**
     * Called when the drawable is disposed. Caused by GLEventListener.dispose().
     * @param context
     */
    void dispose(GLContext context);
}
