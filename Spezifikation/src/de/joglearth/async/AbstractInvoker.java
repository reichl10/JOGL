package de.joglearth.async;


public abstract class AbstractInvoker implements Invoker {

    
    // Wraps a Runnable(-WithResult) and a Listener inside a Runnable, calling the callback on 
    // completion.
    private static class RunnableCallbackWrapper implements Runnable {
        
        private Object runnable;
        private RunnableResultListener callback;
        
        // r must be either Runnable or RunnableWithResult, else run() is a no-op
        public RunnableCallbackWrapper(Object r, RunnableResultListener l) {
            runnable = r;
            callback = l;
        }
        
        @Override
        public void run() {
            Object result = null;
            if (runnable instanceof Runnable) {
                ((Runnable) runnable).run();
            } else if (runnable instanceof RunnableWithResult) {
                result = ((RunnableWithResult) runnable).run();
            }
            if (callback != null) {
                callback.runnableCompleted(result);
            }
        }
    }
    

    @Override
    public void invokeLater(Runnable runnable, RunnableResultListener listener) {
        if (runnable == null) {
            throw new IllegalArgumentException();
        }

        invokeLater(new RunnableCallbackWrapper(runnable, listener));
    }

    // Runnable calling a RunnableWithResult and storing its result
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
        if (runnable == null) {
            throw new IllegalArgumentException();
        }
        
        invokeLater(new RunnableCallbackWrapper(runnable, listener));
    }

    @Override
    public void invokeLater(RunnableWithResult runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException();
        }
        
        invokeLater(new RunnableResultAdapter(runnable));
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
        if (runnable == null) {
            throw new IllegalArgumentException();
        }

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
    
    @Override
    public void invokeAndWait(Runnable runnable) throws InterruptedException {
        if (runnable == null) {
            throw new IllegalArgumentException();
        }

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
        if (runnable == null) {
            throw new IllegalArgumentException();
        }

        if (canInvokeDirectly()) {
            return runnable.run();
        } else {
            final RunnableResultAdapter adapter = new RunnableResultAdapter(runnable);
            invokeAndWait(adapter);
            return adapter.result;
        }
    }
    
}
