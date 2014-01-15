package de.joglearth.rendering;

import java.util.LinkedList;
import java.util.List;

import de.joglearth.geometry.ProjectedTile;
import de.joglearth.geometry.SurfaceListener;
import de.joglearth.geometry.Tile;
import de.joglearth.height.HeightMap;
import de.joglearth.height.flat.FlatHeightMap;
import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.VertexBuffer;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.RequestDistributor;
import de.joglearth.util.Predicate;


/**
 * Creates and cashes tile meshes as OpenGL vertex buffer objects.
 */
public class VertexBufferManager implements Source<ProjectedTile, VertexBuffer> {

    private final int VERTEX_BUFFER_CACHE_SIZE = 500;

    private RequestDistributor<ProjectedTile, VertexBuffer> dist;
    private VertexBufferPool<ProjectedTile> cache;
    private VertexBufferLoader source;
    private List<SurfaceListener> listeners;
    private GLContext gl;
    private HeightMap heightMap = FlatHeightMap.getInstance();
    private SurfaceListener heightListener = new SurfaceHeightListener();


    /**
     * Creates a new {@link VertexBufferManager} as it initializes the source, the cache, sets the
     * {@link de.joglearth.source.caching.RequestDistributor} and adds a surface listener from
     * {@link de.joglearth.surface.HeightMap}.
     * 
     * @param gl The GL context. May not be null.
     * @param t The <code>Tessellator</code> that should be used. May be null.
     */
    public VertexBufferManager(GLContext gl, Tessellator t) {
        if (gl == null) {
            throw new IllegalArgumentException();
        }

        this.gl = gl;
        source = new VertexBufferLoader(gl, t);
        cache = new VertexBufferPool<ProjectedTile>(gl);
        dist = new RequestDistributor<ProjectedTile, VertexBuffer>();
        dist.setSource(source);
        dist.addCache(cache, VERTEX_BUFFER_CACHE_SIZE);

        listeners = new LinkedList<SurfaceListener>();
    }

    private class SurfaceHeightListener implements SurfaceListener {

        @Override
        public void surfaceChanged(final double lonFrom, final double latFrom,
                final double lonTo, final double latTo) {
            dist.dropAll(new Predicate<ProjectedTile>() {

                @Override
                public boolean test(ProjectedTile t) {
                    return t.tile.intersects(lonFrom, latFrom, lonTo, latTo);
                }
            });
            // notify listeners!
        }
    };
    
    /**
     * Sets the {@link de.joglearth.rendering.Tessellator} of the
     * {@link de.joglearth.rendering.VertexBufferLoader}
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
    public void setHeightMap(HeightMap heightMap) {
        if (heightMap == null) {
            throw new IllegalArgumentException();
        }
        this.heightMap.removeSurfaceListener(heightListener);
        this.heightMap = heightMap;
        this.heightMap.addSurfaceListener(heightListener);
        
        source.setHeightMap(heightMap);
        dist.dropAll();
    }

    @Override
    public SourceResponse<VertexBuffer> requestObject(ProjectedTile key,
            SourceListener<ProjectedTile, VertexBuffer> sender) {
        return dist.requestObject(key, sender);
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

    public void dispose() {
        gl.invokeSooner(new Runnable() {
            
            @Override
            public void run() {
                dist.dropAll();
            }
        });
    }

}
