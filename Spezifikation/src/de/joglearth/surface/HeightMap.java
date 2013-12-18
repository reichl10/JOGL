package de.joglearth.surface;

import java.util.ArrayList;
import java.util.List;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.source.SourceListener;
import de.joglearth.source.srtm.SRTMTile;
import de.joglearth.source.srtm.SRTMTileIndex;
import de.joglearth.source.srtm.SRTMTileManager;
import static java.lang.Math.*;


/**
 * Static class for the interpolation of information about the height of points displayed on the
 * map. For that purpose SRTM height data is used. A {@link de.joglearth.rendering.Tessellator} uses
 * this class to generate a map surface by the {@link HeightMap}.
 */
public final class HeightMap {

    private final static SRTMListener srtmListener = new SRTMListener();
    private final static List<SurfaceListener> listeners = new ArrayList<>();
    private final static SRTMTileManager srtm = SRTMTileManager.getInstance();
    private final static double[] lodResolutions = { 1.2110e-08, 2.4221e-08, 4.8441e-08,
            9.6882e-08, 1.9376e-07, 3.8753e-07, 7.7506e-07, 1.5501e-06, 3.1002e-06, 6.2004e-06,
            1.2401e-05 };
    
    public final static double EARTH_RADIUS_METERS = 6378000.0;
    public final static double MAX_HEIGHT = Short.MAX_VALUE / EARTH_RADIUS_METERS;
    public final static double MIN_HEIGHT = -MAX_HEIGHT;


    private static class SRTMListener implements SourceListener<SRTMTileIndex, SRTMTile> {

        @Override
        public void requestCompleted(SRTMTileIndex key, SRTMTile value) {
            for (SurfaceListener l : listeners) {
                l.surfaceChanged(key.longitude, key.latitude, key.longitude + 1, key.latitude + 1);
            }
        }
    }


    private HeightMap() {}

    private static int toTileIndex(double rad) {
        return (int) floor(rad / PI * 180);
    }

    private static double getTileOffset(double angle) {
        return (abs(angle) / PI * 180) % 1;
    }

    /**
     * Tries to determine the height of a point using the SRTM data that contains its
     * {@link de.joglearth.geometry.GeoCoordinates} or returns default <code>0</code> if no value
     * was found.
     * 
     * @param coords The <code>GeoCoordinates</code> of the point
     * @param resolution The angle step size between two points in the constructed surface.
     * @return The height of the requested point, <code>0</code> if the height of the point is not
     *         available (yet).
     */
    public static double getHeight(GeoCoordinates coords, double resolution) {
        System.err.println("HeightMap: requesting height of " + coords + " with resolution "
                + resolution);
        
        SRTMTileIndex index = new SRTMTileIndex(toTileIndex(coords.getLongitude()), 
                toTileIndex(coords.getLatitude()));
        SRTMTile tile = srtm.requestObject(index, srtmListener).value;
        
        if (tile != null) {
            int lod = 0;
            while (resolution > lodResolutions[lod] && lod <= 10) {
                ++lod;
            }
            short[][] values = tile.getTile(lod);
            double x = getTileOffset(coords.getLongitude()) * values.length,
                   y = getTileOffset(coords.getLatitude()) * values.length;
            
            int xIndex = (int) floor(x), yIndex = (int) floor(y);
            short topLeft = values[yIndex][xIndex],
                  topRight = values[yIndex][xIndex + 1], 
                  bottomLeft = values[yIndex + 1][xIndex], 
                  bottomRight = values[yIndex + 1][xIndex + 1];
            
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
    public static void addSurfaceListener(SurfaceListener l) {
        listeners.add(l);
    }

    /**
     * Removes a specific {@link SurfaceListener}.
     * 
     * @param l The listener that should be removed
     */
    public static void removeSurfaceListener(SurfaceListener l) {
        while (listeners.remove(l))
            ;
    }

    public static void main(String[] args) {
        System.out.println(getTileOffset(-80.2 * PI / 180));
    }
}
