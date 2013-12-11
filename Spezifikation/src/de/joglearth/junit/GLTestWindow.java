package de.joglearth.junit;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import com.jogamp.opengl.util.Animator;

import de.joglearth.rendering.GLError;
import de.joglearth.util.AWTInvoker;


/**
 * A simple OpenGL test window class containing a GLCanvas.
 */
public class GLTestWindow {

    private JFrame frame;
    private GLCanvas canvas;
    private Animator anim;
    private boolean quit;
    private boolean insideFrame;
    private Object frameMonitor = new Object();


    private synchronized boolean isInsideFrame() {
        return insideFrame;
    }

    /**
     * Constructor.
     * 
     * Creates the underlying frame and canvas, waiting until the display() method is first called
     * in the event queue.
     */
    public GLTestWindow() {
        ClearableEventQueue.impose();
        insideFrame = false;
        final GLTestWindow that = this;
        AWTInvoker.invoke(new Runnable() {

            @Override
            public void run() {
                frame = new JFrame("GL Test Window");
                frame.setSize(400, 300);

                quit = false;
                canvas = new GLCanvas(new GLCapabilities(GLProfile.getDefault()));

                canvas.addGLEventListener(new GLEventListener() {

                    @Override
                    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {}

                    @Override
                    public void init(GLAutoDrawable arg0) {
                        anim = new Animator(arg0);
                    }

                    @Override
                    public void dispose(GLAutoDrawable arg0) {}

                    @Override
                    public void display(GLAutoDrawable arg0) {
                        synchronized (frameMonitor) {
                            frameMonitor.notify();
                        }
                        synchronized (that) {
                            try {
                                that.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (!quit) {
                                arg0.display();
                            }
                        }
                    }
                });

                frame.add(canvas);
                frame.setVisible(true);
            }

        });
    }

    /**
     * Returns the canvas.
     * 
     * @return The canvas
     */
    public GLCanvas getGLCanvas() {
        return canvas;
    }

    /**
     * Returns the OpenGL context.
     * 
     * @return The context
     */
    public GL2 getGL() {
        return canvas.getGL().getGL2();
    }

    public void beginFrame() {
        synchronized (frameMonitor) {
            try {
                frameMonitor.wait();
            } catch (InterruptedException e) {}
        }
    }

    public synchronized void endFrame() {
        notify();
    }

    /**
     * Disposes the frame.
     */
    public void dispose() {
        beginFrame();
        synchronized (this) {
            quit = true;
            notify();
        }
        frame.dispose();
    }

}
