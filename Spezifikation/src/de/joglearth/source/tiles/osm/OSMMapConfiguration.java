package de.joglearth.source.tiles.osm;

import java.awt.Dimension;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.Tile;
import de.joglearth.geometry.TileLayout;
import de.joglearth.source.Source;
import de.joglearth.source.caching.PathTranslator;
import de.joglearth.source.tiles.TileName;


public class OSMMapConfiguration implements MapConfiguration {

    private OSMMapType mapType;
    
    public OSMMapConfiguration(OSMMapType mapType) {
        this.mapType = mapType;
    }
    
    @Override
    public TileLayout getOptimalTileLayout(Camera camera, Dimension screenSize) {
        return new OSMTileLayout(OSMTileLayout.getOptimalzoomLevel(camera, screenSize.width/256));
    }
    
    @Override
    public Source<TileName, byte[]> getImageSource() {
        return OSMTileManager.getInstance();
    }
    
    public OSMMapType getMapType() {
        return mapType;
    }

    /* (nicht-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mapType == null) ? 0 : mapType.hashCode());
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
        OSMMapConfiguration other = (OSMMapConfiguration) obj;
        if (mapType != other.mapType) {
            return false;
        }
        return true;
    }

}
