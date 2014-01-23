package de.joglearth.geometry;


/**
 * Describes the layout of tiles onto an infinite grid of {@link GridPoints}.
 * 
 * A TileLayout usually has a corresponding {@link Tile} implementation it applies to.
 * How GridPoints are mapped to Tiles is up to the implementation, as long as the methods are
 * consistent with one another and neighbor relationships between tiles are represented as 
 * neighboring GridPoints.
 */
public interface TileLayout {
    
    /**
     * Constructs a Tile identified by a corner GridPoint. Which corner that is is up to the 
     * implementation, as long as the choice is consistent (i.e. always the bottom-left corner).
     * A GridPoint must always map to a tile, this may be enforced by adequate modulo operations.
     * @param origin One of the tile's corners. Must not be null
     * @return The tile. Must not be null
     */
    Tile createTile(GridPoint origin);
    
    /**
     * Constructs a Tile containing given {@link GeoCoordinates}. Every pair of coordinates must 
     * map to exactly one tile.
     * @param geo The coordinates. Must not be null
     * @return The tile. Must not be null
     */
    Tile getContainingTile(GeoCoordinates geo);
    
    /**
     * Returns the {@link GeoCoordinates} mapping to the {@link GridPoint} provided.
     * @param point The grid point. Must not be null
     * @return The coordinates. Must not be null
     */
    GeoCoordinates getGeoCoordinates(GridPoint point);    
    
    /**
     * Returns the maximum number of different tiles with the same origin latitude. 
     * @return The number of tiles
     */
    int getHoritzontalTileCount();

    /**
     * Returns the maximum number of different tiles with the same origin longitude. 
     * @return The number of tiles
     */
    int getVerticalTileCount();

    /**
     * Returns a grid point that can be used to construct the tile given.
     * <code>createTile(getTileOrigin(tile)).equals(tile)</code> must hold for all valid tiles.
     * @param tile The tile. Must not be null
     * @return The origin grid point
     * @throws IllegalArgumentException The passed tile was not of a type used by the layout
     */
    GridPoint getTileOrigin(Tile tile);
    
    /**
     * Returns all grid points contained in the tile's border. One of these grid points must be
     * the tile origin as returned by <code>getTileOrigin()</code>.
     * @param tile The tile. Must not be null
     * @return An array of corner grid points. Must not be null
     * @throws IllegalArgumentException The passed tile was not of a type used by the layout
     */
    GridPoint[] getTileCorners(Tile tile);
    
    /**
     * Maps all equal grid points to the same exact point.
     * Usually tile layouts are periodical and repeating mirrored at corners to correctly map to
     * an infinite grid of points. A modulo operation on two equal points on different repetitions
     * of the map thus yields the same point.
     *
     * @param point The point. Must not be null
     * @return The unique representation of the point. Must not be null
     */
    GridPoint modulo(GridPoint point);
}
