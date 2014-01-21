package de.joglearth.height.srtm;

import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.List;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.SurfaceListener;
import de.joglearth.height.HeightMap;
import de.joglearth.source.SourceListener;


public class SRTMHeightMap implements HeightMap {

    private static SRTMHeightMap instance = null;
    
    private final SRTMListener srtmListener = new SRTMListener();
    private final ArrayList<SurfaceListener> listeners = new ArrayList<>();
    private final static SRTMTileManager srtm = SRTMTileManager.getInstance();
    private final static double[] lodResolutions = { 1.7044e-05, 3.4088e-05, 6.8177e-05, 
        1.3635e-04, 2.7271e-04, 5.4542e-04, 1.0908e-03, 2.1817e-03, 4.3633e-03, 8.7266e-03,
        1.7453e-02 };
    
    public final static double EARTH_RADIUS_METERS = 6378000.0;
    public final static double MAX_HEIGHT = Short.MAX_VALUE / EARTH_RADIUS_METERS;
    public final static double MIN_HEIGHT = -MAX_HEIGHT;


    private class SRTMListener implements SourceListener<SRTMTileName, SRTMTile> {

        private double degToRad(double deg) {
            return (deg/180)*PI;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void requestCompleted(SRTMTileName key, SRTMTile value) {
            ArrayList<SurfaceListener> listenersClone 
                = (ArrayList<SurfaceListener>) listeners.clone();
            for (SurfaceListener l : listenersClone) {
                l.surfaceChanged(degToRad(key.longitude), degToRad(key.latitude),
                        degToRad(key.longitude + 1), degToRad(key.latitude + 1));
            }
        }
    }


    private SRTMHeightMap() {}
    
    public static SRTMHeightMap getInstance() {
        if (instance == null) {
            instance = new SRTMHeightMap();
        }
        return instance;
    }
    

    private static int toTileIndex(double rad) {
        return (int) floor(rad / PI * 180);
    }

    private static double getTileOffset(double angle) {
        return (abs(angle) / PI * 180) % 1;
    }

    @Override
    public double getHeight(GeoCoordinates coords, double angularResolution) {
        //TODO System.err.println("HeightMap: requesting height of " + coords + " with resolution "
        //        + resolution);
        
        SRTMTileName index = new SRTMTileName(toTileIndex(coords.getLongitude()), 
                toTileIndex(coords.getLatitude()));
        SRTMTile tile = srtm.requestObject(index, srtmListener).value;
        
        if (tile != null) {
            int lod = 0;
            while (lod < 10 && angularResolution > lodResolutions[lod]) {
                ++lod;
            }
            short[][] values = tile.getTile(lod);
            double x = getTileOffset(coords.getLongitude()) * values.length,
                   y = getTileOffset(coords.getLatitude()) * values.length;
            
            int leftIndex = (int) floor(x), rightIndex = min(values.length-1, leftIndex+1), 
               bottomIndex = (int) floor(y), topIndex = min(values.length-1, bottomIndex+1);
            short topLeft = values[topIndex][leftIndex],
                  topRight = values[bottomIndex][rightIndex], 
                  bottomLeft = values[bottomIndex][leftIndex], 
                  bottomRight = values[bottomIndex][rightIndex];
            
            double rightFraction = (x - floor(x)), bottomFraction = (y - floor(y)), 
                   leftFraction = 1 - rightFraction, topFraction = 1 - bottomFraction;
            
            double interpolated = (topLeft * leftFraction + topRight * rightFraction) * topFraction
                    + (bottomLeft * leftFraction + bottomRight * rightFraction) * bottomFraction;
            
            return interpolated / EARTH_RADIUS_METERS;
        } else {
            return 0;
        }
    }
    
    /**
     * Adds a {@link SurfaceListener} that distributes a notification if the surface was changed.
     * 
     * @param l The new listener
     */
    public void addSurfaceListener(SurfaceListener l) {
        listeners.add(l);
    }

    /**
     * Removes a specific {@link SurfaceListener}.
     * 
     * @param l The listener that should be removed
     */
    public void removeSurfaceListener(SurfaceListener l) {
        while (listeners.remove(l))
            ;
    }

    @Override
    public int hashCode() {
        return "SRTMHeightMap".hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass() == obj.getClass();
    }
    
}
