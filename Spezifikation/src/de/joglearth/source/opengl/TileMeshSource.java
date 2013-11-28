package de.joglearth.source.opengl;

import javax.media.opengl.GL2;

import de.joglearth.geometry.Tile;
import de.joglearth.rendering.Tessellator;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;


/**
 * Adapter for a {@link de.joglearth.rendering.Tessellator} to use it as a
 * {@link de.joglearth.source.Source}.
 */
public class TileMeshSource implements Source<Tile, Integer> {

    private Tessellator tess;
    private GL2         gl;
    private int         subdivisions;
    private boolean     heightMap;


    /**
     * Constructor. Initializes the {@link de.joglearth.source.opengl.TileMeshSource} as it assign
     * values to its GL context and {@link de.joglearth.rendering.Tesselator}.
     * 
     * @param gl The GL context of <code>TileMeshManager</code>
     * @param t The <code>Tesselator</code> of the <code>TileMeshManager</code>
     */
    public TileMeshSource(GL2 gl, Tessellator t) {
        this.gl = gl;
        tess = t;
    }

    /**
     * Sets the {@link de.joglearth.rendering.Tessellator} of the
     * {@link de.joglearth.source.opengl.TileMeshSource}
     * 
     * @param t The new <code>Tesselator</code>
     */
    public void setTessellator(Tessellator t) {
        tess = t;
    }

    /**
     * Sets the level of detail of the {@link de.joglearth.rendering.Tessellator}.
     * 
     * @param sub The new level of detail
     */
    public void setTileSubdivisions(int sub) {
        subdivisions = sub;
    }

    /**
     * Enables or disables the {@link de.joglearth.surface.HeightMap}.
     * 
     * @param enable Whether to enable or disable the <code>HeightMap</code>
     */
    public void setHeightMapEnabled(boolean enable) {
        heightMap = enable;
    }

    @Override
    public SourceResponse<Integer> requestObject(Tile key,
            SourceListener<Tile, Integer> sender) {
        return null;

    }
}
