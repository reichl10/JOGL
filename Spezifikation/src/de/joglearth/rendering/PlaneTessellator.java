package de.joglearth.rendering;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Tile;
import de.joglearth.geometry.Vector3;
import de.joglearth.surface.HeightMap;
import static java.lang.Math.*;
import static de.joglearth.rendering.MeshUtils.*;


/**
 * Generates {@link de.joglearth.rendering.Mesh}es for a tile on the map plane.
 */
public class PlaneTessellator implements Tessellator {

    @Override
    public Mesh tessellateTile(Tile tile, int subdivisions, boolean heightMap) {
        int nVertices = subdivisions + 2;
        double latStep = PI / pow(2, tile.getDetailLevel()), lonStep = 2 * latStep;
        float[] vertices = new float[8 * nVertices * nVertices];
        int vertIndex = 0;
        double lon = tile.longitudeFrom(), lat = tile.latitudeFrom();

        for (int line = 0; line < nVertices; ++line) {
            for (int col = 0; col < nVertices; ++col) {
                writeVertex(vertices, vertIndex, (float) lon, (float) lat,
                        (float) HeightMap.getHeight(new GeoCoordinates(lon, lat)));
                writeTextureCoordinates(vertices, vertIndex, (float) col / nVertices, (float) line
                        / nVertices);
                
                double heightEast = HeightMap.getHeight(new GeoCoordinates(lon + lonStep, lat));
                double heightWest = HeightMap.getHeight(new GeoCoordinates(lon - lonStep, lat));
                double heightSouth = HeightMap.getHeight(new GeoCoordinates(lon, lat + latStep));
                double heightNorth = HeightMap.getHeight(new GeoCoordinates(lon, lat - latStep));
                
                Vector3 westEast = new Vector3(2 * lonStep, heightEast - heightWest,0);
                Vector3 northSouth = new Vector3(0, heightNorth - heightSouth, 2 * latStep);
                
                //TODO sign!
                Vector3 normal = westEast.crossProduct(northSouth);
                writeNormal(vertices, vertIndex, normal.x, normal.y, normal.z);
                
                lon += lonStep;
                lat += latStep;
                vertIndex += VERTEX_SIZE;
                
            }
        }

        int[] indices = new int[6 * (nVertices - 1) * (nVertices - 1)];
        int indIndex = 0;

        for (int line = 0; line < nVertices - 1; ++line) {
            for (int col = 0; col < nVertices - 1; ++col)
            {
                //Annahme: Angabe der Dreieck-Eckpunkte gegen den Uhrzeigersinn
                
                //Dreieck 'eins' (in rechteckiger Subdivision)
                indices[indIndex+0] = indIndex;
                indices[indIndex+1] = indIndex + nVertices;
                indices[indIndex+2] = indIndex + 1;
                
                //Dreieck 'zwei'
                indices[indIndex+3] = indIndex + 1;
                indices[indIndex+4] = indIndex + nVertices;
                indices[indIndex+5] = indIndex + nVertices + 1;
                
                indIndex += 6;
            }
        }

        return new Mesh(vertices, VERTEX_FORMAT, indices);
    }

}
