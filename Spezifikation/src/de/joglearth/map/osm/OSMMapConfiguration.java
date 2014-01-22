package de.joglearth.map.osm;

import java.awt.Dimension;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.MapProjection;
import de.joglearth.geometry.MercatorProjection;
import de.joglearth.geometry.TileLayout;
import de.joglearth.map.MapConfiguration;
import de.joglearth.map.TileName;
import de.joglearth.source.Source;


/**
 * Implements the {@link MapConfiguration} interface to work with different OpenStreetMap map types.
 */
public class OSMMapConfiguration implements MapConfiguration {

    private final OSMMapType mapType;
    
    /**
     * Constructor. 
     * @param mapType The map type. Must not be null
     */
    public OSMMapConfiguration(OSMMapType mapType) {
        if (mapType == null) {
            throw new IllegalArgumentException();
        }
        
        this.mapType = mapType;
    }
    
    @Override
    public TileLayout getOptimalTileLayout(Camera camera, Dimension screenSize) {
        return new OSMTileLayout(OSMTileLayout.getOptimalZoomLevel(camera, screenSize.width/256));
    }
    
    @Override
    public Source<TileName, byte[]> getImageSource() {
        return OSMTileManager.getInstance();
    }
    
    /**
     * Returns the map type.
     * @return The map type, as passed to the constructor
     */
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

    @Override
    public String getImageFormatSuffix() {
        return OSMTileSource.getImageFormatSuffix(mapType);
    }

    @Override
    public MapProjection getProjection() {
        return new MercatorProjection();
    }

    @Override
    public double getMinimumCameraDistance() {
        return 1e-5;
    }
}