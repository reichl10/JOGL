package de.joglearth.junit;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import de.joglearth.async.AWTInvoker;
import de.joglearth.opengl.Antialiasing;
import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.GLEasel;


/**
 * A simple OpenGL test window class containing a GLCanvas.
 */
public class GLTestWindow {

    private JFrame frame;
    private GLEasel eas;
    private GLProfile prof = GLProfile.get(GLProfile.GL2ES1);
    private GLContext context = new GLContext();
    private GLCanvas canvas;
    private Runnable nextDisplay = null;
    
    /**
     * Constructor.
     * 
     * Creates the underlying frame and canvas, waiting until the display() method is first called
     * in the event queue.
     */
    public GLTestWindow() {
        ClearableEventQueue.impose();
        try {
            AWTInvoker.getInstance().invokeLater(new Runnable() {

                @Override
                public void run() {
                    frame = new JFrame("GL Test Window");
                    frame.setSize(400, 300);

                    canvas = eas.newCanvas(prof, Antialiasing.MSAA_16X);
                    canvas.addGLEventListener(new GLEventListener() {

                        @Override
                        public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
                                int arg4) {}

                        @Override
                        public void init(GLAutoDrawable arg0) {}

                        @Override
                        public void dispose(GLAutoDrawable arg0) {}

                        @Override
                        public void display(GLAutoDrawable arg0) {
                            if (nextDisplay != null) {
                                nextDisplay.run();
                            }
                        }
                    });

                    frame.add(canvas);
                    frame.setVisible(true);
                }

            });
        } catch (Throwable e) {
            throw new RuntimeException("GLTestWindow initialization failed", e);
        }
        
        ClearableEventQueue.getInstance().clear();
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
    public GLProfile getGLProfile() {
        return prof;
    }

    /**
     * Returns the GLEasel.
     * 
     * @return The easel
     */
    public GLEasel getEasel() {
        return eas;
    }
    
    public GLContext getGLContext() {
        return context;
    }
    public void display(Runnable r) throws Throwable {
        nextDisplay = r;
        AWTInvoker.getInstance().invokeLater(new Runnable() {
            @Override
            public void run() {
                canvas.display();
            }
        });
    }

    /**
     * Disposes the frame.
     */
    public void dispose() {
        frame.dispose();
    }

}
