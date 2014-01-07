package de.joglearth.height.flat;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.SurfaceListener;
import de.joglearth.height.HeightMap;


public class FlatHeightMap implements HeightMap {

    private static FlatHeightMap instance;
    
    private FlatHeightMap() {  }
    
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

}
