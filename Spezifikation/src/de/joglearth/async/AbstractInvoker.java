package de.joglearth.async;

import java.util.ArrayList;


public abstract class AbstractInvoker implements Invoker {

    protected abstract boolean canInvokeDirectly();
    
    protected abstract boolean tasksAvaliable();
    
    
    // Structure holding tasks provided by invokeLater()
    private class Invocation {

        public Runnable runnable;
        public RunnableResultListener listener;


        public Invocation(Runnable r, RunnableResultListener l) {
            runnable = r;
            listener = l;
        }
    }

    private ArrayList<Invocation> pendingInvocations = new ArrayList<>();
    
    
    @Override
    public void invokeLater(Runnable runnable, RunnableResultListener listener) {
        if (runnable == null) {
            throw new IllegalArgumentException();
        }

        synchronized (pendingInvocations) {
            pendingInvocations.add(new Invocation(runnable, listener));
        }
    }
    
    @Override
    public void invokeLater(Runnable runnable) {
        invokeLater(runnable, null);
    }


    // For using a RunnableWithResult as a Runnable
    private static class RunnableResultAdapter implements Runnable {

        public Object result;
        public RunnableWithResult runnable;


        public RunnableResultAdapter(RunnableWithResult runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            result = runnable.run();
        }

    };

    
    @Override
    public void invokeLater(RunnableWithResult runnable, RunnableResultListener listener) {
        RunnableResultAdapter wrapper = new RunnableResultAdapter(runnable);
        invokeLater(wrapper, listener);
    }

    @Override
    public void invokeLater(RunnableWithResult runnable) {
        invokeLater(runnable, null);
    }

    @Override
    public void invokeSooner(Runnable runnable, RunnableResultListener listener) {
        if (runnable == null) {
            throw new IllegalArgumentException();
        }

        if (canInvokeDirectly()) {
            runnable.run();
            if (listener != null) {
                listener.runnableCompleted(null);
            }
        } else {
            invokeLater(runnable, listener);
        }

    }

    @Override
    public void invokeSooner(RunnableWithResult runnable) {
        invokeSooner(runnable, null);
    }

    @Override
    public void invokeSooner(RunnableWithResult runnable, RunnableResultListener listener) {
        if (canInvokeDirectly()) {
            Object result = runnable.run();
            if (listener != null) {
                listener.runnableCompleted(result);
            }
        } else {
            invokeLater(runnable, listener);
        }
    }

    @Override
    public void invokeSooner(Runnable runnable) {
        invokeSooner(runnable, null);
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
    

    @Override
    public void invokeAndWait(Runnable runnable) throws InterruptedException {
        if (canInvokeDirectly()) {
            runnable.run();
        } else {
            final Object monitor = new Object();
            invokeLater(runnable, new RunnableResultListener() {
                @Override
                public void runnableCompleted(Object result) {
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            });
            synchronized (monitor) {
                monitor.wait();
            }
        }
    }

    @Override
    public Object invokeAndWait(RunnableWithResult runnable) throws InterruptedException {
        if (canInvokeDirectly()) {
            return runnable.run();
        } else {
            final RunnableResultAdapter adapter = new RunnableResultAdapter(runnable);
            invokeAndWait(adapter);
            return adapter.result;
        }
    }

    protected void invokePending() {// Invoke all pending invokeLater()s.
        ArrayList<Invocation> pendingCopy;
        synchronized (pendingInvocations) {
            pendingCopy = pendingInvocations;
            pendingInvocations = new ArrayList<>();
        }
        for (Invocation inv : pendingCopy) {
            inv.runnable.run();

            if (inv.listener != null) {
                if (inv.runnable instanceof RunnableResultAdapter) {
                    inv.listener.runnableCompleted(((RunnableResultAdapter) inv.runnable).result);
                } else {
                    inv.listener.runnableCompleted(null);
                }
            }
        }
    }
    
}
