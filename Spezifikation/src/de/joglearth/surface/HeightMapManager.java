package de.joglearth.surface;

import de.joglearth.caching.FileSystemCache;
import de.joglearth.caching.MemoryCache;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Tile;
import de.joglearth.settings.SettingsListener;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SRTMTileSource;


/**
 * Interpolates informations about the height of points displayed on the map using SRTM height data.
 * Tesselators are using this class to generate a map surface by the HeightMap.
 */
public class HeightMapManager implements SettingsListener {

    private MemoryCache<Tile, byte[]> cache;

    /**
     * Constructor for HeightMapManager which knows and initializes a <code>FileSystemCache</code>,
     * a <code>SRTMTileSource</code> and a <code>MemoryCache</code>.
     */
    public HeightMapManager() {
        FileSystemCache<Tile> fsCache = null;
        SRTMTileSource source = null;
        cache = new MemoryCache<Tile, byte[]>();
    }

    /**
     * Tries to determine the height of a point using the SRTM data that contains its
     * <code>GeoCoordinates</code> or returns default <code>0</code> if no value was found.
     * 
     * @param coords The GeoCoordinates of the point.
     * @return The height of the wanted point, 0 if the height of the point is not yet in the cache.
     */
    public float getHeight(GeoCoordinates coords) {
        /*
         * l�ngen- breitengrad auf bogensekunden runden, tileCoordinate bestimmen kachel vom cache
         * anfordern, h�henwert interpolieren(bestimmen).
         */
        return 0;
    }

    /**
     * Adds a <code>SurfaceListener</code> that distributes a notification if the surface
     * was changed.
     * 
     * @param l The new listener.
     */
    public void addSurfaceListener(SurfaceListener l) {

    }

    /**
     * Removes a specific <code>SurfaceListener</code>.
     * 
     * @param l The listener that should be removed.
     */
    public void removeSurfaceListener(SurfaceListener l) {

    }

    @Override
    public void settingsChanged(String key, Object valOld, Object valNew) {

    }
}
