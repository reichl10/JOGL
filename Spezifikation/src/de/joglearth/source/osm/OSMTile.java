package de.joglearth.source.osm;

import de.joglearth.geometry.Tile;
import de.joglearth.surface.TiledMapType;

/**
 * Aggregate of geometric tile information and {@link TiledMapType}.
 */
public final class OSMTile {
    
    public Tile tile;
    public TiledMapType type;
    
    /**
     * Constructor. Assigns values to the {@link Tile} and the {@link TiledMapType} attribute.
     * @param tile The value of the <code>Tile</code>
     * @param type The <code>TiledMapType</code> of the {@link OSMTile}
     */
    public OSMTile (Tile tile, TiledMapType type) {
        
    }

}
