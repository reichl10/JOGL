package de.joglearth.geometry;


public final class TransformedTile {
    
    public final Tile tile;
    
    public final Matrix4 transformation;
    
    public TransformedTile(Tile tile, Matrix4 transformation) {
        this.tile = tile;
        this.transformation = transformation;
    }

}
