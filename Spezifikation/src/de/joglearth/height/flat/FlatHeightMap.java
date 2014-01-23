package de.joglearth.height.flat;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.SurfaceListener;
import de.joglearth.height.HeightMap;

/**
 * Singleton class that returns 0 for all height requests.
 * 
 */
public class FlatHeightMap implements HeightMap {

    private static FlatHeightMap instance;
    
    private FlatHeightMap() {  }

    /**
     * Returns the instance of the class or creates it, if it does not exist yet.
     * 
     * @return The instance of <code>FlatHeightMap</code>
     */
    public static FlatHeightMap getInstance() {
        if (instance == null) {
            instance = new FlatHeightMap();
        }
        return instance;
    }
    
    @Override
    public double getHeight(GeoCoordinates coords, double angularResolution) {
        return 0;
    }

    @Override
    public void addSurfaceListener(SurfaceListener l) { }

    @Override
    public void removeSurfaceListener(SurfaceListener l) { }

    @Override
    public int hashCode() {
        return "FlatHeightMap".hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass() == obj.getClass();
    }
    
}
