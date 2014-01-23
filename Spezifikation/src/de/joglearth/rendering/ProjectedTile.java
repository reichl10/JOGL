package de.joglearth.rendering;

import de.joglearth.geometry.MapProjection;
import de.joglearth.geometry.Tile;
import de.joglearth.height.HeightMap;


/**
 * Structure that describes a tile that is projected with a certain projection. That is necessary
 * because a tile that is used for a globe has to have other surface coordinates as a tile that is
 * used for a flat map. (e.g. mercator projection for the earth)
 */
public final class ProjectedTile {

    /**
     * The tile that is projected in a certain way.
     */
    public final Tile tile;

    /**
     * The projection of the tile.
     */
    public final MapProjection projection;

    /**
     * The number of subdivisions of the vertices on the equator.
     */
    public final int equatorSubdivisions;

    /**
     * The number of subdivisions that at least have to be fulfilled.
     */
    public final int minEquatorSubdivisions;

    /**
     * The used {@link HeightMap}.
     */
    public final HeightMap heightMap;


    /**
     * Creates a {@link ProjectedTile} using different values.
     * 
     * @param tile The tile that is given under a certain projection
     * @param projection The projection of the tile
     * @param minEquatorSubdivisions The minimal number of subdivisions on the equator
     * @param equatorSubdivisions The actual number of subdivisions on the equator
     * @param heightMap The used {@link HeightMap}
     */
    public ProjectedTile(Tile tile, MapProjection projection, int minEquatorSubdivisions,
            int equatorSubdivisions, HeightMap heightMap) {
        if (tile == null || projection == null || minEquatorSubdivisions < 0
                || equatorSubdivisions < 0 || heightMap == null) {
            throw new IllegalArgumentException();
        }

        this.tile = tile;

        this.projection = projection;
        this.minEquatorSubdivisions = minEquatorSubdivisions;
        this.equatorSubdivisions = equatorSubdivisions;
        this.heightMap = heightMap;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + equatorSubdivisions;
        result = prime * result + ((heightMap == null) ? 0 : heightMap.hashCode());
        result = prime * result + minEquatorSubdivisions;
        result = prime * result + ((projection == null) ? 0 : projection.hashCode());
        result = prime * result + ((tile == null) ? 0 : tile.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ProjectedTile other = (ProjectedTile) obj;
        if (equatorSubdivisions != other.equatorSubdivisions) {
            return false;
        }
        if (heightMap == null) {
            if (other.heightMap != null) {
                return false;
            }
        } else if (!heightMap.equals(other.heightMap)) {
            return false;
        }
        if (minEquatorSubdivisions != other.minEquatorSubdivisions) {
            return false;
        }
        if (projection == null) {
            if (other.projection != null) {
                return false;
            }
        } else if (!projection.equals(other.projection)) {
            return false;
        }
        if (tile == null) {
            if (other.tile != null) {
                return false;
            }
        } else if (!tile.equals(other.tile)) {
            return false;
        }
        return true;
    }
}
