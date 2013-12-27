package de.joglearth.source.tiles.single;

import java.awt.Dimension;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.TileLayout;
import de.joglearth.source.Source;
import de.joglearth.source.tiles.TileName;
import de.joglearth.source.tiles.osm.MapConfiguration;


public class SingleMapConfiguration implements MapConfiguration {

    private SingleMapType mapType;
    
    public SingleMapConfiguration(SingleMapType mapType) {
        this.mapType = mapType;
    }
    
    @Override
    public TileLayout getOptimalTileLayout(Camera camera, Dimension screenSize) {
        return new SingleTileLayout();
    }
    
    @Override
    public Source<TileName, byte[]> getImageSource() {
        return SingleTileManager.getInstance();
    }
    
    public SingleMapType getMapType() {
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
        SingleMapConfiguration other = (SingleMapConfiguration) obj;
        if (mapType != other.mapType) {
            return false;
        }
        return true;
    }

}
