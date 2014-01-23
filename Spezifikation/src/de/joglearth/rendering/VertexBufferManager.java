package de.joglearth.rendering;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.joglearth.geometry.SurfaceListener;
import de.joglearth.height.HeightMap;
import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.VertexBuffer;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.RequestDistributor;


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
    private SurfaceListener heightListener = new SurfaceHeightListener();
    private HashSet<WeakReference<HeightMap>> observedHeightMaps = new HashSet<>();


    /**
     * Creates a new {@link VertexBufferManager} as it initializes the source, the cache, sets the
     * {@link RequestDistributor} and adds a surface listener from {@link HeightMap}.
     * 
     * @param gl The GL context. May not be null.
     * @param t The {@link Tessellator} that should be used. May be null.
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

            //drop for predicate
            dist.dropAll();

            for (SurfaceListener sirFace : listeners) {
                sirFace.surfaceChanged(lonFrom, latFrom, lonTo, latTo);
            }
        }
    };


    /**
     * Sets the {@link Tessellator} of the
     * {@link VertexBufferLoader}
     * 
     * @param t The new <code>Tesselator</code>
     */
    public void setTessellator(Tessellator t) {
        if (t == null || !t.equals(source.getTessellator())) {
            source.setTessellator(t);
            dist.dropAll();
        }
    }

    @Override
    public SourceResponse<VertexBuffer> requestObject(ProjectedTile key,
            SourceListener<ProjectedTile, VertexBuffer> sender) {
        if (observedHeightMaps.add(new WeakReference<HeightMap>(key.heightMap))) {
            key.heightMap.addSurfaceListener(heightListener);
        }
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

    @Override
    public void dispose() {
        gl.invokeSooner(new Runnable() {

            @Override
            public void run() {
                dist.dropAll();
            }
        });
    }
}
