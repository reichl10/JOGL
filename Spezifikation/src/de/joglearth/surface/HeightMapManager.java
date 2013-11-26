package de.joglearth.surface;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Tile;
import de.joglearth.settings.SettingsListener;
import de.joglearth.source.SourceListener;
import de.joglearth.source.caching.FileSystemCache;
import de.joglearth.source.caching.MemoryCache;
import de.joglearth.source.srtm.SRTMTileSource;


/**
 * Singleton class for interpolation of informations about the height of points displayed on the
 * map. For that purpose SRTM height data is used. A {@link de.joglearth.rendering.Tessellator}
 * uses this class to generate a map surface by the {@link HeightMap}.
 */
public class HeightMapManager {
    
    private static HeightMapManager instance = null;
    

    /**
     * Returns the existing instance of {@link HeightMapManager}. If there is no such object, it's
     * created.
     * @return The singletons instance.
     */
    public static HeightMapManager getInstance() {
        if (instance == null) {
            instance = new HeightMapManager();
        }
        return instance;
    }

    //Private constructor for the HeightMapManager
    private HeightMapManager() {
    }

    /**
     * Tries to determine the height of a point using the SRTM data that contains its
     * {@link GeoCoordinates} or returns default <code>0</code> if no value was found.
     * 
     * @param coords The <code>GeoCoordinates</code> of the point
     * @return The height of the wanted point, <code>0</code> if the height of the point is not yet
     *         in the cache
     */
    public double getHeight(GeoCoordinates coords) {
        /*
         * approximate longitude and latitude to arcsec, determine tileCoordinate, request tile
         * from cache, interpolate height value.
         */
        return 0;
    }

    /**
     * Adds a {@link SurfaceListener} that distributes a notification if the surface was changed.
     * 
     * @param l The new listener
     */
    public void addSurfaceListener(SurfaceListener l) {

    }

    /**
     * Removes a specific {@link SurfaceListener}.
     * 
     * @param l The listener that should be removed
     */
    public void removeSurfaceListener(SurfaceListener l) {

    }
}
