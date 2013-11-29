package de.joglearth.source.osm;

import de.joglearth.geometry.Tile;
import de.joglearth.surface.TiledMapType;


/**
 * Aggregate of geometric tile information and {@link de.joglearth.surface.TiledMapType}.
 */
public final class OSMTile {

    /**
     * A tile.
     */
    public Tile         tile;

    /**
     * The map type.
     */
    public TiledMapType type;


    /**
     * Constructor. Assigns values to the {@link Tile} and the
     * {@link de.joglearth.surface.TiledMapType} attribute.
     * 
     * @param tile The value of the <code>Tile</code>
     * @param type The <code>TiledMapType</code> of the <code>OSMTile</code>
     */
    public OSMTile(Tile tile, TiledMapType type) {

    }

}
