package de.joglearth.source.osm;

import de.joglearth.geometry.Tile;
import de.joglearth.surface.TiledMapType;


/**
 * Aggregate of geometric tile information and {@link de.joglearth.surface.TiledMapType}.
 */
public final class OSMTile {

    @Override
    public String toString() {
        return "OSMTile [tile=" + tile + ", type=" + type + "]";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tile == null) ? 0 : tile.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OSMTile other = (OSMTile) obj;
        if (tile == null) {
            if (other.tile != null) {
                return false;
            }
        } else if (!tile.equals(other.tile)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }


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
        this.tile = tile;
        this.type = type;
    }

}
