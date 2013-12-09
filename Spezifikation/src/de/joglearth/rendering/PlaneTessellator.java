package de.joglearth.rendering;

import de.joglearth.geometry.Tile;
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
                writeVertex(vertices, vertIndex, (float) lon, (float) lat, 0);
                writeTextureCoordinates(vertices, vertIndex, (float) col / nVertices, (float) line
                        / nVertices);
                lon += lonStep;
                lat += latStep;
                // fehlt hier ein vertIndex += 8; ?
            }
        }

        vertIndex = 0;
        for (int line = 0; line < nVertices; ++line) {
            for (int col = 0; col < nVertices; ++col) {
                writeNormal(vertices, vertIndex, 0, 0, 1);
                lon += lonStep;
                lat += latStep;
                // fehlt hier ein vertIndex += 8; ?
            }
        }

        int[] indices = new int[6 * (nVertices - 1) * (nVertices - 1)];
        int indIndex = 0;

        for (int line = 0; line < nVertices - 1; ++line) {
            for (int col = 0; col < nVertices - 1; ++col) 
            {
                //Annahme: Angabe der Dreieck-Eckpunkte im Uhrzeigersinn
                //wenn falsch -> 2. und 3. Eckpunkt bitte vertauschen!
                
                //Dreieck 'eins' (in rechteckiger Subdivision)
                indices[indIndex+0] = indIndex;
                indices[indIndex+1] = indIndex+1; // (2.Eckpunkt)
                indices[indIndex+2] = indIndex+nVertices; // (3.Eckpunkt)
                
                //Dreieck 'zwei'
                indices[indIndex+3] = indIndex+1;
                indices[indIndex+4] = indIndex+nVertices+1; // (2.Eckpunkt)
                indices[indIndex+5] = indIndex+nVertices; // (3.Eckpunkt)
                
                indIndex += 6;
            }
        }

        return new Mesh(vertices, VERTEX_FORMAT, indices);
    }

}
