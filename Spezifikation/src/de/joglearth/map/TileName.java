package de.joglearth.map;

import de.joglearth.geometry.Tile;


/**
 * Structure combining a map configuration with a geometric tile, thus identifying a tile texture
 * uniquely.
 */
public final class TileName {

    public final MapConfiguration configuration;
    public final Tile tile;
    
    /**
     * Constructor.
     * @param configuration The map configuration. Must not be null
     * @param tile The tile. Must not be null
     */
    public TileName(MapConfiguration configuration, Tile tile) {
        if (configuration == null || tile == null) {
            throw new IllegalArgumentException();
        }
        
        this.configuration = configuration;
        this.tile = tile;
    }

    /* (nicht-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((configuration == null) ? 0 : configuration.hashCode());
        result = prime * result + ((tile == null) ? 0 : tile.hashCode());
        return result;
    }

    /* (nicht-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
        TileName other = (TileName) obj;
        if (configuration == null) {
            if (other.configuration != null) {
                return false;
            }
        } else if (!configuration.equals(other.configuration)) {
            return false;
        }
        if (tile == null) {
            if (other.tile != null) {
                return false;
            }
        } else if (!tile.equals(other.tile)) {
            return false;
        }
        return true;
    }
    
}
