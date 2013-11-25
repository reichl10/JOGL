package de.joglearth.rendering;

import de.joglearth.geometry.Tile;
import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.surface.HeightMapManager;
import de.joglearth.ui.*;


/**
 * An implementation of the <code>Tesselator</code> Interface provides the occastion to generate a
 * {@link Mesh}.
 * 
 */
public interface Tessellator {

    /**
     * Creates a {@link Mesh} for a {@link} Tile with height data.
     * 
     * 
     * @param tile The location where the Mesh should be rendered.
     * @param subdivisions Number of times the Mesh is divided in both axis. Minimum <code>0</code>
     *        divisions.
     * @param heightMap A {@link de.joglearth.surface.HeightMapManager} that provides the height
     *        data for the tile.
     * @return A {@link Mesh} with (subdivisions + 1)^2 squares, with each divided in two triangles.
     */
    Mesh tessellateTile(Tile tile, int subdivisions, boolean heightMap);
}
