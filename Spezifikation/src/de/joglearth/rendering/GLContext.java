package de.joglearth.rendering;

import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import de.joglearth.geometry.Matrix4;
import de.joglearth.source.opengl.VertexBuffer;
import de.joglearth.util.AWTInvoker;
import de.joglearth.util.RunnableResultListener;
import de.joglearth.util.RunnableWithResult;
import static javax.media.opengl.GL2.*;


public final class GLContext implements GLEventListener {

    private GL2 gl = null;
    private Thread glThread = null;
    private GLAutoDrawable drawable = null;
    private ArrayList<GLContextListener> listeners = new ArrayList<>();
    private boolean insideDisplay = false, insideFrame = false;


    private class Invocation {

        public Runnable runnable;
        public RunnableResultListener listener;

        public Invocation(Runnable r, RunnableResultListener l) {
            runnable = r;
            listener = l;
        }
    }


    private ArrayList<Invocation> pendingInvocations = new ArrayList<>();
    private volatile boolean isPosted;
    private volatile boolean isRunning;


    private void assertIsInitialized() {
        if (drawable == null) {
            throw new IllegalStateException("GLContext has not yet been initialized");
        }
    }

    private void assertIsInitialized(GLAutoDrawable caller) {
        assertIsInitialized();
        if (caller != drawable) {
            throw new IllegalStateException(
                    "GLContext has not been initialized with the given GLAutoDrawable");
        }
    }

    private void assertIsInsideDisplayFunction() {
        if (!isInsideDisplayFunction()) {
            throw new IllegalStateException(
                    "GL operations must not be performed outside a GL callback");
        }
    }

    private void assertValidIds(int[] ids) {
        for (int id : ids) {
            if (id <= 0) {
                throw new GLError("Unable to allocate GL object (returned id 0)");
            }
        }
    }

    public boolean isInitialized() {
        return drawable != null;
    }

    public boolean isInsideDisplayFunction() {
        return drawable != null && Thread.currentThread().equals(glThread) && insideDisplay;
    }

    public Dimension getSize() {
        assertIsInitialized();
        return new Dimension(drawable.getWidth(), drawable.getHeight());
    }

    public int loadTexture(byte[] image, int format, int internalFormat, int width, int height,
            boolean mipmaps) {
        assertIsInitialized();
        assertIsInsideDisplayFunction();

        if (image == null || width <= 0 || height <= 0) {
            throw new IllegalArgumentException();
        }

        int[] ids = new int[1];
        gl.glGenTextures(1, ids, 0);
        GLError.throwIfActive(gl);
        assertValidIds(ids);

        gl.glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height,
                0, format, GL_UNSIGNED_BYTE, ByteBuffer.wrap(image));
        GLError.throwIfActive(gl);

        if (mipmaps) {
            gl.glGenerateMipmap(GL_TEXTURE_2D);
            GLError.throwIfActive(gl);
        }

        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
                mipmaps ? GL_LINEAR_MIPMAP_LINEAR : GL_LINEAR);
        GLError.throwIfActive(gl);

        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        GLError.throwIfActive(gl);

        gl.glBindTexture(GL_TEXTURE_2D, 0);
        GLError.throwIfActive(gl);

        return ids[0];
    }

    public void deleteTexture(int id) {
        assertIsInitialized();
        assertIsInsideDisplayFunction();

        if (id <= 0) {
            throw new IllegalArgumentException();
        }

        int[] ids = { id };
        gl.glDeleteTextures(GL_TEXTURE_2D, ids, 0);
        GLError.throwIfActive(gl);
    }

    public VertexBuffer loadMesh(Mesh mesh) {
        if (mesh == null || mesh.indices == null || mesh.indexCount < 0 || mesh.vertices == null) {
            throw new IllegalArgumentException();
        }

        assertIsInitialized();
        assertIsInsideDisplayFunction();

        // Allocate vertex and index buffer
        int[] buffers = new int[2];
        gl.glGenBuffers(2, buffers, 0);
        GLError.throwIfActive(gl);
        assertValidIds(buffers);

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

    public void deleteVertexBuffer(VertexBuffer vbo) {
        if (vbo == null || vbo.indices <= 0 || vbo.vertices <= 0) {
            throw new IllegalArgumentException();
        }

        assertIsInitialized();
        assertIsInsideDisplayFunction();

        gl.glDeleteBuffers(2, new int[] { vbo.vertices, vbo.indices }, 0);
        GLError.throwIfActive(gl);
    }

    public void loadMatrix(int slot, Matrix4 matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException();
        }

        assertIsInitialized();
        assertIsInsideDisplayFunction();

        gl.glMatrixMode(slot);
        GLError.throwIfActive(gl);

        gl.glLoadMatrixd(matrix.doubles(), 0);
        GLError.throwIfActive(gl);
    }

    public void drawVertexBuffer(VertexBuffer vbo, int texture) {
        if (vbo == null || texture <= 0) {
            throw new IllegalArgumentException();
        }

        assertIsInitialized();
        assertIsInsideDisplayFunction();

        gl.glBindTexture(GL_TEXTURE_2D, texture);
        GLError.throwIfActive(gl);

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

    public void clear() {
        assertIsInitialized();
        assertIsInsideDisplayFunction();

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void invokeLater(Runnable runnable, RunnableResultListener listener) {
        synchronized (pendingInvocations) {
            pendingInvocations.add(new Invocation(runnable, listener));
        }
        postRedisplay();
    }

    public void invokeLater(Runnable runnable) {
        invokeLater(runnable, null);
    }


    private static class RunnableResultAdapter implements Runnable {

        public Object result;
        public RunnableWithResult runnable;


        public RunnableResultAdapter(RunnableWithResult runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            result = runnable.run();
        }

    };


    public void invokeLater(final RunnableWithResult runnable, RunnableResultListener listener) {
        RunnableResultAdapter wrapper = new RunnableResultAdapter(runnable);
        invokeLater(wrapper, listener);
    }

    public void invokeLater(RunnableWithResult runnable) {
        invokeLater(runnable, null);
    }

    public void invokeSooner(Runnable runnable, RunnableResultListener listener) {
        if (runnable == null) {
            throw new IllegalArgumentException();
        }

        if (isInsideDisplayFunction()) {
            runnable.run();
            if (listener != null) {
                listener.runnableCompleted(null);
            }
        } else {
            invokeLater(runnable, listener);
        }
    }

    public void invokeSooner(RunnableWithResult runnable, RunnableResultListener listener) {
        if (isInsideDisplayFunction()) {
            Object result = runnable.run();
            if (listener != null) {
                listener.runnableCompleted(result);
            }
        } else {
            invokeLater(runnable, listener);
        }
    }

    private void beginDisplay() {
        insideDisplay = true;

        ArrayList<Invocation> pendingCopy;
        synchronized (pendingInvocations) {
            pendingCopy = pendingInvocations;
            pendingInvocations = new ArrayList<>();
        }
        for (Invocation inv : pendingCopy) {
            inv.runnable.run();

            if (inv.listener != null) {
                if (inv.runnable instanceof RunnableResultAdapter) {
                    inv.listener.runnableCompleted(((RunnableResultAdapter) inv.runnable).result);
                } else {
                    inv.listener.runnableCompleted(null);
                }
            }
        }

        if (!insideFrame) {
            insideFrame = true;
            for (GLContextListener l : listeners) {
                l.beginFrame(this);
            }
        }
    }

    private void endDisplay(boolean endFrame) {
        if (endFrame && insideFrame) {
            insideFrame = false;
            for (GLContextListener l : listeners) {
                l.endFrame(this);
            }
        }
        insideDisplay = false;
    }

    @Override
    public synchronized void display(GLAutoDrawable arg0) {
        assertIsInitialized(arg0);

        beginDisplay();
        for (GLContextListener l : listeners) {
            l.display(this);
        }
        endDisplay(true);
    }

    @Override
    public synchronized void dispose(GLAutoDrawable arg0) {
        assertIsInitialized(arg0);

        beginDisplay();
        for (GLContextListener l : listeners) {
            l.dispose(this);
        }
        endDisplay(true);

        drawable = null;
        gl = null;
        glThread = null;
    }

    @Override
    public synchronized void init(GLAutoDrawable arg0) {
        if (drawable != null) {
            throw new IllegalStateException();
        }

        drawable = arg0;
        gl = drawable.getGL().getGL2();
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

    public synchronized void addGLContextListener(GLContextListener l) {
        listeners.add(l);
    }

    public synchronized void removeGLContextListener(GLContextListener l) {
        while (listeners.remove(l));
    }

    /**
     * Notifies the {@link de.joglearth.rendering.Renderer} that a new frame should be rendered. If
     * <code>start()</code> is called this method may have no effect. Asynchronous method, does not
     * wait until a frame is drawn.
     */
    public void postRedisplay() {
        if (isInitialized()) {
            synchronized (this) {
                isPosted = true;
                if (isRunning) {
                    return;
                }
                isRunning = true;
            }

            AWTInvoker.invoke(new Runnable() {

                @Override
                public void run() {
                    do {
                        isPosted = false;
                        drawable.display();
                    } while (isPosted);
                    isRunning = false;
                }
            });
        }
    }

}
