package de.joglearth.surface;

import java.awt.Dimension;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.SingleTileLayout;
import de.joglearth.geometry.TileLayout;
import de.joglearth.source.SingleTileManager;
import de.joglearth.source.Source;
import de.joglearth.source.TileName;


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

}
