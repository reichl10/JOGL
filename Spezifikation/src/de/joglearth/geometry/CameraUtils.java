package de.joglearth.geometry;

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
     * @param detailLevel The zoom level
     * @return The array of visible tiles
     */
    public static Iterable<Tile> getVisibleTiles(Camera camera,
            TileLayout tileLayout) {
        Tile centerTile = tileLayout.getContainingTile(
                camera.getGeoCoordinates(new ScreenCoordinates(0.5, 0.5)));
                
        GridPoint center = null;
        ArrayList<Tile> visibleTiles = new ArrayList<Tile>();
        HashSet<Tile> addedTiles = new HashSet<>();
        
        if (centerTile != null) {
            for (GridPoint corner : centerTile.getCorners()) {
                if (camera.isPointVisible(tileLayout.getGeoCoordinates(corner))) {
                    center = corner;
                    break;
                }
            }
            
            visibleTiles.add(centerTile);
            addedTiles.add(centerTile);
        }

        if (center != null) {
            Set<GridPoint> visiblePoints = getVisibleGridPoints(center, camera, tileLayout);
            TileWalker walker = new TileWalker(visiblePoints, center, tileLayout);
            while (walker.step()) {
                Tile t = walker.getTile();
                if (!addedTiles.contains(t)) {
                    visibleTiles.add(t);
                    addedTiles.add(t);
                }
            }
        }

        System.out.println("\nVisible Tiles:");
        for (Tile t : visibleTiles) {
            System.out.println(t);
        }
        System.out.println();
        return visibleTiles;
    }

    /*
     * Calculates all GridPoints (intersection of longitude and latitude lines) visible from the
     * camera at a given zoom level
     */
    private static Set<GridPoint> getVisibleGridPoints(GridPoint center,
            Camera camera, TileLayout tileLayout) {
        if (center == null || camera == null) {
            throw new IllegalArgumentException();
        }
        
        Set<GridPoint> visiblePoints = new HashSet<GridPoint>();

        // Start at center point
        GridWalker walker = new GridWalker(center, camera, tileLayout);

        // Walk to the border
        while (walker.step());
        GridPoint start = walker.getPoint();
        visiblePoints.add(start);
        
        int lonMin = start.getLongitude(), lonMax = lonMin, latMin = start.getLatitude(), latMax = latMin;

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
            lonMin = min(lonMin, p.getLongitude());
            lonMax = max(lonMax, p.getLongitude());
            latMin = min(latMin, p.getLatitude());
            latMax = max(latMax, p.getLatitude());
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
    public static class GridWalker {

        private GridPoint pos;
        private int direction;
        private Camera camera;
        //private double angle;
        private int maxLon, maxLat;
        //private int horizontalCrossings = 0, verticalCrossings = 0;
        private TileLayout tileLayout;


        public GridWalker(GridPoint start, Camera camera, TileLayout tileLayout) {
            this.direction = UP;
            this.pos = start;
            this.camera = camera;
            //angle = pow(2, -detailLevel) * PI;
            maxLon = tileLayout.getHoritzontalTileCount();
            maxLat = tileLayout.getVerticalTileCount();
            this.tileLayout = tileLayout;
        }

        private boolean isGridPointVisible(GridPoint point) {
            /* int peekLon = point.getLongitude(), peekLat = point.getLatitude();

           
            if (peekLat > maxIndex) {
                peekLat = 2 * maxIndex - peekLat;
            } else if (peekLat < -maxIndex) {
                peekLat = -2 * maxIndex - peekLat;
            }

            if (peekLon > maxIndex) {
                peekLon = 2 * maxIndex - peekLon;
            } else if (peekLon < -maxIndex) {
                peekLon = -2 * maxIndex - peekLon;
            }*/

            return camera.isPointVisible(tileLayout.getGeoCoordinates(point));
        }

        /*
         * Returns a grid point that would be next in line if it is actually visible, else null.
         */
        private GridPoint peekPoint() {
            int peekLon = pos.getLongitude(), peekLat = pos.getLatitude();
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
            if (isGridPointVisible(peek) && peekLon >= -2 * maxLon && peekLon <= 2 * maxLon
                    && peekLat >= -2 * maxLat && peekLat <= 2 * maxLat) {
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
            GridPoint newPos = peekPoint();
            if (newPos != null) {
                pos = newPos;
                return true;
            } else {
                return false;
            }
        }

        public GridPoint getPoint() {
            return pos;
        }

    }

    /*
     * Iterates over an area of visible tiles in a spiral way. The visibility of a tile is
     * determined by the visibility of its corners, which is given as a set of grid points.
     */
    public static class TileWalker {

        // The current position
        private int lon, lat;

        // The visible grid points
        private Set<GridPoint> points;
        private int turnsSinceLastTile = 0;
        private int direction = RIGHT;

        // The total number of steps to do in the current direction until the next turn.
        private int maxSteps = 1;
        private int maxIndex;

        // The number of steps that were performed in this direction so far.
        private int stepNo = 0;

        // The maxSteps need to be increased every second turn
        private boolean firstTurn = true;
        private Tile currentTile = null;
        private TileLayout tileLayout;


        public TileWalker(Set<GridPoint> points, GridPoint center, TileLayout tileLayout) {
            this.points = points;
            this.lon = center.getLongitude();
            this.lat = center.getLatitude();
            this.currentTile = tileLayout.createTile(new GridPoint(index(center.getLongitude()), index(center.getLatitude())));
            this.tileLayout = tileLayout;
            this.maxIndex = tileLayout.getHoritzontalTileCount();
        }

        private int index(int ind) {
            /*int r = ind % maxIndex;
            if (r < 0) {
                r = maxIndex + r;
            }
            return r;           */
            return ind;
        }
        
        public boolean step() {
            assert stepNo < maxSteps;
            
            if (currentTile == null) {
                return false;
            }

            Tile nextTile = null;
            
            // If four turns have been made without finding a tile that is visible, the walker has
            // left the visible area
            while (nextTile == null && turnsSinceLastTile < 4 && maxSteps-1 <= maxIndex) {
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
                    nextTile = tileLayout.createTile(new GridPoint(index(lon), index(maxIndex / 2 - 1 - lat)));
                    turnsSinceLastTile = 0;
                }

                //Make a turn if necessary.
                if (stepNo == maxSteps) {
                    stepNo = 0;
                    ++turnsSinceLastTile;
                    direction = turnRight(direction);
                    if (!firstTurn) {
                        ++maxSteps;
                    }
                    firstTurn = !firstTurn;
                }
            }
            
            if (nextTile != null) {
                currentTile = nextTile;
                return true;
            } else {
                return false;
            }
            
        }

        Tile getTile() {
            return currentTile;
        }
    }

}
