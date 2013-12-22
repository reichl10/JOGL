package de.joglearth.async;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;


public final class AWTInvoker {

    public static void invoke(Runnable runnable) {
        if (EventQueue.isDispatchThread()) {
            runnable.run();
        } else {
            try {
                try {
                    EventQueue.invokeAndWait(runnable);
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException("Exception in AWTInvoker.invoke()", e);
            }
        }
    }
    
    private static class RunnableResultWrapper implements Runnable {

        public RunnableWithResult runnable;
        public Object result;
        
        public RunnableResultWrapper(RunnableWithResult r) {
            runnable = r;
        }

        @Override
        public void run() {
            result = runnable.run();
        }
    }
    
    private static class RunnableCallbackWrapper implements Runnable {
        
        private Object runnable;
        private RunnableResultListener callback;
        
        public RunnableCallbackWrapper(Object r, RunnableResultListener l) {
            runnable = r;
            callback = l;
        }
        
        @Override
        public void run() {
            if (runnable instanceof Runnable) {
                ((Runnable) runnable).run();
                callback.runnableCompleted(null);
            } else if (runnable instanceof RunnableWithResult) {
                callback.runnableCompleted(((RunnableWithResult) runnable).run());
            }
        }
    }
    

    public static Object invoke(RunnableWithResult runnable) throws Throwable {
        RunnableResultWrapper wrapper = new RunnableResultWrapper(runnable);
        invoke(wrapper);
        return wrapper.result;
    }

    public static void invokeLater(Runnable runnable) {
        EventQueue.invokeLater(runnable);
    }
    
    public static void invokeLater(Runnable runnable, RunnableResultListener l) {
        EventQueue.invokeLater(runnable);
    }
    
    public static void invokeLater(RunnableWithResult runnable) {
        EventQueue.invokeLater(new RunnableCallbackWrapper(runnable, null));
    }
    
    public static void invokeLater(RunnableWithResult runnable, RunnableResultListener l) {
        EventQueue.invokeLater(new RunnableCallbackWrapper(runnable, l));
    }
    
}
