package de.joglearth.rendering;

import static de.joglearth.rendering.MeshUtils.VERTEX_FORMAT;
import static de.joglearth.rendering.MeshUtils.VERTEX_SIZE;
import static de.joglearth.rendering.MeshUtils.writeNormal;
import static de.joglearth.rendering.MeshUtils.writeTextureCoordinates;
import static de.joglearth.rendering.MeshUtils.writeVertex;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.round;
import static javax.media.opengl.GL.GL_TRIANGLES;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Tile;
import de.joglearth.geometry.Vector3;
import de.joglearth.height.HeightMap;

/**
 * Generates {@link de.joglearth.rendering.Mesh}es for a tile on the map plane.
 */
public class PlaneTessellator implements Tessellator {
    
    private double getZCoordinate(double lon, double lat, double latStep, HeightMap heightMap) {
        return heightMap.getHeight(new GeoCoordinates(lon, lat), latStep) - HeightMap.MIN_HEIGHT;
    }

    @Override
    public Mesh tessellateTile(ProjectedTile projected) {
        Tile tile = projected.tile; 
        HeightMap heightMap = projected.heightMap;
        
        double largeLonStep = tile.getLongitudeTo() - tile.getLongitudeFrom();
        if (largeLonStep <= 0) {
            largeLonStep += 2 * PI;
        }
        
        int horizontalTileCount =  (int) round((2*PI) / largeLonStep);
        int nHorizontalQuads = projected.equatorSubdivisions / horizontalTileCount;
        int subdivisions = nHorizontalQuads - 1;
        
        
        int nHorizontalVertices = subdivisions + 2, nVerticalQuads = max(nHorizontalQuads / 2, 1),
                nVerticalVertices = nVerticalQuads + 1;
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
                         1 - (float) line / nVerticalQuads);   // TODO bugfix: flipped textures upsite down

                writeVertex(vertices, vertIndex, lon, lat, getZCoordinate(lon, lat, latStep, heightMap));

                double heightEast = getZCoordinate(lon + lonStep, lat, latStep, heightMap);
                double heightWest = getZCoordinate(lon - lonStep, lat, latStep, heightMap);
                double heightSouth = getZCoordinate(lon, lat + latStep, latStep, heightMap);
                double heightNorth = getZCoordinate(lon, lat - latStep, latStep, heightMap);

                Vector3 westEast = new Vector3(2 * lonStep, 0, heightEast - heightWest);
                Vector3 northSouth = new Vector3(0, 2 * latStep, heightNorth - heightSouth);

                // TODO sign!
                Vector3 normal = westEast.crossProduct(northSouth).normalized();
                writeNormal(vertices, vertIndex, normal.x, normal.y, normal.z);

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
    
    @Override
    public int hashCode() {
        return "PlaneTessellator".hashCode();
    }
}
