package de.joglearth.source.opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import de.joglearth.geometry.Tile;
import de.joglearth.opengl.GLError;
import de.joglearth.opengl.VertexBuffer;
import de.joglearth.rendering.Mesh;
import de.joglearth.rendering.Renderer;
import de.joglearth.rendering.Tessellator;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.util.RunnableResultListener;
import de.joglearth.util.RunnableWithResult;
import static javax.media.opengl.GL2.*;


/**
 * Adapter for a {@link de.joglearth.rendering.Tessellator} to use it as a
 * {@link de.joglearth.source.Source}.
 */
public class TileMeshSource implements Source<Tile, VertexBuffer> {

    private Tessellator tess;
    private GL2 gl;
    private int subdivisions;
    private boolean heightMap;
    private Renderer renderer;
    

    /**
     * Constructor. Initializes the {@link de.joglearth.source.opengl.TileMeshSource} as it assign
     * values to its GL context and {@link de.joglearth.rendering.Tesselator}.
     * 
     * @param gl The GL context of <code>TileMeshManager</code>. Must not be null.
     * @param t The <code>Tesselator</code> of the <code>TileMeshManager</code>. May be null.
     */
    public TileMeshSource(Renderer renderer, GL2 gl, Tessellator t) {
        if (renderer == null || gl == null) {
            throw new IllegalArgumentException();
        }

        this.renderer = renderer;
        this.gl = gl;
        tess = t;
    }

    /**
     * Sets the {@link de.joglearth.rendering.Tessellator} of the
     * {@link de.joglearth.source.opengl.TileMeshSource}
     * 
     * @param t The new <code>Tesselator</code>. May be null.
     */
    public synchronized void setTessellator(Tessellator t) {
        tess = t;
    }

    public Tessellator getTessellator() {
        return tess;
    }

    /**
     * Sets the level of detail of the {@link de.joglearth.rendering.Tessellator}.
     * 
     * @param sub The new level of detail
     */
    public synchronized void setTileSubdivisions(int sub) {
        if (subdivisions < 0) {
            throw new IllegalArgumentException();
        }

        subdivisions = sub;
    }

    public int getTileSubdivisions() {
        return subdivisions;
    }

    /**
     * Enables or disables the {@link de.joglearth.surface.HeightMap}.
     * 
     * @param enable Whether to enable or disable the <code>HeightMap</code>
     */
    public synchronized void setHeightMapEnabled(boolean enable) {
        heightMap = enable;
    }

    public boolean isHeightMapEnabled() {
        return heightMap;
    }

    public VertexBuffer createVBO(Tile key) {
        Mesh mesh;
        synchronized (this) {
            mesh = tess.tessellateTile(key, subdivisions, heightMap);
        }

        // Allocate vertex and index buffer
        int[] buffers = new int[2];
        gl.glGenBuffers(2, buffers, 0);
        GLError.throwIfActive(gl);

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

    @Override
    public SourceResponse<VertexBuffer> requestObject(final Tile key,
            final SourceListener<Tile, VertexBuffer> sender) {

        if (key == null) {
            throw new IllegalArgumentException();
        }

        if (tess == null) {
            throw new IllegalStateException("Requested tile from null tessellator");
        }

        if (renderer.isInsideDisplayFunction()) {
            return new SourceResponse<VertexBuffer>(SourceResponseType.SYNCHRONOUS, createVBO(key));
        } else {
            renderer.invokeLater(
                    new RunnableWithResult() {
    
                        @Override
                        public Object run() {
                            return createVBO(key);
                        }
                    },
    
                    new RunnableResultListener() {
    
                        @Override
                        public void runnableCompleted(Object result) {
                            sender.requestCompleted(key, (VertexBuffer) result); 
                        }
                    });
            return new SourceResponse<VertexBuffer>(SourceResponseType.ASYNCHRONOUS, null);
        }
    }
}
