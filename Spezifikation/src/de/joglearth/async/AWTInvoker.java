package de.joglearth.async;

import java.awt.EventQueue;


public final class AWTInvoker extends AbstractInvoker {
    
    private static AWTInvoker instance = null;
    
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
