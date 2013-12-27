package de.joglearth.source.tiles.osm;

import java.awt.Dimension;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.Tile;
import de.joglearth.geometry.TileLayout;
import de.joglearth.source.Source;
import de.joglearth.source.caching.PathTranslator;
import de.joglearth.source.tiles.TileName;


public interface MapConfiguration {
    TileLayout getOptimalTileLayout(Camera camera, Dimension screenSize);
    
    Source<TileName, byte[]> getImageSource();
}
