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

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import de.joglearth.async.AWTInvoker;
import de.joglearth.async.AbstractInvoker;
import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.Vector3;
import de.joglearth.rendering.Mesh;
import static javax.media.opengl.GL2.*;
import static java.lang.Double.*;
import static java.lang.Math.*;

/**
 * Encapsulates OpenGL calls and callbacks.
 */
public final class GLContext extends AbstractInvoker implements GLEventListener {

    private int lightCount = 0;
    
    // The internal contexts
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
    private volatile boolean redisplayActive;

    private FPSAnimator animator = null;
    
    private boolean anisotropySupported;
    private int maxAnisotropy;    


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
     * @return The size, in pixels
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
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
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     * @throws GLError An internal OpenGL error has occurred
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
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo.getVertices());
        GLError.throwIfActive(gl);

        // Write vertex data
        gl.glBufferData(GL_ARRAY_BUFFER, 4 * mesh.vertices.length, FloatBuffer.wrap(mesh.vertices),
                GL_STATIC_DRAW);
        GLError.throwIfActive(gl);

        // Bind index buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo.getIndices());
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
     * @param vbo The vertex buffer object. Must not be null and must hold valid buffer IDs
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     * @throws GLError An internal OpenGL error has occurred
     */
    public void deleteVertexBuffer(VertexBuffer vbo) {
        if (vbo == null) {
            throw new IllegalArgumentException();
        }

        assertIsInitialized();
        assertIsInsideCallback();

        gl.glDeleteBuffers(2, new int[] { vbo.getVertices(), vbo.getIndices() }, 0);
        GLError.throwIfActive(gl);
    }

    /**
     * Loads a texture via the JOGL Texture API.
     * 
     * @param data The texture data to load
     * @param filter The filter used to interpolate the texture
     * @return The texture
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     */
    public Texture loadTexture(TextureData data, TextureFilter filter) {
        assertIsInitialized();
        assertIsInsideCallback();
        if (data == null) {
            throw new IllegalArgumentException();
        }
        
        // Mipmaps only make sense with trilinear or anisotropic filtering
        // TODO This is evil as it modifies the TextureData object.
        data.setMipmap(filter != TextureFilter.NEAREST && filter != TextureFilter.BILINEAR);
        
        Texture tex = new Texture(gl, data);
        
        // Set the GL interpolation filter.
        int minFilter, magFilter;
        switch (filter) {
            case NEAREST:
                minFilter = GL_NEAREST;
                magFilter = GL_NEAREST;
                break;
                
            case BILINEAR:
                minFilter = GL_LINEAR;
                magFilter = GL_LINEAR;
                break;
             
            default:
                // Anisotropic filtering is a variation of trilinear
                minFilter = GL_LINEAR_MIPMAP_LINEAR;
                magFilter = GL_LINEAR;
        }

        tex.setTexParameteri(gl, GL_TEXTURE_MIN_FILTER, minFilter);
        GLError.throwIfActive(gl);
        
        tex.setTexParameteri(gl, GL_TEXTURE_MAG_FILTER, magFilter);
        GLError.throwIfActive(gl);
        
        if (anisotropySupported) {
            int anisotropy;
            switch (filter) {
                case ANISOTROPIC_16X: anisotropy = 16; break;
                case ANISOTROPIC_8X: anisotropy = 8; break;
                case ANISOTROPIC_4X: anisotropy = 4; break;
                case ANISOTROPIC_2X: anisotropy = 2; break;
                default: anisotropy = 0; break;
            }
            anisotropy = min(anisotropy, maxAnisotropy);
            
            if (anisotropy != 0) {
                tex.setTexParameteri(gl, GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropy);
                GLError.throwIfActive(gl);
            }
        }

        return tex;
    }

    /**
     * Loads a texture from an input stream via the JOGL Texture API.
     * 
     * @param stream The input stream.
     * @param suffix The file suffix, used to determine the content type.
     * @param mipmap Whether to create and use mipmaps.
     * @return The texture
     * @throws IOException An error occurred while loading the image data
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     */
    public Texture loadTexture(InputStream stream, String suffix, TextureFilter filter)
            throws IOException {
        if (stream == null || suffix == null) {
            throw new IllegalArgumentException();
        }

        return loadTexture(TextureIO.newTextureData(gl.getGLProfile(), stream, false, suffix),
                filter);
    }

    /**
     * Loads a texture from a byte buffer via the JOGL Texture API.
     * 
     * @param image The byte buffer containing the image in the given format
     * @param width The width, in pixels
     * @param height The height, in pixels
     * @param format The pixel format of the data provided
     * @param internalFormat The internal pixel format used by OpenGL
     * @param mipmap Whether to create and use mipmaps
     * @return The texture
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     */
    public Texture loadTexture(byte[] image, int width, int height, int format, int internalFormat,
            TextureFilter filter) {
        if (image == null || width <= 0 || height <= 0) {
            throw new IllegalArgumentException();
        }

        return loadTexture(new TextureData(gl.getGLProfile(), internalFormat, width, height, 0,
                format, GL_UNSIGNED_BYTE, false, false, false, ByteBuffer.wrap(image), null), 
                filter);
    }

    /**
     * Removes a texture from OpenGL graphics memory.
     * 
     * @param tex
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     */
    public void deleteTexture(Texture tex) {
        assertIsInitialized();
        assertIsInsideCallback();
        if (tex == null) {
            throw new IllegalArgumentException();
        }

        tex.destroy(gl);
    }

    /**
     * Loads a matrix into a given matrix slot (also called "matrix stack").
     * 
     * @param slot The slot, e.g. GL_MODELVIEW or GL_PROJECTION
     * @param matrix The matrix to load. Must not be null
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     * @throws GLError An internal OpenGL error has occurred
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
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     * @throws GLError An internal OpenGL error has occurred
     */
    public void drawVertexBuffer(VertexBuffer vbo, Texture texture) {
        if (vbo == null) {
            throw new IllegalArgumentException();
        }

        assertIsInitialized();
        assertIsInsideCallback();

        if (texture != null) {
            texture.bind(gl);
            GLError.throwIfActive(gl);
        }

        // Bind vertex buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo.getVertices());
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
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo.getIndices());
        GLError.throwIfActive(gl);

        // Draw
        gl.glDrawElements(vbo.getPrimitiveType(), vbo.getIndexCount(), GL_UNSIGNED_INT, 0);
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
     * 
     * @param radius The radius of the sphere. Must be greater than zero
     * @param slices The number of vertices on the equator. Must be greater or equal 3
     * @param stacks The number of vertices from north to south pole. Must be greater or equal 3
     * @param inside Whether to make surfaces pointing inwards instead of outwards
     * @param texture The texture to use. May be null
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     * @throws GLError An internal OpenGL error has occurred
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

        // Enable texturing
        glu.gluQuadricTexture(quadric, true);
        GLError.throwIfActive(gl);

        glu.gluSphere(quadric, radius, slices, stacks);
        GLError.throwIfActive(gl);

        if (texture != null) {
            gl.glBindTexture(GL_TEXTURE_2D, 0);
            GLError.throwIfActive(gl);
        }
    }
    
    
    private void assertIsValidIntensity(double intensity) {
        if (intensity < 0 || intensity > 1 || isNaN(intensity)) {
            throw new IllegalArgumentException();
        }
    }

    private void assertIsValidLight(int index) {
        if (index < 0 || index >= lightCount) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Sets the intensity of the specular component on the current material.
     * @param intensity The intensity, in the range of [0, 1].
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     * @throws GLError An internal OpenGL error has occurred
     */
    public void setMaterialSpecularity(double intensity) {
        assertIsInitialized();
        assertIsInsideCallback();
        assertIsValidIntensity(intensity);
        
        float fi = (float) intensity;
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, new float[] { fi, fi, fi, 1}, 0);
        GLError.throwIfActive(gl);
    }
    

    /**
     * Sets the ambient light intensity in the scene.
     * @param intensity The intensity, in the range of [0, 1].
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     * @throws GLError An internal OpenGL error has occurred
     */
    public void setAmbientLight(double intensity) {
        assertIsInitialized();
        assertIsInsideCallback();
        assertIsValidIntensity(intensity);

        float fi = (float) intensity;
        gl.glLightModelfv(GL_LIGHT_MODEL_AMBIENT, new float[] { fi, fi, fi, 1 }, 0);
        GLError.throwIfActive(gl);
    }
    
    
    /**
     * Returns the number of lights supported.
     * @return The number of lights
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     */
    public int getLightCount() {
        assertIsInitialized();
        return lightCount;
    }
    

    /**
     * Places a GL light in the scene. The position is affected by the model-view-matrix.
     * @param index The light index. Must be between (including) 0 and getLightCount()-1
     * @param position
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     * @throws GLError An internal OpenGL error has occurred
     */
    public void placeLight(int index, Vector3 position) {
        assertIsInitialized();
        assertIsInsideCallback();
        assertIsValidLight(index);
        
        float[] floats = { (float) position.x, (float) position.y, (float) position.z, 1 };
        gl.glLightfv(GL_LIGHT0 + index, GL_POSITION, floats, 0);
        GLError.throwIfActive(gl);
    }
    
    
    /**
     * Sets the intensity of a light.
     * @param index The light index. Must be between (including) 0 and getLightCount()-1
     * @param intensity The intensity, in the range [0, 1]
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     * @throws GLError An internal OpenGL error has occurred
     */
    public void setLightIntensity(int index, double intensity) {
        assertIsInitialized();
        assertIsInsideCallback();
        assertIsValidLight(index);
        assertIsValidIntensity(intensity);
        
        float fi = (float) intensity;        
        gl.glLightfv(GL_LIGHT0 + index, GL_DIFFUSE, new float[] { fi, fi, fi, 1 }, 0);
        GLError.throwIfActive(gl);
    }
    
    
    /**
     * Sets whether a light is used to light the next primitives rendered.
     * @param index The light index. Must be between (including) 0 and getLightCount()-1
     * @param enabled Whether the light should be used
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     * @throws GLError An internal OpenGL error has occurred
     */
    public void setLightEnabled(int index, boolean enabled) {
        assertIsValidLight(index);
        setFeatureEnabled(GL_LIGHT0 + index, enabled);
    }
    
    
    /**
     * Returns whether a light is currently enabled.
     * @param index The light index. Must be between (including) 0 and getLightCount()-1
     * @return Whether the light is enabled
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     * @throws GLError An internal OpenGL error has occurred
     */
    public boolean isLightEnabled(int index) {
        assertIsValidLight(index);
        return isFeatureEnabled(GL_LIGHT0 + index);
    }

    /**
     * Clears the OpenGL color and depth buffer.
     * 
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     * @throws GLError An internal OpenGL error has occurred
     */
    public void clear() {
        assertIsInitialized();
        assertIsInsideCallback();

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        GLError.throwIfActive(gl);
    }

    /**
     * Determines whether an OpenGL feature is active (glIsEanbled).
     * 
     * @param The feature to check, e.g. GL_DEPTH_TEST
     * @return Whether the feature is enabled
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     * @throws GLError An internal OpenGL error has occurred
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
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     * @throws GLError An internal OpenGL error has occurred
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
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     * @throws GLError An internal OpenGL error has occurred
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

        animator.stop();

        beginDisplay();
        for (GLContextListener l : listeners) {
            l.dispose(this);
        }
        endDisplay(true);

        animator = null;
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
        animator = new FPSAnimator(drawable, 60);

        beginDisplay();
        
        int[] integers = new int[1];
        gl.glGetIntegerv(GL_MAX_LIGHTS, integers, 0);
        GLError.throwIfActive(gl);
        lightCount = integers[0];
        
        String extensions = gl.glGetString(GL_EXTENSIONS);
        GLError.throwIfActive(gl);
        
        anisotropySupported = extensions.contains("GL_EXT_texture_filter_anisotropic");
        
        if (anisotropySupported) {
            int[] ints = {0};
            gl.glGetIntegerv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, ints, 0);
            GLError.throwIfActive(gl);
            maxAnisotropy = ints[0];
        }
        
        if (drawable.getChosenGLCapabilities().getSampleBuffers()) {
            setFeatureEnabled(GL_MULTISAMPLE, true);
        }
        
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
        while (listeners.remove(l))
            ;
    }

    /**
     * Starts redisplaying at a target frame rate of 60 FPS. Calls to postRedisplay are delayed to
     * the call of stopDisplayLoop().
     * 
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     */
    public synchronized void startDisplayLoop() {
        assertIsInitialized();
        animator.start();
    }

    /**
     * Starts redisplaying at a custom target frame rate. Calls to postRedisplay are delayed to the
     * call of stopDisplayLoop().
     * 
     * @param fps The target frame rate in FPS
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     */
    public synchronized void startDisplayLoop(int fps) {
        assertIsInitialized();
        animator.setFPS(fps);
        animator.start();
    }

    /**
     * Stops the display loop started by startDisplayLoop, if any. Resumes suspended redisplay
     * requests.
     * 
     * @throws IllegalStateException The context has not yet been initialized by a GLAutoDrawable
     */
    public synchronized void stopDisplayLoop() {
        assertIsInitialized();
        animator.stop();
        if (redisplayPending) {
            postRedisplay();
        }
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
                if (redisplayActive || animator.isAnimating()) {
                    return;
                }
                redisplayActive = true;
            }

            // Call invokeLater() while there are pending frames. Don't use a loop so that other
            // AWT events can be processed as well.
            AWTInvoker.getInstance().invokeLater(new Runnable() {

                @Override
                public void run() {
                    if (!isInitialized()) {
                        return;
                    }
                    
                    drawable.display();

                    boolean doContinue;
                    synchronized (GLContext.this) {
                        redisplayPending = false;
                        doContinue = redisplayPending && !animator.isAnimating();
                    }

                    // might be re-set in another thread
                    if (doContinue) {
                        AWTInvoker.getInstance().invokeLater(this);
                    } else {
                        redisplayActive = false;
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
