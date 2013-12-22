package de.joglearth.surface;

import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL2;

import de.joglearth.geometry.Tile;
import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.VertexBuffer;
import de.joglearth.rendering.Renderer;
import de.joglearth.rendering.Tessellator;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.RequestDistributor;
import de.joglearth.source.opengl.TileMeshSource;
import de.joglearth.source.opengl.VertexBufferCache;
import de.joglearth.util.Predicate;


/**
 * Creates and cashes tile meshes as OpenGL vertex buffer objects.
 */
public class TileMeshManager implements Source<Tile, VertexBuffer> {

    private final int                         VERTEX_BUFFER_CACHE_SIZE = 50;

    private RequestDistributor<Tile, VertexBuffer> dist;
    private VertexBufferCache                 cache;
    private TileMeshSource                    source;
    private List<SurfaceListener> listeners;

    /**
     * Creates a new {@link TileMeshManager} as it initializes the source, the cache, sets the
     * {@link de.joglearth.source.caching.RequestDistributor} and adds a surface listener from {@link de.joglearth.surface.HeightMap}.
     * 
     * @param gl The GL context. May not be null.
     * @param t The <code>Tessellator</code> that should be used. May be null.
     */
    public TileMeshManager(GLContext gl, Tessellator t) {
        if (gl == null) {
            throw new IllegalArgumentException();
        }
        
        source = new TileMeshSource(gl, t);
        cache = new VertexBufferCache(gl);
        dist = new RequestDistributor<Tile, VertexBuffer>();
        dist.setSource(source);
        dist.addCache(cache, VERTEX_BUFFER_CACHE_SIZE);

        HeightMap.addSurfaceListener(
            new SurfaceListener() {

                @Override
                public void surfaceChanged(final double lonFrom, final double latFrom,
                        final double lonTo, final double latTo) {
                    dist.dropAll(new Predicate<Tile>() {

                        @Override
                        public boolean test(Tile t) {
                            return t.intersects(lonFrom, latFrom, lonTo, latTo);
                        }
                    });
                    // notify listeners!
                }
            });
        listeners = new LinkedList<SurfaceListener>();
    }

    /**
     * Sets the {@link de.joglearth.rendering.Tessellator} of the
     * {@link de.joglearth.source.opengl.TileMeshSource}
     * 
     * @param t The new <code>Tesselator</code>
     */
    public void setTessellator(Tessellator t) {
        if (t == null || !t.equals(source.getTessellator())) {
            source.setTessellator(t);
            dist.dropAll();
        }
    }

    /**
     * Sets the level of detail of the {@link de.joglearth.rendering.Tessellator}.
     * 
     * @param sub The new <code>level of detail</code>
     */
    public void setTileSubdivisions(int sub) {
        if (sub != source.getTileSubdivisions()) {
            source.setTileSubdivisions(sub);
            dist.dropAll();            
        }
    }

    /**
     * Enables or disables the {@link de.joglearth.surface.HeightMap}.
     * 
     * @param enable Whether to enable or disable the <code>HeightMap</code>
     */
    public void setHeightMapEnabled(boolean enable) {
        if (enable != source.isHeightMapEnabled()) {
            source.setHeightMapEnabled(enable);
            dist.dropAll();
        }
    }

    @Override
    public SourceResponse<VertexBuffer> requestObject(Tile key,
            SourceListener<Tile, VertexBuffer> sender) {
        return dist.requestObject(key, sender);
        //return source.requestObject(key, sender);
    }

    /**
     * Adds a {@link SurfaceListener} that distributes a notification if the surface was changed.
     * 
     * @param l The new listener
     */
    public void addSurfaceListener(SurfaceListener l) {
        if (l == null) {
            throw new IllegalArgumentException();
        }
        
        listeners.add(l);
    }

    /**
     * Removes a specific {@link SurfaceListener}.
     * 
     * @param l The listener that should be removed
     */
    public void removeSurfaceListener(SurfaceListener l) {
        if (l == null) {
            throw new IllegalArgumentException();
        }
        
        listeners.remove(l);
    }

}
