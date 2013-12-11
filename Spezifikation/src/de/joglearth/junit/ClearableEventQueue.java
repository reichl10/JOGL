package de.joglearth.junit;

import java.awt.EventQueue;
import java.awt.Toolkit;


/**
 * Extends the default AWT EventQueue class with the ability to dispatch all pending events.
 */
public final class ClearableEventQueue extends EventQueue {

    private static ClearableEventQueue instance;
    private static Object monitor = new Object();

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
        synchronized (monitor) {
            if (instance == null) {
                instance = new ClearableEventQueue();
                Toolkit.getDefaultToolkit().getSystemEventQueue().push(instance);
            }
        }
    }

    /**
     * Returns the singleton's instance, {@link impose}ing it if not existing yet.
     * @return The instance
     */
    public static ClearableEventQueue getInstance() {
        impose();
        return instance;
    }
}
