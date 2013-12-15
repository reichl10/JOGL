package de.joglearth.rendering;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Tile;
import de.joglearth.geometry.Vector3;
import de.joglearth.surface.HeightMap;
import static java.lang.Math.*;
import static de.joglearth.rendering.MeshUtils.*;
import static javax.media.opengl.GL2.*;

/**
 * Generates {@link de.joglearth.rendering.Mesh}es for a tile on the map plane.
 */
public class PlaneTessellator implements Tessellator {

    @Override
    public Mesh tessellateTile(Tile tile, int subdivisions, boolean useHeightMap) {
        System.out.println(tile);
        int nVertices = subdivisions + 2, nQuads = subdivisions + 1;
        double latStep = abs(tile.getLatitudeTo() - tile.getLatitudeFrom()) / nQuads, 
               lonStep = abs(tile.getLongitudeTo() - tile.getLongitudeFrom()) / nQuads;
        float[] vertices = new float[8 * nVertices * nVertices];
        int vertIndex = 0;
        double lon = tile.getLongitudeFrom(), lat = tile.getLatitudeTo();

        for (int line = 0; line < nVertices; ++line) {
            for (int col = 0; col < nVertices; ++col) {
                writeTextureCoordinates(vertices, vertIndex, (float) col / (nVertices - 1), 
                        1 - (float) line / (nVertices - 1));

                if (useHeightMap) {
                    writeVertex(vertices, vertIndex, (float) lon, (float) lat,
                            (float) HeightMap.getHeight(new GeoCoordinates(lon, lat)));

                    double heightEast = HeightMap.getHeight(new GeoCoordinates(lon + lonStep, lat));
                    double heightWest = HeightMap.getHeight(new GeoCoordinates(lon - lonStep, lat));
                    double heightSouth = HeightMap
                            .getHeight(new GeoCoordinates(lon, lat + latStep));
                    double heightNorth = HeightMap
                            .getHeight(new GeoCoordinates(lon, lat - latStep));

                    Vector3 westEast = new Vector3(2 * lonStep, 0, heightEast - heightWest);
                    Vector3 northSouth = new Vector3(0, 2 * latStep, heightNorth - heightSouth);

                    // TODO sign!
                    Vector3 normal = westEast.crossProduct(northSouth).normalized();
                    writeNormal(vertices, vertIndex, normal.x, normal.y, normal.z);
                } else {
                    writeVertex(vertices, vertIndex, lon, lat, 0);
                    writeNormal(vertices, vertIndex, 0, 0, 1);
                }

                lon += lonStep;
                vertIndex += VERTEX_SIZE;
            }
            lat -= latStep;
            lon = tile.getLongitudeFrom();
        }

        int[] indices = new int[6 * (nVertices - 1) * (nVertices - 1)];
        int indIndex = 0;
        int index = 0;

        for (int line = 0; line < nVertices - 1; ++line) {
            index = line * nVertices;
            for (int col = 0; col < nVertices - 1; ++col)
            {
                // Annahme: Angabe der Dreieck-Eckpunkte gegen den Uhrzeigersinn

                // Dreieck 'eins' (in rechteckiger Subdivision)
                indices[indIndex + 0] = index;
                indices[indIndex + 1] = index + nVertices;
                indices[indIndex + 2] = index + 1;

                // Dreieck 'zwei'
                indices[indIndex + 3] = index + 1;
                indices[indIndex + 4] = index + nVertices;
                indices[indIndex + 5] = index + nVertices + 1;

                indIndex += 6;
                ++index;
            }
        }

        return new Mesh(VERTEX_FORMAT, vertices, GL_TRIANGLES, indices, indIndex);
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other.getClass() == this.getClass();
    }
}
