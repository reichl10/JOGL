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
            TileLayout tileLayout, int maxTiles) {
        if (camera == null || tileLayout == null || maxTiles <= 0) {
            throw new IllegalArgumentException();
        }
        
        Tile centeredTile = tileLayout.getContainingTile(
                camera.getGeoCoordinates(new ScreenCoordinates(0.5, 0.5)));
                
        // Find a grid point to start from, i.e. a visible corner of the centered tile
        GridPoint start = null;        
        if (centeredTile != null) {
            for (GridPoint corner : tileLayout.getTileCorners(centeredTile)) {
                if (camera.isPointVisible(tileLayout.getGeoCoordinates(corner))) {
                    start = corner;
                    break;
                }
            }
        }

        ArrayList<Tile> visibleTiles = new ArrayList<Tile>();
        if (start != null) {
            Set<GridPoint> visiblePoints = getVisibleGridPoints(start, camera, tileLayout);
            TileWalker walker = new TileWalker(visiblePoints, tileLayout.getTileOrigin(centeredTile), 
                    tileLayout);
            // Use a set to skip already added tiles. This may happen if the walker rounds the
            // earth.
            HashSet<Tile> addedTiles = new HashSet<>();
            int i = 0;
            do {
                Tile t = walker.getTile();
                if (!addedTiles.contains(t)) {
                    visibleTiles.add(t);
                    addedTiles.add(t);
                }
            } while (walker.step() && ++i < maxTiles);
        } else if (centeredTile != null) {
            // If no corner is visible, only the centered tile is part of the set
            visibleTiles.add(centeredTile);
        }
        
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
        
        Set<GridPoint> border = new HashSet<GridPoint>();

        // Start at center point
        GridWalker walker = new GridWalker(center, camera, tileLayout);

        // Walk to the border
        while (walker.step());
        GridPoint start = walker.getPoint();
        border.add(start);
        
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
            border.add(p);
            // Until the start point is hit again
        } while (!walker.getPoint().equals(start));
        
        Set<GridPoint> visibleModuloPoints = new HashSet<>();
        for (GridPoint point : border) {
            visibleModuloPoints.add(tileLayout.modulo(point));
        }

        // Fill each line...
        for (int lat = latMin; lat <= latMax; ++lat) {
            int lineMin = lonMin, lineMax = lonMax;
            // Find the left and right border on that line
            while (!border.contains(new GridPoint(lineMin, lat))) {
                ++lineMin;
            }
            while (!border.contains(new GridPoint(lineMax, lat))) {
                --lineMax;
            }

            // Fill everything in between
            for (int lon = lineMin + 1; lon < lineMax; ++lon) {   
                visibleModuloPoints.add(tileLayout.modulo(new GridPoint(lon, lat)));
            }
        }

        return visibleModuloPoints;
    }

    // Defines directions for walker classes
    // TODO Add diagonal directions to prevent missing edges on a zoomed-in globe
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
     * Iterates over an area of grid points visible by the camera.
     */
    public static class GridWalker {

        private GridPoint pos;
        private int direction;
        private Camera camera;
        private int maxLon, maxLat;
        private TileLayout tileLayout;


        public GridWalker(GridPoint start, Camera camera, TileLayout tileLayout) {
            if (start == null || camera == null || tileLayout == null) {
                throw new IllegalArgumentException();
            }
            
            this.direction = UP;
            this.pos = start;
            this.camera = camera;
            maxLon = tileLayout.getHoritzontalTileCount();
            maxLat = tileLayout.getVerticalTileCount();
            this.tileLayout = tileLayout;
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
            
            // Prevent "running in a circle" around the globe by artificially treating points too 
            // far away as invisible
            if (camera.isPointVisible(tileLayout.getGeoCoordinates(peek)) 
                    && peekLon >= -2 * maxLon && peekLon <= 2 * maxLon
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
        
        // The number of steps that were performed in this direction so far.
        private int stepNo = 0;

        // The maxSteps need to be increased every second turn
        private boolean firstTurn = true;
        
        private Tile currentTile = null;
        private TileLayout tileLayout;

        private final int maxMaxSteps;


        public TileWalker(Set<GridPoint> points, GridPoint center, TileLayout tileLayout) {
            if (points == null || center == null || tileLayout == null) {
                throw new IllegalArgumentException();
            }
            
            this.points = points;
            this.lon = center.getLongitude();
            this.lat = center.getLatitude();
            this.currentTile = tileLayout.createTile(center);
            this.tileLayout = tileLayout;
            this.maxMaxSteps = 2*tileLayout.getHoritzontalTileCount();
        }
        
        public boolean step() {
            assert stepNo < maxSteps;
            
            if (currentTile == null) {
                return false;
            }

            Tile nextTile = null;            
            
            // If five turns have been made without finding a tile that is visible, the walker has
            // left the visible area
            while (nextTile == null && turnsSinceLastTile <= 4 && maxSteps <= maxMaxSteps) {
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

                Tile nextTileCandidate = tileLayout.createTile(new GridPoint(lon, lat));
                
                // A tile is visible if any of its corners are visible.
                for (GridPoint corner: tileLayout.getTileCorners(nextTileCandidate)) {
                    GridPoint moduloCorner = tileLayout.modulo(corner);
                    //System.out.print(moduloCorner);
                    if (points.contains(moduloCorner)) {
                        nextTile = nextTileCandidate;
                        turnsSinceLastTile = 0;
                        break;
                    }
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

        public Tile getTile() {
            return currentTile;
        }
    }

}
