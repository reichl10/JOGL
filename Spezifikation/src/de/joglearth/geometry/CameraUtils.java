package de.joglearth.geometry;

import static java.lang.Math.PI;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.*;

/**
 * Utility class providing extended {@link Camera} operations.
 */
public final class CameraUtils {

    private CameraUtils() {}

    /**
     * Returns an array of tiles visible or partially visible by the
     * {@link de.joglearth.geometry.Camera}.
     * 
     * All tiles have the same detail level, which is calculated from the distance and number of
     * visible tiles.
     * 
     * @param camera The camera
     * @param zoomLevel The zoom level
     * @return The array of visible tiles
     */
    public static Iterable<Tile> getVisibleTiles(Camera camera, int zoomLevel) {
        /*
         * The tile below (0.5, 0.5) is always visible. Either it it the only tile on the screen, or
         * one of its corners can be used as a starting point by the grid walking algorithm.
         */
        System.out.println(zoomLevel);
        ScreenCoordinates s = new ScreenCoordinates(0.5, 0.5);
        System.out.println("COORDS" + s);
        GeoCoordinates g = camera.getGeoCoordinates(s);
        System.out.println("GEO: " + camera.geometry);
        System.out.println("GEOCOORDS: " + g);
        Tile centerTile = Tile.getContainingTile(zoomLevel,
                camera.getGeoCoordinates(s));
        

        GeoCoordinates[] corners = {
                new GeoCoordinates(centerTile.getLongitudeFrom(), centerTile.getLatitudeFrom()),
                new GeoCoordinates(centerTile.getLongitudeTo(), centerTile.getLatitudeFrom()),
                new GeoCoordinates(centerTile.getLongitudeFrom(), centerTile.getLatitudeTo()),
                new GeoCoordinates(centerTile.getLongitudeTo(), centerTile.getLatitudeTo())
        };

        // Pick an arbitrary corner as the center point (if possible)
        GridPoint center = null;
        for (GeoCoordinates corner : corners) {
            if (camera.isPointVisible(corner)) {
                int lon = centerTile.getLongitudeIndex();
                if (corner.getLongitude() == centerTile.getLongitudeTo()) {
                    ++lon;
                }

                int lat = centerTile.getLatitudeIndex();
                if (corner.getLatitude() == centerTile.getLatitudeTo()) {
                    ++lat;
                }

                center = new GridPoint(lon, lat);
                break;
            }
        }

        ArrayList<Tile> visibleTiles = new ArrayList<Tile>();
        visibleTiles.add(centerTile);

        if (center != null) {
            Set<GridPoint> visiblePoints = getVisibleGridPoints(center, zoomLevel, camera);
            TileWalker walker = new TileWalker(visiblePoints, center, zoomLevel);
            while (walker.step()) {
                visibleTiles.add(walker.getTile());
            }
        }

        return visibleTiles;
    }

    /**
     * Calculates the optimal zoom level so that at least <code>leastHorizontalTiles</code>^2 tiles
     * are displayed
     * 
     * @param camera The current camera
     * @param leastHorizontalTiles The least number of horizontal tiles that should be displayed
     * @return The optimal level of the tiles
     */
    public static int getOptimalZoomLevel(Camera camera, int leastHorizontalTiles) {
        return (int) ceil(log(leastHorizontalTiles / camera.getScale()) / log(2));
    }

    /*
     * Calculates all GridPoints (intersection of longitude and latitude lines) visible from the
     * camera at a given zoom level
     */
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


    // Models an intersection of longitude and latitude lines
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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + lat;
            result = prime * result + lon;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            GridPoint other = (GridPoint) obj;
            if (lat != other.lat)
                return false;
            if (lon != other.lon)
                return false;
            return true;
        }
    }


    // Defines directions for walker classes
    private static final int RIGHT = 0, DOWN = 1, LEFT = 2, UP = 3;


    private static int turnRight(int direction) {
        direction += 1;
        if (direction > 3) {
            direction = 0;
        }
        return direction;
    }

    private static int turnLeft(int direction) {
        direction -= 1;
        if (direction < 0) {
            direction = 3;
        }
        return direction;
    }


    /*
     * Iterates over the border of an area of grid points visible by the camera at a certain zoom
     * level.
     */
    private static class GridWalker {

        private GridPoint pos;
        private int direction;
        private Camera camera;
        private int zoomLevel;
        private double angle;
        private int maxIndex;


        public GridWalker(GridPoint start, int zoomLevel, Camera camera) {
            this.direction = UP;
            this.pos = start;
            this.zoomLevel = zoomLevel;
            this.camera = camera;
            angle = pow(2, -zoomLevel) * PI;
            maxIndex = (int) pow(2, zoomLevel - 1);
        }

        private boolean isGridPointVisible(GridPoint point) {
            int peekLon = point.longitude(), peekLat = point.latitude();

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

            return camera.isPointVisible(new GeoCoordinates(peekLon * angle * 2, peekLat * angle));
        }

        /*
         * Returns a grid point that would be next in line if it is actually visible, else null.
         */
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

            GridPoint peek = new GridPoint(peekLon, peekLat);
            if (isGridPointVisible(peek)) {
                return peek;
            } else {
                return null;
            }
        }

        public void turnLeft() {
            direction = CameraUtils.turnLeft(direction);
        }

        public void turnRight() {
            direction = CameraUtils.turnRight(direction);
        }

        public boolean step() {
            pos = peekPoint();
            return pos != null;
        }

        public GridPoint getPoint() {
            return pos;
        }

    }

    /*
     * Iterates over an area of visible tiles in a spiral way. The visibility of a tile is
     * determined by the visibility of its corners, which is given as a set of grid points.
     */
    private static class TileWalker {

        // The current position
        private int lon, lat;

        // The visible grid points
        private Set<GridPoint> points;
        private int turnsSinceLastTile = 0;
        private int zoomLevel;
        private int direction = RIGHT;

        // The total number of steps to do in the current direction until the next turn.
        private int maxSteps = 1;

        // The number of steps that were performed in this direction so far.
        private int stepNo = 0;

        // The maxSteps need to be increased every second turn
        private boolean firstTurn = true;
        private Tile currentTile = null;


        public TileWalker(Set<GridPoint> points, GridPoint center, int zoomLevel) {
            this.points = points;
            this.lon = center.longitude();
            this.lat = center.latitude();
            this.zoomLevel = zoomLevel;
        }

        public boolean step() {
            assert stepNo < maxSteps;

            currentTile = null;

            // If four turns have been made without finding a tile that is visible, the walker has
            // left the visible area
            while (currentTile == null && turnsSinceLastTile < 4) {
                ++stepNo;
                
                //Make a step
                switch (direction) {
                    case LEFT:
                        lon -= 1;
                        break;
                    case UP:
                        lat += 1;
                        break;
                    case RIGHT:
                        lon += 1;
                        break;
                    case DOWN:
                        lat -= 1;
                        break;
                }

                //The corners of the tile that would be defined by (lon, lat).
                GridPoint[] corners = {
                        new GridPoint(lon, lat),
                        new GridPoint(lon + 1, lat),
                        new GridPoint(lon, lat + 1),
                        new GridPoint(lon + 1, lat + 1)
                };

                //The tile is visible if any corner is visible.
                boolean visible = false;
                for (GridPoint corner : corners) {
                    if (points.contains(corner)) {
                        visible = true;
                        break;
                    }
                }

                if (visible) {
                    currentTile = new Tile(zoomLevel, lon, lat);
                    turnsSinceLastTile = 0;
                }

                //Make a turn if necessary.
                if (stepNo == maxSteps) {
                    ++turnsSinceLastTile;
                    direction = turnRight(direction);
                    if (!firstTurn) {
                        ++maxSteps;
                    }
                    firstTurn = !firstTurn;
                }
            }

            return currentTile != null;
        }

        Tile getTile() {
            return currentTile;
        }
    }

}
