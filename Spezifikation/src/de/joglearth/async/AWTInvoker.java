package de.joglearth.async;

import java.awt.EventQueue;


/**
 * Implements the {@link Invoker} interface for the AWTEventQueue
 */
public final class AWTInvoker extends AbstractInvoker {

    private static AWTInvoker instance = null;


    /**
     * Returns the instance of the class or creates it, if it does not exist yet.
     * 
     * @return The instance of {@link AWTInvoker}
     */
    public static AWTInvoker getInstance() {
        if (instance == null) {
            instance = new AWTInvoker();
        }
        return instance;
    }

    private AWTInvoker() {}

    @Override
    public boolean canInvokeDirectly() {
        return EventQueue.isDispatchThread();
    }

    @Override
    public void invokeLater(Runnable runnable) {
        EventQueue.invokeLater(runnable);
    }

    @Override
    public void invokeLater(RunnableWithResult runnable) {
        super.invokeLater(runnable);
    }

    @Override
    public void invokeLater(Runnable runnable, RunnableResultListener listener) {
        super.invokeLater(runnable, listener);
    }

    @Override
    public void invokeLater(RunnableWithResult runnable, RunnableResultListener listener) {
        super.invokeLater(runnable, listener);
    }
}
