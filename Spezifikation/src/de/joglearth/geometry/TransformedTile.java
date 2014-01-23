package de.joglearth.geometry;

/**
 * Structure that creates a tile that contains a certain transformation matrix. It is used for
 * the different transformations that exist.
 */
public final class TransformedTile {
    
    /**
     * The tile that is given in a certain transformation.
     */
    public final Tile tile;
    
    /**
     * The transformation matrix that describes the transformation of the tile.
     */
    public final Matrix4 transformation;


    /**
     * Creates a {@link TransformedTile} as it assigns values to the attributes of the tile.
     * 
     * @param tile The tile that is given under a transformation
     * @param transformation The transformation matrix of the tile
     */
    public TransformedTile(Tile tile, Matrix4 transformation) {
        this.tile = tile;
        this.transformation = transformation;
    }

}
