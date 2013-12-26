package de.joglearth.source;

import java.util.HashMap;
import java.util.Map;

import de.joglearth.geometry.SingleTile;
import de.joglearth.surface.SingleMapConfiguration;
import de.joglearth.surface.SingleMapType;
import de.joglearth.util.Resource;


public final class SingleTileManager implements Source<TileName, byte[]> {

    private Map<SingleMapType, byte[]> images;
    
    private SingleTileManager() {
        images = new HashMap<>();
        for (SingleMapType type : SingleMapType.values()) {
            images.put(type, Resource.loadBinary("singleMapTextures/" + type.name() + ".jpg"));
        }
    }
    
    private static SingleTileManager instance = null;
    
    public static SingleTileManager getInstance() {
        if (instance == null) {
            instance = new SingleTileManager();
        }
        return instance;
    }
    
    @Override
    public SourceResponse<byte[]> requestObject(TileName key,
            SourceListener<TileName, byte[]> sender) {
        byte[] result = null;
        if (key.tile instanceof SingleTile
                && key.configuration instanceof SingleMapConfiguration) {
            result = images.get(((SingleMapConfiguration) key.configuration).getMapType());
        }
        
        if (result != null) {
            return new SourceResponse<>(SourceResponseType.SYNCHRONOUS, result);
        } else {
            return new SourceResponse<>(SourceResponseType.MISSING, null);        
        }
    }

    @Override
    public void dispose() {

    }

}
