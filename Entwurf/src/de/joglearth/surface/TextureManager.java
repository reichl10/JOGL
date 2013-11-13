package de.joglearth.surface;

import de.joglearth.caching.CachedSource;
import de.joglearth.geometry.Tile;



public class TextureManager {

    private Integer                     placeholderTexture;
    private CachedSource<Tile, Integer> source;


    public Integer getTexture(Tile tile) {
        return placeholderTexture;
    }

    public void addSurfaceListener(SurfaceListener l) {

    }

    public void removeSurfaceListener(SurfaceListener l) {

    }

    public int getPendingRequests() {
        return 0;
    }
}
