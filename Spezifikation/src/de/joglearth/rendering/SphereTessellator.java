package de.joglearth.rendering;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Tile;
import de.joglearth.surface.HeightMap;
import static de.joglearth.rendering.MeshUtils.*;
import static java.lang.Math.*;


/**
 * Generates {@link de.joglearth.rendering.Mesh}es for tiles on the globe.
 * 
 */
public class SphereTessellator implements Tessellator {

    private static void writeVector(float[] vertices, int offset, double lon, double lat) {
        double height = 1 + HeightMap.getHeight(new GeoCoordinates(lon, lat));
        writeVertex(vertices, offset, cos(lat) * sin(lon) * height, sin(lat) * height, cos(lat)
                * cos(lon) * height);
    }

    @Override
    public Mesh tessellateTile(Tile tile, int subdivisions, boolean heightMap) {
        return null;
    }

}
