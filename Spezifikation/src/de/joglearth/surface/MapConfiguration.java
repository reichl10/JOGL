package de.joglearth.surface;

import java.awt.Dimension;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.Tile;
import de.joglearth.geometry.TileLayout;
import de.joglearth.source.Source;
import de.joglearth.source.TileName;
import de.joglearth.source.caching.PathTranslator;


public interface MapConfiguration {
    TileLayout getOptimalTileLayout(Camera camera, Dimension screenSize);
    
    Source<TileName, byte[]> getImageSource();
}
