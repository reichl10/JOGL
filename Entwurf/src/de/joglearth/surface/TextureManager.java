package de.joglearth.surface;

import de.joglearth.caching.RequestDistributor;
import de.joglearth.geometry.Tile;



public class TextureManager {

    private Integer                     placeholderTexture;
    private RequestDistributor<Tile, Integer> source;


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
