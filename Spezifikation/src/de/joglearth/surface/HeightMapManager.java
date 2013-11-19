package de.joglearth.surface;

import de.joglearth.caching.FileSystemCache;
import de.joglearth.caching.MemoryCache;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Tile;
import de.joglearth.settings.SettingsListener;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SRTMTileSource;


public class HeightMapManager implements SettingsListener {

    // primary cache. handles the secondary (file system) cache and
	// loading the height data using HTTP. (as defined in the constructor of heigthMap)
    // memory cache doesn't know file system cash??
    private MemoryCache<Tile, byte[]> cache;


    public HeightMapManager() {
        FileSystemCache<Tile> fsCache = null;
        SRTMTileSource source = null;
        cache = new MemoryCache<Tile, byte[]>();
    }

    /**
     * This method tries to determine the height of a point
     * @param coords the GEOCoordinates of the point
     * @return the height of the wanted point, 0 if the height of the point
     *          is not yet in the cache
     */
    public float getHeight(GeoCoordinates coords) {
        /*
         * l�ngen- breitengrad auf bogensekunden runden, tileCoordinate
         * bestimmen kachel vom cache anfordern, h�henwert
         * interpolieren(bestimmen).
         */
        return 0;
    }

    /**
     * Adds a listener that distributes a notification if the surface was changed
     * @param l
     */
    public void addSurfaceListener(SurfaceListener l) {

    }

    /**
     * Removes a listener
     * @param l the listener that should be removed
     */
    public void removeSurfaceListener(SurfaceListener l) {

    }

    @Override
    public void settingsChanged(String key, Object valOld, Object valNew) {

    }
}