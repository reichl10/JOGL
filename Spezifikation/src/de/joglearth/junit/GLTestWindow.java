package de.joglearth.junit;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;


/**
 * A simple OpenGL test window class containing a GLCanvas.
 */
public class GLTestWindow {
    
    private JFrame frame;
    private GLCanvas canvas;
    private GL2 gl;
    private boolean ready;
    
    /**
     * Constructor.
     * 
     * Creates the underlying frame and canvas, waiting until the display() method is first called
     * in the event queue.
     */
    public GLTestWindow() {
        frame = new JFrame("GL Test Window");
        frame.setSize(400, 300);
        
        ready = false;
        canvas = new GLCanvas(new GLCapabilities(GLProfile.getDefault()));
        
        canvas.addGLEventListener(new GLEventListener() {
            
            @Override
            public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
            }
            
            @Override
            public void init(GLAutoDrawable arg0) {
            }
            
            @Override
            public void dispose(GLAutoDrawable arg0) {
            }
            
            @Override
            public void display(GLAutoDrawable arg0) {
                ready = true;
            }
        });
        
        frame.add(canvas);        
        frame.setVisible(true);  
        
        while (!ready) {
            ClearableEventQueue.getInstance().clear();
        }
    }
    
    /**
     * Returns the canvas.
     * @return The canvas
     */
    public GLCanvas getGLCanvas() {
        return canvas;
    }
    
    /**
     * Returns the OpenGL context.
     * @return The context
     */
    public GL2 getGL() {
        return gl;
    }
    
    /**
     * Disposes the frame.
     */
    public void dispose() {
        frame.dispose();
    }
    
}
