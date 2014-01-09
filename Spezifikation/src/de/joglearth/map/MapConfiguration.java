package de.joglearth.map;

import java.awt.Dimension;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.TileLayout;
import de.joglearth.source.Source;


/**
 * Models the combination of tile layouts and image source forming a valid map configuration.
 */
public interface MapConfiguration {
    /**
     * Determines the optimal tile layout for a camera its viewport size.
     * @param camera The camera. Must not be null
     * @param screenSize The viewport size. Must not be null
     * @return A tile layout compatible with the image data delivered by the source. Must not be
     * null
     */
    TileLayout getOptimalTileLayout(Camera camera, Dimension screenSize);
    
    /**
     * Returns the image source associated with the map configuration
     * @return The image source. Must not be null.
     */
    Source<TileName, byte[]> getImageSource();
    
    /**
     * Returns the file name suffix of the image format delivered by the source, e.g. "jpg".
     * @return The suffix. Must not be null.
     */
    String getImageFormatSuffix();
}
