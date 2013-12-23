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
    
    private double getZCoordinate(double lon, double lat, double latStep) {
        return HeightMap.getHeight(new GeoCoordinates(lon, lat), latStep) + HeightMap.MIN_HEIGHT;
    }

    @Override
    public Mesh tessellateTile(Tile tile, int subdivisions, boolean useHeightMap) {
        //TODO System.out.println(tile);
        int nHorizontalVertices = subdivisions + 2, nHorizontalQuads = subdivisions + 1,
            nVerticalQuads = max(nHorizontalQuads / 2, 1), nVerticalVertices = nVerticalQuads + 1;
        float[] vertices = new float[8 * nVerticalVertices * nHorizontalVertices];
        int vertIndex = 0;
        
        double lonStart = tile.getLongitudeFrom();
        if (lonStart > tile.getLongitudeTo()) {
            lonStart -= 2 * PI;
        }
        double lon = lonStart, lat = tile.getLatitudeTo();
        double latStep = abs(tile.getLatitudeTo() - tile.getLatitudeFrom()) / nVerticalQuads, 
               lonStep = abs(tile.getLongitudeTo() - lonStart) / nHorizontalQuads;

        for (int line = 0; line < nVerticalVertices; ++line) {
            for (int col = 0; col < nHorizontalVertices; ++col) {
                writeTextureCoordinates(vertices, vertIndex, (float) col / nHorizontalQuads, 
                         1 - (float) line / nVerticalQuads);   //bugfix: flipped textures upsite down

                if (useHeightMap) {
                    writeVertex(vertices, vertIndex, lon, lat, getZCoordinate(lon, lat, latStep));

                    double heightEast = getZCoordinate(lon + lonStep, lat, latStep);
                    double heightWest = getZCoordinate(lon - lonStep, lat, latStep);
                    double heightSouth = getZCoordinate(lon, lat + latStep, latStep);
                    double heightNorth = getZCoordinate(lon, lat - latStep, latStep);

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
            lon = lonStart;
        }

        int[] indices = new int[6 * (nHorizontalVertices - 1) * (nHorizontalVertices - 1)];
        int indIndex = 0;
        int index = 0;

        for (int line = 0; line < nVerticalQuads; ++line) {
            index = line * nHorizontalVertices;
            for (int col = 0; col < nHorizontalQuads; ++col)
            {
                // Annahme: Angabe der Dreieck-Eckpunkte gegen den Uhrzeigersinn

                // Dreieck 'eins' (in rechteckiger Subdivision)
                indices[indIndex + 0] = index;
                indices[indIndex + 1] = index + nHorizontalVertices;
                indices[indIndex + 2] = index + 1;

                // Dreieck 'zwei'
                indices[indIndex + 3] = index + 1;
                indices[indIndex + 4] = index + nHorizontalVertices;
                indices[indIndex + 5] = index + nHorizontalVertices + 1;

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
