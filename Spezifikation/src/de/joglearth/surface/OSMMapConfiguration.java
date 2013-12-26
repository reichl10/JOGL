package de.joglearth.surface;

import java.awt.Dimension;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.OSMTileLayout;
import de.joglearth.geometry.Tile;
import de.joglearth.geometry.TileLayout;
import de.joglearth.source.Source;
import de.joglearth.source.TileName;
import de.joglearth.source.caching.PathTranslator;
import de.joglearth.source.osm.OSMTileManager;
import de.joglearth.source.osm.OSMTileSource;


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

}
