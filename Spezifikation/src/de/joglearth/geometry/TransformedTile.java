package de.joglearth.geometry;

/**
 *  //TODO
 *
 */
public final class TransformedTile {

    //TODO: JavaDoc
    public final Tile tile;
    
    //TODO: JavaDoc
    public final Matrix4 transformation;


    /**
     * //TODO
     * 
     * @param tile
     * @param transformation
     */
    public TransformedTile(Tile tile, Matrix4 transformation) {
        this.tile = tile;
        this.transformation = transformation;
    }

}
