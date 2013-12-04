package de.joglearth.geometry;

import static java.lang.Math.PI;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public final class CameraUtils {

    private CameraUtils() {}

    /**
     * Returns an array of tiles visible or partially visible by the
     * {@link de.joglearth.geometry.Camera}.
     * 
     * All tiles have the same detail level, which is calculated from the distance and number of
     * visible tiles.
     * 
     * @return The array of visible tiles
     */
    public static Iterable<Tile> getVisibleTiles(Camera camera) {
        int zoomLevel = getOptimalZoomLevel(camera);

        /*
         * The tile below (0.5, 0.5) is always visible. Either it it the only tile on the screen, or
         * one of its corners can be used as a starting point by the grid walking algorithm.
         */
        Tile centerTile = Tile.getContainingTile(zoomLevel,
                camera.getGeoCoordinates(new ScreenCoordinates(0.5, 0.5)));

        GeoCoordinates[] corners = {
                new GeoCoordinates(centerTile.longitudeFrom(), centerTile.latitudeFrom()),
                new GeoCoordinates(centerTile.longitudeTo(), centerTile.latitudeFrom()),
                new GeoCoordinates(centerTile.longitudeFrom(), centerTile.latitudeTo()),
                new GeoCoordinates(centerTile.longitudeTo(), centerTile.latitudeTo())
        };

        // Pick an arbitrary corner as the center point if (possible)
        GridPoint center = null;
        for (GeoCoordinates corner : corners) {
            if (camera.isPointVisible(corner)) {
                int lon = centerTile.getLongitudeIndex();
                if (corner.getLongitude() == centerTile.longitudeTo()) {
                    ++lon;
                }

                int lat = centerTile.getLatitudeIndex();
                if (corner.getLatitude() == centerTile.latitudeTo()) {
                    ++lat;
                }

                center = new GridPoint(lon, lat);
                break;
            }
        }

        ArrayList<Tile> visibleTiles = new ArrayList<Tile>();

        if (center == null) {
            // If all corners are invisible, only the center tile is on the screen
            visibleTiles.add(centerTile);
        } else {
            Set<GridPoint> visiblePoints = getVisibleGridPoints(center, zoomLevel, camera);
        }

        return visibleTiles;
    }

    
    public static int getOptimalZoomLevel(Camera camera) {
        return 0;
    }
    
    
    public static double getScale(Camera camera) {
        return 0;
    }
    

    private static Set<GridPoint> getVisibleGridPoints(GridPoint center, int zoomLevel,
            Camera camera) {
        Set<GridPoint> visiblePoints = new HashSet<GridPoint>();

        // Start at center point
        GridWalker walker = new GridWalker(center, zoomLevel, camera);

        // Walk to the border
        while (walker.step())
            ;
        GridPoint start = walker.getPoint();
        visiblePoints.add(start);
        int lonMin = start.longitude(), lonMax = lonMin, latMin = start.latitude(), latMax = latMin;

        // Step "onto the border"
        walker.turnRight();
        do {
            // Try turning outside if possible, else try stepping straight, turning right or back
            walker.turnLeft();
            int turns = 0;
            while (turns < 4 && !walker.step()) {
                walker.turnRight();
                ++turns;
            }

            // If there's nowhere to go (Only a single visible point), stop
            if (turns == 4) {
                break;
            }

            // Find minimum and maximum to get a surrounding rectangle
            GridPoint p = walker.getPoint();
            lonMin = min(lonMin, p.longitude());
            lonMax = max(lonMax, p.longitude());
            latMin = min(latMin, p.latitude());
            latMax = max(latMax, p.latitude());
            visiblePoints.add(p);
            // Until the start point is hit again
        } while (!walker.getPoint().equals(start));

        // Fill each line...
        for (int lat = latMin; lat <= latMax; ++lat) {
            int lineMin = lonMin, lineMax = lonMax;
            // Find the left and right border on that line
            while (!visiblePoints.contains(new GridPoint(lineMin, lat))) {
                ++lineMin;
            }
            while (!visiblePoints.contains(new GridPoint(lineMax, lat))) {
                --lineMax;
            }

            // Fill everything inbetween
            for (int lon = lineMin + 1; lon < lineMax; ++lon) {
                visiblePoints.add(new GridPoint(lon, lat));
            }
        }

        return visiblePoints;
    }


    private static final class GridPoint {

        private int lon, lat;


        public int longitude() {
            return lon;
        }

        public int latitude() {
            return lat;
        }

        public GridPoint(int lon, int lat) {
            this.lon = lon;
            this.lat = lat;
        }

        public boolean equals(GridPoint other) {
            return lon == other.lon && lat == other.lat;
        }
    }

    private static class GridWalker {

        private final int RIGHT = 0, DOWN = 1, LEFT = 2, UP = 3;
        private GridPoint pos;
        private int direction, maxIndex;
        private double angle;
        private Camera camera;


        public GridWalker(GridPoint start, int zoomLevel, Camera camera) {
            this.direction = UP;
            this.pos = start;
            this.angle = pow(2, -zoomLevel) * PI;
            this.maxIndex = (int) pow(2, zoomLevel - 1);
            this.camera = camera;
        }

        private GridPoint peekPoint() {
            int peekLon = pos.longitude(), peekLat = pos.latitude();
            switch (direction) {
                case RIGHT:
                    peekLon += 1;
                    break;
                case DOWN:
                    peekLat -= 1;
                    break;
                case LEFT:
                    peekLon -= 1;
                    break;
                case UP:
                    peekLat += 1;
                    break;
            }
            if (peekLat > maxIndex) {
                peekLat = 2 * maxIndex - peekLat;
            } else if (peekLat < -maxIndex) {
                peekLat = -2 * maxIndex - peekLat;
            }

            if (peekLon > maxIndex) {
                peekLon = 2 * maxIndex - peekLon;
            } else if (peekLon < -maxIndex) {
                peekLon = -2 * maxIndex - peekLon;
            }

            if (camera.isPointVisible(new GeoCoordinates(peekLon * angle * 2, peekLat * angle))) {
                return new GridPoint(peekLon, peekLat);
            } else {
                return null;
            }
        }

        public void turnLeft() {
            direction -= 1;
            if (direction < 0) {
                direction = 3;
            }
        }

        public void turnRight() {
            direction += 1;
            if (direction > 3) {
                direction = 0;
            }
        }

        public boolean step() {
            pos = peekPoint();
            return pos != null;
        }

        public GridPoint getPoint() {
            return pos;
        }

    }

    private static class TileWalker {

        private GridPoint index;
        private Set<GridPoint> points;
        private int turnsSinceLastTile = 0;
        private int zoomLevel;


        public TileWalker(Set<GridPoint> points, GridPoint center, int zoomLevel) {
            this.points = points;
            this.index = center;
            this.zoomLevel = zoomLevel;
        }

        boolean step() {
            return false;
        }

        Tile getTile() {
            return new Tile(zoomLevel, index.longitude(), index.latitude());
        }
    }
    
    

}
