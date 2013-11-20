package de.joglearth.source;

import javax.media.opengl.GL2;

import de.joglearth.geometry.Tile;


/**
 * The class TextureSource loads the textures into the OpenGL object. Each texture gets it own ID.
 * The class implements the interface Source to get a new texture, when it is needed. So the
 * TextureSource has a own Source.
 * 
 */
public class TextureSource implements Source<Tile, Integer> {

    private GL2                  gl;
    private Source<Tile, byte[]> imageSource;

    /**
	 * 
	 */
    @Override
    public SourceResponse<Integer> requestObject(Tile key,
            SourceListener<Tile, Integer> sender) {
        // Textur in OpenGL laden
        return null;
    }
    
}
