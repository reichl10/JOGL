package de.joglearth.source;

import javax.media.opengl.GL2;

import de.joglearth.geometry.Tile;


/**
 * Loads the textures into the OpenGL object where each texture gets it own ID and implements
 * {@link Source} to get a new texture, when it is needed. Owns a {@link Source}.
 */
public class TextureSource implements Source<Tile, Integer> {

    private GL2                  gl;
    private Source<Tile, byte[]> imageSource;

    @Override
    public SourceResponse<Integer> requestObject(Tile key,
            SourceListener<Tile, Integer> sender) {
        // Textur in OpenGL laden
        return null;
    }
}