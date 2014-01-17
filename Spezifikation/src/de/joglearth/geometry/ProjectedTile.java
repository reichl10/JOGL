package de.joglearth.geometry;

import de.joglearth.height.HeightMap;

public final class ProjectedTile {
	
	public final Tile tile;
	public final MapProjection projection;
	public final int equatorSubdivisions;
	public final int minEquatorSubdivisions;
	
	public ProjectedTile (Tile tile, MapProjection projection, int minEquatorSubdivisions, int equatorSubdivisions) {
		this.tile = tile;
		this.projection = projection;
		this.minEquatorSubdivisions = minEquatorSubdivisions;
		this.equatorSubdivisions = equatorSubdivisions;
	}

    /* (nicht-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + equatorSubdivisions;
        result = prime * result + ((projection == null) ? 0 : projection.hashCode());
        result = prime * result + ((tile == null) ? 0 : tile.hashCode());
        return result;
    }

    /* (nicht-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
