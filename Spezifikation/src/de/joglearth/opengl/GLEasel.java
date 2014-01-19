package de.joglearth.opengl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPanel;


/**
 * Holds a canvas - just like you'd expect. This component class allows re-instantiating a 
 * GLCanvas if the creation GLCapabilities, such as the level of anti-aliasing, change.
 * 
 */
public class GLEasel extends JPanel {

    /**
     * Makes the compiler happy
     */
    private static final long serialVersionUID = 3722896571146904415L;

    private GLCanvas canvas = null;
    private boolean initialized = false;


    /**
     * Constructor.
     */
    public GLEasel() {
        super(new BorderLayout());
        setBackground(Color.BLACK);
    }

    public boolean canReset() {
    	return canvas == null || initialized;
    }
    
    /**
     * Creates a new GL canvas, destroying a previously existing canvas.
     * @param caps The GLCapabilities describing the context to create.
     * @return The new canvas
     */
    public GLCanvas newCanvas(GLCapabilitiesImmutable caps) {
        if (caps == null) {
            throw new IllegalArgumentException();
        }

        if (canvas != null) {
        	if (!initialized) {
        		throw new IllegalStateException();
        	}
            canvas.destroy();
            remove(canvas);
        }

        initialized = false;
        canvas = new GLCanvas(caps);
        canvas.addGLEventListener(new GLEventListener() {

            @Override
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
                // Workaround: Allows shrinking of the canvas
                ((Component) drawable).setMinimumSize(new Dimension(0, 0));
            }

            @Override
            public void init(GLAutoDrawable drawable) {
            	initialized = true;
            }

            @Override
            public void dispose(GLAutoDrawable drawable) {}

            @Override
            public void display(GLAutoDrawable drawable) {}
        });

        add(canvas);
        doLayout();
        return canvas;
    }

    
    /**
     * Creates a new GL canvas, destroying a previously existing canvas.
     * @param prof The GLProfile used to create the context
     * @param aa The level of anti-aliasing to use in the new context
     * @return The new canvas
     */
    public GLCanvas newCanvas(GLProfile prof, Antialiasing aa) {
        
        int numSamples = 0;
        switch (aa) {
            case NONE: numSamples = 0; break;
            case MSAA_2X: numSamples = 2; break;
            case MSAA_4X: numSamples = 4; break;
            case MSAA_8X: numSamples = 8; break;
            case MSAA_16X: numSamples = 16; break;
        }
        
        GLCapabilities caps = new GLCapabilities(prof);
        if (numSamples > 0) {
            caps.setSampleBuffers(true);
            caps.setNumSamples(numSamples);
        }
        return newCanvas(caps);
    }

    
    /**
     * Returns the currently active GLCanvas.
     * @return The canvas, or null if none has been created yet
     */
    public GLCanvas getCanvas() {
        return canvas;
    }
}
