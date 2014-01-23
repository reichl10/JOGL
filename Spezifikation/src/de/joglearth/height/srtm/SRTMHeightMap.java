package de.joglearth.height.srtm;

import static java.lang.Math.PI;
import static java.lang.Math.floor;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.SurfaceListener;
import de.joglearth.height.HeightMap;
import de.joglearth.source.SourceListener;

/**
 * Singleton class that returns certain values for height requests.
 * 
 */
public class SRTMHeightMap implements HeightMap {

    private static SRTMHeightMap instance = null;
    
    /**
     * The radius of the earth given in meters.
     */
    public final static double EARTH_RADIUS_METERS = 6378000.0;
    
    /**
     * The maximal height of a point on the sphere.
     */
    public final static double MAX_HEIGHT = Short.MAX_VALUE / EARTH_RADIUS_METERS;
    
    /**
     * The minimal height of a point on the sphere.
     */
    public final static double MIN_HEIGHT = -MAX_HEIGHT;
    
    private final SRTMListener srtmListener = new SRTMListener();
    private final ArrayList<SurfaceListener> listeners = new ArrayList<>();
    private final static SRTMTileManager srtm = SRTMTileManager.getInstance();
    private final static double[] lodResolutions = { 1.7044e-05, 3.4088e-05, 6.8177e-05, 
        1.3635e-04, 2.7271e-04, 5.4542e-04, 1.0908e-03, 2.1817e-03, 4.3633e-03, 8.7266e-03,
        1.7453e-02 };
    
    
    private static final class SRTMNameTilePair {
        public final SRTMTileName name;
        public final SRTMTile tile;
        
        public SRTMNameTilePair(SRTMTileName name, SRTMTile tile) {
            this.name = name;
            this.tile = tile;
        }
        
        @Override
        public boolean equals(Object other) {
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            SRTMNameTilePair pair = (SRTMNameTilePair) other;
            return this.name.equals(pair.name) && this.tile == pair.tile;
        }
    }
    
    SRTMNameTilePair lastTile = null, nextToLastTile = null;
    
    private synchronized SRTMTile getSRTMTile(SRTMTileName tileName) {
        if (lastTile == null || !tileName.equals(lastTile.name)) {
            if (nextToLastTile != null && tileName.equals(nextToLastTile.name)) {
                SRTMNameTilePair temp = lastTile;
                lastTile = nextToLastTile;
                nextToLastTile = temp;
            } else {
                nextToLastTile = lastTile;
                lastTile = new SRTMNameTilePair(tileName, 
                        srtm.requestObject(tileName, srtmListener).value);
            }
        }
        return lastTile.tile;
    }
    

    private class SRTMListener implements SourceListener<SRTMTileName, SRTMTile> {

        private double degToRad(double deg) {
            return (deg/180)*PI;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void requestCompleted(SRTMTileName key, SRTMTile value) {
            ArrayList<SurfaceListener> listenersClone;
            synchronized (SRTMHeightMap.this) {
                lastTile = null;
                nextToLastTile = null;
                listenersClone = (ArrayList<SurfaceListener>) listeners.clone();
            }
            for (SurfaceListener l : listenersClone) {
                l.surfaceChanged(degToRad(key.longitude), degToRad(key.latitude),
                        degToRad(key.longitude + 1), degToRad(key.latitude + 1));
            }
        }
    }


    private SRTMHeightMap() {}
    
    /**
     * Returns the instance of the class or creates it, if it does not exist yet.
     * 
     * @return The instance of <code>SRTMHeightMap</code>
     */
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
        if (angle < 0) {
            angle += 2*PI;
        }
        
        return (angle / PI * 180) % 1;
    }

    @Override
    public double getHeight(GeoCoordinates coords, double angularResolution) {
        
        SRTMTileName index = new SRTMTileName(toTileIndex(coords.getLongitude()), 
                toTileIndex(coords.getLatitude()));
        SRTMTile tile = getSRTMTile(index);
        
        if (tile != null) {
            int lod = 0;
            while (lod < 10 && angularResolution > lodResolutions[lod]) {
                ++lod;
            }
            short[][] values = tile.getTile(lod);
            double x = getTileOffset(coords.getLongitude()) * values.length,
                   y = getTileOffset(coords.getLatitude()) * values.length;
            
            int leftIndex = (int) floor(x), rightIndex = min(values.length-1, leftIndex+1), 
               bottomIndex = values.length - 1 - (int) floor(y), topIndex = max(0, bottomIndex-1);
            short topLeft = values[topIndex][leftIndex],
                  topRight = values[topIndex][rightIndex], 
                  bottomLeft = values[bottomIndex][leftIndex], 
                  bottomRight = values[bottomIndex][rightIndex];
            
            double rightFraction = (x - floor(x)), bottomFraction = 1 - (y - floor(y)), 
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
