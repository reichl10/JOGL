package de.joglearth.rendering;

import de.joglearth.async.RunnableResultListener;
import de.joglearth.async.RunnableWithResult;
import de.joglearth.geometry.Tile;
import de.joglearth.height.HeightMap;
import de.joglearth.height.flat.FlatHeightMap;
import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.VertexBuffer;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;


/**
 * Adapter for a {@link de.joglearth.rendering.Tessellator} to use it as a
 * {@link de.joglearth.source.Source}.
 */
public class VertexBufferLoader implements Source<Tile, VertexBuffer> {

    private Tessellator tess;
    private GLContext gl;
    private int subdivisions;
    private HeightMap heightMap = FlatHeightMap.getInstance();
    

    /**
     * Constructor. Initializes the {@link de.joglearth.rendering.VertexBufferLoader} as it assign
     * values to its GL context and {@link de.joglearth.rendering.Tesselator}.
     * 
     * @param gl The GL context of <code>TileMeshManager</code>. Must not be null
     * @param t The <code>Tesselator</code> of the <code>TileMeshManager</code>. May be null
     */
    public VertexBufferLoader(GLContext gl, Tessellator t) {
        if (gl == null) {
            throw new IllegalArgumentException();
        }
        this.gl = gl;
        tess = t;
    }

    /**
     * Sets the {@link de.joglearth.rendering.Tessellator} of the
     * {@link de.joglearth.rendering.VertexBufferLoader}
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

    private VertexBuffer createVBO(Tile key) {
        Mesh mesh;
        synchronized (this) {
            mesh = tess.tessellateTile(key, subdivisions, heightMap);
        }

        return gl.loadMesh(mesh);
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

        if (gl.isInsideCallback()) {
            return new SourceResponse<VertexBuffer>(SourceResponseType.SYNCHRONOUS, createVBO(key));
        } else {
            gl.invokeLater(
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

    @Override
    public void dispose() {
    }

    public void setHeightMap(HeightMap heightMap) {
        this.heightMap = heightMap;
    }
}