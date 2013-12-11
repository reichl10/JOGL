package de.joglearth.junit;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import de.joglearth.util.AWTInvoker;


/**
 * Extends the default AWT EventQueue class with the ability to dispatch all pending events.
 */
public final class ClearableEventQueue extends EventQueue {

    private static ClearableEventQueue instance;


    /**
     * Dispatches all pending events.
     */
    public void clear() {
        while (peekEvent() != null) {
            try {
                dispatchEvent(getNextEvent());
            } catch (InterruptedException e) {}
        }
    }

    private ClearableEventQueue() {}

    /**
     * Instantiates the singleton if not existing, setting it as the system event queue.
     */
    public static void impose() {
        // TODO Auto-generated method stub
        if (instance == null) {
            instance = new ClearableEventQueue();
            AWTInvoker.invoke(new Runnable() {

                @Override
                public void run() {
                    Toolkit.getDefaultToolkit().getSystemEventQueue().push(instance);
                }
            });
        }
    }

    /**
     * Returns the singleton's instance, {@link impose}ing it if not existing yet.
     * 
     * @return The instance
     */
    public static ClearableEventQueue getInstance() {
        impose();
        return instance;
    }

}
