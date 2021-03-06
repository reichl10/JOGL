package de.joglearth.rendering;

/**
 * An implementation of the {@link Tesselator} interface provides the occasion to generate a
 * {@link Mesh}.
 * 
 */
public interface Tessellator {

    /**
     * Creates a {@link Mesh} for a {@link Tile} with height data.
     * 
     * 
     * @param tile The location where the <code>Mesh</code> should be rendered
     * @param subdivisions Number of times the <code>Mesh</code> is divided in both axis. Minimum
     *        <code>0</code> divisions.
     * @param heightMap A <code>HeightMap</code> that provides the height data for the tile
     * @return A <code>Mesh</code> with (subdivisions + 1)^2 squares, with each divided in two
     *         triangles
     */
    Mesh tessellateTile(ProjectedTile tile);

    @Override
    int hashCode();

    @Override
    boolean equals(Object other);
}
