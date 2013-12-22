package de.joglearth.opengl;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import static javax.media.opengl.glu.GLU.*;

import javax.media.opengl.glu.GLUquadric;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import de.joglearth.async.AWTInvoker;
import de.joglearth.async.AbstractInvoker;
import de.joglearth.geometry.Matrix4;
import de.joglearth.rendering.Mesh;
import static javax.media.opengl.GL2.*;


/**
 * Encapsulates OpenGL calls and callbacks.
 */
public final class GLContext extends AbstractInvoker implements GLEventListener {

    private GL2 gl = null;
    private GLU glu = null;
    private GLUquadric quadric = null;

    // The thread accessing the GL object
    private Thread glThread = null;

    // The source drawable (e.g. a GLCanvas)
    private GLAutoDrawable drawable = null;

    // All registered GLContextListeners
    private ArrayList<GLContextListener> listeners = new ArrayList<>();

    // Whether the execution is currently inside a GLEventListener callback
    private boolean insideCallback = false;

    // Whether a frame is currently drawn
    private boolean insideFrame = false;

    private ArrayList<Runnable> pendingInvocations = new ArrayList<>();

    // Whether postRedisplay() has been called and no new frame has begun since
    private volatile boolean redisplayPending;

    // Whether a rendering loop is currently active
    private volatile boolean renderingLoopActive;


    // Throws if init() has not been called yet
    private void assertIsInitialized() {
        if (drawable == null) {
            throw new IllegalStateException("GLContext has not yet been initialized");
        }
    }

    // Throws if init() has not been called, or has been called with a different drawable
    private void assertIsInitialized(GLAutoDrawable caller) {
        assertIsInitialized();
        if (caller != drawable) {
            throw new IllegalStateException(
                    "GLContext has not been initialized with the given GLAutoDrawable");
        }
    }

    // Throws if not inside a callback
    private void assertIsInsideCallback() {
        if (!isInsideCallback()) {
            throw new IllegalStateException(
                    "GL operations must not be performed outside a GL callback");
        }
    }

    // Throws if the provided array contains at least one invalid OpenGL id
    private void assertValidIDs(int[] ids) {
        for (int id : ids) {
            if (id <= 0) {
                throw new GLError("Unable to allocate GL object (returned id 0)");
            }
        }
    }

    /**
     * Returns whether the context has yet been initialized by a GLEventListener.init() callback.
     * 
     * @return Whether the context has been initialized.
     */
    public boolean isInitialized() {
        return drawable != null;
    }

    /**
     * Returns whether the current thread is inside a GLEventListener callback and GL operations may
     * be performed.
     * 
     * @return Whether the thread is inside a GL callback.
     */
    public boolean isInsideCallback() {
        return drawable != null && Thread.currentThread().equals(glThread) && insideCallback;
    }

    /**
     * Returns the drawable's size.
     * 
     * @return The size, in pixels.
     */
    public Dimension getSize() {
        assertIsInitialized();
        return new Dimension(drawable.getWidth(), drawable.getHeight());
    }

  
    /**
     * Loads a mesh into OpenGL memory, returning a vertex buffer object.
     * 
     * @param mesh The mesh to load. Must be a valid mesh.
     * @return The vertex buffer object.
     */
    public VertexBuffer loadMesh(Mesh mesh) {
        if (mesh == null || mesh.indices == null || mesh.indexCount < 0 || mesh.vertices == null) {
            throw new IllegalArgumentException();
        }

        assertIsInitialized();
        assertIsInsideCallback();

        // Allocate vertex and index buffer
        int[] buffers = new int[2];
        gl.glGenBuffers(2, buffers, 0);
        GLError.throwIfActive(gl);
        assertValidIDs(buffers);

        VertexBuffer vbo = new VertexBuffer(mesh.primitiveType, mesh.indexCount, buffers[0],
                buffers[1]);

        // Bind vertex buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo.vertices);
        GLError.throwIfActive(gl);

        // Write vertex data
        gl.glBufferData(GL_ARRAY_BUFFER, 4 * mesh.vertices.length, FloatBuffer.wrap(mesh.vertices),
                GL_STATIC_DRAW);
        GLError.throwIfActive(gl);

        // Bind index buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo.indices);
        GLError.throwIfActive(gl);

        // Write index array
        gl.glBufferData(GL_ARRAY_BUFFER, 4 * mesh.indices.length, IntBuffer.wrap(mesh.indices),
                GL_STATIC_DRAW);
        GLError.throwIfActive(gl);

        return vbo;
    }

    /**
     * Removes an existing vertex buffer object from graphics memory.
     * 
     * @param vbo The vertex buffer object. Must not be null and must hold valid buffer IDs.
     */
    public void deleteVertexBuffer(VertexBuffer vbo) {
        if (vbo == null || vbo.indices <= 0 || vbo.vertices <= 0) {
            throw new IllegalArgumentException();
        }

        assertIsInitialized();
        assertIsInsideCallback();

        gl.glDeleteBuffers(2, new int[] { vbo.vertices, vbo.indices }, 0);
        GLError.throwIfActive(gl);
    }
    
    
    public Texture loadTexture(TextureData data) {
        assertIsInitialized();
        assertIsInsideCallback();
        if (data == null) {
            throw new IllegalArgumentException();
        }
        
        return new Texture(gl, data);
    }
    
    public Texture loadTexture(InputStream stream, String suffix, boolean mipmap) throws IOException {
        return loadTexture(TextureIO.newTextureData(gl.getGLProfile(), stream, mipmap, suffix));
    }

    public Texture loadTexture(byte[] image, int width, int height, int format, int internalFormat,
            boolean mipmap) {
        return loadTexture(new TextureData(gl.getGLProfile(), internalFormat, width, height, 0,
                format, GL_UNSIGNED_BYTE, mipmap, false, false, ByteBuffer.wrap(image), null));
    }

    public void deleteTexture(Texture tex) {
        if (tex == null) {
            throw new IllegalArgumentException();
        }
        
        tex.destroy(gl);
    }
    

    /**
     * Loads a matrix into a given matrix slot (also called "matrix stack").
     * 
     * @param slot The slot, e.g. GL_MODELVIEW or GL_PROJECTION.
     * @param matrix The matrix to load. Must not be null.
     */
    public void loadMatrix(int slot, Matrix4 matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException();
        }

        assertIsInitialized();
        assertIsInsideCallback();

        gl.glMatrixMode(slot);
        GLError.throwIfActive(gl);

        gl.glLoadMatrixd(matrix.doubles(), 0);
        GLError.throwIfActive(gl);
    }

    /**
     * Draws a vertex buffer object with a given texture.
     * 
     * @param texture The texture to use. May be null
     */
    public void drawVertexBuffer(VertexBuffer vbo, Texture texture) {
        if (vbo == null || vbo.indices <= 0 || vbo.vertices <= 0) {
            throw new IllegalArgumentException();
        }

        assertIsInitialized();
        assertIsInsideCallback();

        if (texture != null) {
            texture.bind(gl);
            GLError.throwIfActive(gl);
        }
        
        // Bind vertex buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo.vertices);
        GLError.throwIfActive(gl);

        // Set vertex / normal / texcoord pointers
        gl.glEnableClientState(GL_VERTEX_ARRAY);
        GLError.throwIfActive(gl);

        gl.glVertexPointer(3, GL_FLOAT, 8 * 4, 5 * 4);
        GLError.throwIfActive(gl);

        gl.glEnableClientState(GL_NORMAL_ARRAY);
        GLError.throwIfActive(gl);

        gl.glNormalPointer(GL_FLOAT, 8 * 4, 2 * 4);
        GLError.throwIfActive(gl);

        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        GLError.throwIfActive(gl);

        gl.glTexCoordPointer(2, GL_FLOAT, 8 * 4, 0);
        GLError.throwIfActive(gl);

        // Bind index buffer
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo.indices);
        GLError.throwIfActive(gl);

        // Draw
        gl.glDrawElements(vbo.primitiveType, vbo.indexCount, GL_UNSIGNED_INT, 0);
        GLError.throwIfActive(gl);

        // Disable pointers
        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        GLError.throwIfActive(gl);

        gl.glDisableClientState(GL_NORMAL_ARRAY);
        GLError.throwIfActive(gl);

        gl.glDisableClientState(GL_VERTEX_ARRAY);
        GLError.throwIfActive(gl);

        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
        GLError.throwIfActive(gl);

        gl.glBindTexture(GL_TEXTURE_2D, 0);
        GLError.throwIfActive(gl);
    }

    
    /**
     * Draws a sphere using gluSphere().
     * @param radius The radius of the sphere. Must be greater than zero
     * @param slices The number of vertices on the equator. Must be greater or equal 3
     * @param stacks The number of vertices from north to south pole. Must be greater or equal 3
     * @param inside Whether to make surfaces point to the inside of the sphere rather than to
     *               the outside
     * @param texture The texture ID to use. May be 0
     */
    public void drawSphere(double radius, int slices, int stacks, boolean inside, Texture texture) {
        assertIsInitialized();
        assertIsInsideCallback();
        if (radius <= 0 || slices < 3 || stacks < 3) {
            throw new IllegalArgumentException();
        }
        
        if (texture != null) {
            texture.bind(gl);
            GLError.throwIfActive(gl);
        }

        glu.gluQuadricOrientation(quadric, inside ? GLU_INSIDE : GLU_OUTSIDE);
        GLError.throwIfActive(gl);
        
        glu.gluQuadricTexture(quadric, true);
        GLError.throwIfActive(gl);
        
        glu.gluSphere(quadric, radius, slices, stacks);
        GLError.throwIfActive(gl);
        
        gl.glBindTexture(GL_TEXTURE_2D, 0);
        GLError.throwIfActive(gl);
    }
    
    
    /**
     * Clears the OpenGL color and depth buffer.
     */
    public void clear() {
        assertIsInitialized();
        assertIsInsideCallback();

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    
    /**
     * Determines whether an OpenGL feature is active (glIsEanbled).
     * @param The feature to check, e.g. GL_DEPTH_TEST
     * @return Whether the feature is enabled
     */
    public boolean isFeatureEnabled(int feature) {
        assertIsInitialized();
        assertIsInsideCallback();
        
        boolean enabled = gl.glIsEnabled(feature);
        GLError.throwIfActive(gl);
        return enabled;
    }

    /**
     * Enables (glEnable) or disables (glDisable) a feature.
     * 
     * @param feature The OpenGL feature, e.g. GL_DEPTH_TEST
     * @param enabled Whether to enable or disable the feature
     */
    public void setFeatureEnabled(int feature, boolean enabled) {
        assertIsInitialized();
        assertIsInsideCallback();

        if (enabled) {
            gl.glEnable(feature);
        } else {
            gl.glDisable(feature);
        }
        GLError.throwIfActive(gl);
    }

    /**
     * Sets the front- and backface polygon mode.
     * 
     * @param mode The mode, e.g. GL_FILL or GL_LINE
     */
    public void setPolygonMode(int mode) {
        assertIsInitialized();
        assertIsInsideCallback();

        gl.glPolygonMode(GL_FRONT_AND_BACK, mode);
        GLError.throwIfActive(gl);
    }

    // Does things necessary at the beginning of every GL callback, and calls
    // GLContextListener.beginFrame() if necessary
    private void beginDisplay() {
        insideCallback = true;

        // Invoke all pending invokeLater()s.
        ArrayList<Runnable> pendingCopy;
        synchronized (pendingInvocations) {
            pendingCopy = pendingInvocations;
            pendingInvocations = new ArrayList<>();
        }
        for (Runnable runnable : pendingCopy) {
            runnable.run();
        }

        // Some callbacks, like initialize(), will not end the frame they begun
        if (!insideFrame) {
            insideFrame = true;
            for (GLContextListener l : listeners) {
                l.beginFrame(this);
            }
        }
    }

    // Does things necessary at the end of every GL callback, and calls
    // GLContextListener.endFrame().
    private void endDisplay(boolean endFrame) {
        if (endFrame && insideFrame) {
            insideFrame = false;
            for (GLContextListener l : listeners) {
                l.endFrame(this);
            }
        }
        insideCallback = false;
    }

    @Override
    public synchronized void display(GLAutoDrawable caller) {
        assertIsInitialized(caller);

        beginDisplay();
        for (GLContextListener l : listeners) {
            l.display(this);
        }
        endDisplay(true);
    }

    @Override
    public synchronized void dispose(GLAutoDrawable caller) {
        assertIsInitialized(caller);

        beginDisplay();
        for (GLContextListener l : listeners) {
            l.dispose(this);
        }
        endDisplay(true);

        drawable = null;
        gl = null;
        glu = null;
        quadric = null;
        glThread = null;
    }

    @Override
    public synchronized void init(GLAutoDrawable caller) {
        if (drawable != null) {
            throw new IllegalStateException();
        }

        drawable = caller;
        gl = drawable.getGL().getGL2();
        glu = new GLU();
        quadric = glu.gluNewQuadric();
        glThread = Thread.currentThread();

        beginDisplay();
        for (GLContextListener l : listeners) {
            l.initialize(this);
        }
        // No endFrame(), display() will be called afterwards
        endDisplay(false);
    }

    @Override
    public synchronized void reshape(GLAutoDrawable caller, int x, int y, int width, int height) {
        assertIsInitialized(caller);

        beginDisplay();
        for (GLContextListener l : listeners) {
            l.reshape(this, width, height);
        }
        // No endFrame(), display() will be called afterwards
        endDisplay(false);
    }

    /**
     * Adds a @link{GLContextListener}.
     * 
     * @param l The listener.
     */
    public synchronized void addGLContextListener(GLContextListener l) {
        listeners.add(l);
    }

    /**
     * Removes a @link{GLContextListener}.
     * 
     * @param l The listener.
     */
    public synchronized void removeGLContextListener(GLContextListener l) {
        while (listeners.remove(l));
    }

    /**
     * Notifies the context that at least one more frame should be drawn. The drawing is done
     * asynchronously and the function returns immediately.
     */
    public void postRedisplay() {
        // When the initialization occurs, a frame will be drawn anyway.
        if (isInitialized()) {
            synchronized (this) {
                redisplayPending = true;
                if (renderingLoopActive) {
                    return;
                }
                renderingLoopActive = true;
            }

            // Call invokeLater() while there are pending frames. Don't use a loop so that other
            // AWT events can be processed as well.
            AWTInvoker.getInstance().invokeLater(new Runnable() {

                @Override
                public void run() {
                    redisplayPending = false;
                    drawable.display();
                    
                    // might be re-set in another thread
                    if (redisplayPending) {
                        AWTInvoker.getInstance().invokeLater(this);
                    } else {
                        renderingLoopActive = false;
                    }
                }
            });
        }
    }

    
    @Override
    public void invokeLater(Runnable runnable) {
        synchronized (pendingInvocations) {
            pendingInvocations.add(runnable);
        }
        postRedisplay();
    }

    @Override
    protected boolean canInvokeDirectly() {
        return isInsideCallback();
    }

}