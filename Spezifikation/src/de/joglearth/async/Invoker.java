package de.joglearth.async;


public interface Invoker {

    /**
     * Enqueues a runnable and requests its execution in the future. On completion,
     * <code>null</code> is passed to the listener.
     * 
     * @param runnable The runnable. Must not be null
     * @param listener The listener to call when execution has completed. May be null
     */
    public void invokeLater(Runnable runnable, RunnableResultListener listener);

    /**
     * Enqueues a runnable and requests its execution in the future.
     * 
     * @param runnable The runnable. Must not be null.
     */
    public void invokeLater(Runnable runnable);


    /**
     * Enqueues a runnable and requests its execution in the future.
     * 
     * @param runnable The runnable. Must not be null
     * @param listener The listener to call when execution has completed. May be null
     */
    public void invokeLater(final RunnableWithResult runnable, RunnableResultListener listener);
    
    
    /**
     * Enqueues a runnable and requests its execution in the future.
     * 
     * @param runnable The runnable. Must not be null
     */
    public void invokeLater(RunnableWithResult runnable);
    

    /**
     * Calls the runnable right away if the called in the right context and enqueues it via
     * invokeLater() otherwise.
     * 
     * @param runnable The runnable. must not be null
     * @param listener The listener to call when execution has completed. May be null, and might be
     *        called before invokeSooner() returns
     */
    public void invokeSooner(Runnable runnable, RunnableResultListener listener);
    

    /**
     * Calls the runnable right away if called in the right context and enqueues it via
     * invokeLater() otherwise.
     * 
     * @param runnable The runnable. Must not be null
     */
    public void invokeSooner(RunnableWithResult runnable);


    /**
     * Calls the runnable right away if in the right Context and enqueues it via invokeLater()
     * otherwise.
     * 
     * @param runnable The runnable. Must not be null
     * @param listener The listener to call when execution has completed. May be null, and might be
     *        called before invokeSooner() returns
     */
    public void invokeSooner(RunnableWithResult runnable, RunnableResultListener listener);
    

    /**
     * Calls the runnable right away if in the right context and enqueues it via invokeLater()
     * otherwise.
     * 
     * @param runnable The runnable. must not be null
     */
    public void invokeSooner(Runnable runnable);
    
    
    /**
     * Calls the runnable right away if in the right context, enqueues it and waits for its 
     * completion otherwise.
     * 
     * @param runnable The runnable. Must not be null
     * @throws InterruptedException Waiting for the runnable was interrupted
     */ 
    public void invokeAndWait(Runnable runnable) throws InterruptedException;

    
    /**
     * Calls the runnable right away if in the right context, enqueues it and waits for its 
     * completion otherwise.
     * 
     * @param runnable The runnable. Must not be null
     * @return The result the runnable returns
     * @throws InterruptedException Waiting for the runnable was interrupted
     */ 
    public Object invokeAndWait(RunnableWithResult runnable) throws InterruptedException;
}
