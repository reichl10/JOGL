package de.joglearth.source.srtm;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.caching.RequestDistributor;
import de.joglearth.surface.SurfaceListener;


/**
 * Singleton class that retrieves data from the {@link de.joglearth.source.srtm.SRTMTileSource}.
 */
public final class SRTMTileManager implements Source<SRTMTileIndex, SRTMTile> {

    private static SRTMTileManager                      instance = null;

    private RequestDistributor<SRTMTileIndex, SRTMTile> dist;


    /**
     * Returns the instance of the class or creates it, if it does not exist yet.
     * 
     * @return The instance of <code>SRTMTileManager</code>
     */
    public static SRTMTileManager getInstance() {
        if (instance == null) {
            instance = new SRTMTileManager();
        }
        return instance;
    }

    // Default constructor
    private SRTMTileManager() {
        dist = new RequestDistributor<SRTMTileIndex, SRTMTile>();
        
        //TODO caches
    }

    @Override
    public SourceResponse<SRTMTile> requestObject(SRTMTileIndex key,
            SourceListener<SRTMTileIndex, SRTMTile> sender) {
        return dist.requestObject(key, sender);
    }

    /**
     * Adds a new {@link de.joglearth.surface.SurfaceListener}.
     * 
     * @param l The new <code>SurfaceListener</code>
     */
    public void addSurfaceListener(SurfaceListener l) {

    }

    /**
     * Removes a given {@link de.joglearth.surface.SurfaceListener}.
     * 
     * @param l The <code>SurfaceListener</code> that should be removed
     */
    public void removeSurfaceListener(SurfaceListener l) {

    }

}
