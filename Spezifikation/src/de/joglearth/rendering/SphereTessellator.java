package de.joglearth.rendering;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Tile;
import de.joglearth.geometry.Vector3;
import de.joglearth.surface.HeightMap;
import static de.joglearth.rendering.MeshUtils.*;
import static java.lang.Math.*;
import static javax.media.opengl.GL.GL_TRIANGLES;


/**
 * Generates {@link de.joglearth.rendering.Mesh}es for tiles on the globe.
 * 
 */
public class SphereTessellator implements Tessellator {

    private static Vector3 getSurfaceVector(double lon, double lat, double latStep, boolean useHeightMap) {
        // The earth axis is equal to the y axis, lon=0, lat=0 has the coordinates (0, 0, 1).
        Vector3 vec = new Vector3(cos(lat) * sin(lon), sin(lat), cos(lat) * cos(lon));
        if (useHeightMap) {
            return vec.times(1 + HeightMap.getHeight(new GeoCoordinates(lon, lat), latStep));
        } else {
            return vec;
        }
    }

    private static void writeSingleVertex(float[] vertices, int vIndex, double lon, double lat,
            double lonStep, double latStep, boolean useHeightMap, double textureX, double textureY) {
        Vector3 vertex = getSurfaceVector(lon, lat, latStep, useHeightMap), 
                east = getSurfaceVector(lon + lonStep, lat, latStep, useHeightMap), 
                north = getSurfaceVector(lon, lat + latStep, latStep, useHeightMap), 
                west = getSurfaceVector(lon - lonStep, lat, latStep, useHeightMap), 
                south = getSurfaceVector(lon, lat - latStep, latStep, useHeightMap);

        Vector3 normal = east.minus(west).crossProduct(south.minus(north)).normalized();
        writeVertex(vertices, vIndex, vertex.x, vertex.y, vertex.z);
        writeNormal(vertices, vIndex, normal.x, normal.y, normal.z);
        writeTextureCoordinates(vertices, vIndex, textureX, textureY);
    }

    private static void writeVertexLine(float[] vertices, int vIndex, double lon, double lat,
            double lonStep, double latStep, boolean useHeightMap, double textureY, int count) {
        double textureX = 0, textureStep = 1.0 / (count - 1);
        for (int i = 0; i < count; ++i) {
            writeSingleVertex(vertices, vIndex, lon, lat, lonStep, latStep, useHeightMap, textureX,
                    textureY);
            vIndex += VERTEX_SIZE;
            lon += lonStep;
            textureX += textureStep;
        }
    }

    private static void writeInterpolatedVertexLine(float[] vertices, int vIndex, double lon,
            double lat, double smallLonStep, int groupSize, double latStep, boolean useHeightMap,
            double textureY, int largeCount) {

        double textureX = 0, textureStep = 1.0 / (largeCount - 1);

        if (largeCount > 0) {
            writeSingleVertex(vertices, vIndex, lon, lat, smallLonStep, latStep, useHeightMap,
                    textureX, textureY);
        }

        for (int i = 1; i < largeCount; ++i) {
            lon += smallLonStep * groupSize;
            textureX += textureStep;

            writeSingleVertex(vertices, vIndex + groupSize * VERTEX_SIZE, lon, lat, smallLonStep,
                    latStep, useHeightMap, textureX, textureY);

            for (int j = 1; j < groupSize; ++j) {
                interpolateVertex(vertices, vIndex, vIndex + groupSize * VERTEX_SIZE, vIndex + j
                        * VERTEX_SIZE, (double) j / groupSize);
            }

            vIndex += groupSize * VERTEX_SIZE;
        }
    }

    private static void writeIndicesLine(int[] indices, int iIndex, int vIndex, int width,
            int direction) {
        
        /*
         * To always draw front faces (i.e. counter-clockwise indices), the order of vertices is
         * dependent on the "vertex line direction", which is up (+1) for the northern hemisphere
         * and down (-1) for the southern one.
         */
        int shiftLeft = direction > 0 ? 0 : 1, shiftRight = 1 - shiftLeft;
        
        for (int i = 0; i < width - 1; ++i) {
            indices[iIndex + 0] = vIndex - width + i + shiftRight;
            indices[iIndex + 1] = vIndex - width + i + shiftLeft;
            indices[iIndex + 2] = vIndex - 2 * width + i + shiftLeft;
            indices[iIndex + 3] = vIndex - width + i + shiftRight;
            indices[iIndex + 4] = vIndex - 2 * width + i + shiftLeft;
            indices[iIndex + 5] = vIndex - 2 * width + i + shiftRight;
            iIndex += 6;
        }
    }

    private int getMaxShrinkCount(int quads) {
        // Find out how often "quads" can be divided by 2.        
        int max = 0;
        double s = quads;
        for (;;) {
            s /= 2;
            if (s % 1 == 0) {
                ++max;
            } else {
                break;
            }
        }
        return max;
    }

    private static int getShrinkCount(double lat, int maxShrinkCount) {
        if (abs(lat) >= PI / 2) {
            return maxShrinkCount;
        } else {
            int exponent = (int) (log(1 / cos(lat)) / log(2));
            return min(exponent, maxShrinkCount);
        }
    }

    @Override
    public Mesh tessellateTile(Tile tile, int subdivisions, boolean useHeightMap) {
        int nRows = subdivisions + 1, direction = tile.getLatitudeFrom() >= 0 ? +1 : -1;

        double lat = direction > 0 ? tile.getLatitudeFrom() : tile.getLatitudeTo(), lon = tile
                .getLongitudeFrom();

        int maxShrinkCount = tile.getDetailLevel() == 0 ? 0 : getMaxShrinkCount(subdivisions + 1),
            shrinkCount = getShrinkCount(lat, maxShrinkCount), 
            rowWidth = max(2, (subdivisions + 1) / (int) pow(2, shrinkCount)) + 1;

        double largeLonStep = 2 * PI / pow(2, tile.getDetailLevel()), 
               lonStep = largeLonStep / (rowWidth - 1), 
               latStep = direction * (tile.getLatitudeTo() - tile.getLatitudeFrom()) / nRows;

        float[] vertices = new float[(nRows + 1) * rowWidth * VERTEX_SIZE * 2];
        int[] indices = new int[nRows * (rowWidth - 1) * 6];
        int vIndex = 0, iIndex = 0;

        double textureY = direction > 0 ? 1 : 0,                //bugfix: vorzeichenfehler
               textureStep = (double) -direction / nRows;       //bugfix: vorzeichenfehler

        writeVertexLine(vertices, 0, lon, lat, lonStep, latStep, useHeightMap, textureY, rowWidth);
        vIndex += rowWidth;

        for (int i = 0; i < nRows; ++i) {
            lat += latStep;
            textureY += textureStep;
            int newRowWidth = max(2,
                    (subdivisions + 1) / (int) pow(2, getShrinkCount(lat, maxShrinkCount))) + 1;
            if (newRowWidth < rowWidth) {
                int groupSize = (rowWidth - 1) / (newRowWidth - 1);
                writeInterpolatedVertexLine(vertices, vIndex * VERTEX_SIZE, lon, lat, lonStep,
                        groupSize, latStep, useHeightMap, textureY, newRowWidth);
                vIndex += rowWidth;
                writeIndicesLine(indices, iIndex, vIndex, rowWidth, direction);
                iIndex += 6 * (rowWidth - 1);

                rowWidth = newRowWidth;
                lonStep = largeLonStep / (rowWidth - 1);
                writeVertexLine(vertices, vIndex * VERTEX_SIZE, lon,
                        lat, lonStep, latStep, useHeightMap, textureY, rowWidth);
                vIndex += rowWidth;
            } else {
                writeVertexLine(vertices, vIndex * VERTEX_SIZE, lon, lat, lonStep,
                        latStep, useHeightMap, textureY, rowWidth);
                vIndex += rowWidth;

                writeIndicesLine(indices, iIndex, vIndex, rowWidth, direction);
                iIndex += 6 * (rowWidth - 1);
            }
        }

        return new Mesh(VERTEX_FORMAT, vertices, GL_TRIANGLES, indices, iIndex);
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other.getClass() == this.getClass();
    }

}
