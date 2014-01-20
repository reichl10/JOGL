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
    private final List<SurfaceListener> listeners = new ArrayList<>();
    private final static SRTMTileManager srtm = SRTMTileManager.getInstance();
    private final static double[] lodResolutions = { 1.2110e-08, 2.4221e-08, 4.8441e-08,
            9.6882e-08, 1.9376e-07, 3.8753e-07, 7.7506e-07, 1.5501e-06, 3.1002e-06, 6.2004e-06,
            1.2401e-05 };
    
    public final static double EARTH_RADIUS_METERS = 6378000.0;
    public final static double MAX_HEIGHT = Short.MAX_VALUE / EARTH_RADIUS_METERS;
    public final static double MIN_HEIGHT = -MAX_HEIGHT;


    private class SRTMListener implements SourceListener<SRTMTileName, SRTMTile> {

        private double degToRad(double deg) {
            return (deg/180)*PI;
        }
        
        @Override
        public void requestCompleted(SRTMTileName key, SRTMTile value) {
            for (SurfaceListener l : listeners) {
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
    public synchronized double getHeight(GeoCoordinates coords, double angularResolution) {
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
            System.out.println("AngularRes " + angularResolution);
            short[][] values = tile.getTile(lod);
            double x = getTileOffset(coords.getLongitude()) * values.length,
                   y = getTileOffset(coords.getLatitude()) * values.length;
            
            int leftIndex = (int) floor(x), rightIndex = min(values.length-1, leftIndex+1), 
                topIndex = (int) floor(y), bottomIndex = min(values.length-1, topIndex+1);
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
    public synchronized void addSurfaceListener(SurfaceListener l) {
        listeners.add(l);
    }

    /**
     * Removes a specific {@link SurfaceListener}.
     * 
     * @param l The listener that should be removed
     */
    public synchronized void removeSurfaceListener(SurfaceListener l) {
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
