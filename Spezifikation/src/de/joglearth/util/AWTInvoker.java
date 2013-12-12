package de.joglearth.util;

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
