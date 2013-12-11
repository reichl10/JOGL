package de.joglearth.util;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;


public final class AWTInvoker {

    public static void invoke(Runnable runnable) throws Throwable {
        if (EventQueue.isDispatchThread()) {
            runnable.run();
        } else {
            try {
                EventQueue.invokeAndWait(runnable);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            } catch (InterruptedException e) {
                throw new RuntimeException("invokeWithResult failed");

            }
        }
    }

    public static Object invoke(final RunnableWithResult runnable) throws Throwable {
        class RunnableWrapper implements Runnable {

            public Object result;


            @Override
            public void run() {
                result = runnable.run();
            }

        }
        ;
        RunnableWrapper wrapper = new RunnableWrapper();
        invoke(wrapper);
        return wrapper.result;
    }
}
