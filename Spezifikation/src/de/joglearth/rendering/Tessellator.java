package de.joglearth.rendering;

import de.joglearth.geometry.ProjectedTile;
import de.joglearth.geometry.Tile;
import de.joglearth.height.HeightMap;


/**
 * An implementation of the {@link de.joglearth.rendering.Tesselator} interface provides the
 * occasion to generate a {@link de.joglearth.rendering.Mesh}.
 * 
 */
public interface Tessellator {

    /**
     * Creates a {@link de.joglearth.rendering.Mesh} for a {@link de.joglearth.geometry.Tile} with
     * height data.
     * 
     * 
     * @param tile The location where the <code>Mesh</code> should be rendered
     * @param subdivisions Number of times the <code>Mesh</code> is divided in both axis. Minimum
     *        <code>0</code> divisions.
     * @param heightMap A <code>HeightMap</code> that provides the height data for the tile
     * @return A <code>Mesh</code> with (subdivisions + 1)^2 squares, with each divided in two
     *         triangles
     */
    Mesh tessellateTile(ProjectedTile tile, int subdivisions, HeightMap heightMap);
}

