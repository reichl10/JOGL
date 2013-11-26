package de.joglearth.source.opengl;

import javax.media.opengl.GL2;

import de.joglearth.geometry.Tile;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;


/**
 * Loads the textures into OpenGL returning the ID. Implements {@link Source} to get a new texture,
 * when it is needed. Owns a {@link Source} for image data.
 */
public class TileTextureSource implements Source<Tile, Integer> {

    private GL2                  gl;
    private Source<Tile, byte[]> imageSource;


    @Override
    public SourceResponse<Integer> requestObject(Tile key,
            SourceListener<Tile, Integer> sender) {
        // Textur in OpenGL laden
        return null;
    }
}
