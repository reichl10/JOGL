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

    private static Vector3 getSurfaceVector(double lon, double lat, boolean useHeightMap) {
        // The earth axis is equal to the y axis, lon=0, lat=0 has the coordinates (0, 0, 1).
        Vector3 vec = new Vector3(cos(lat) * sin(lon), sin(lat), cos(lat) * cos(lon));
        if (useHeightMap) {
            return vec.times(1 + HeightMap.getHeight(new GeoCoordinates(lon, lat)));
        } else {
            return vec;
        }
    }

    private static void writeSingleVertex(float[] vertices, int vIndex, double lon, double lat,
            double lonStep, double latStep, boolean useHeightMap) {
        Vector3 vertex = getSurfaceVector(lon, lat, useHeightMap),
                east = getSurfaceVector(lon + lonStep, lat, useHeightMap),
                north = getSurfaceVector(lon, lat + latStep, useHeightMap),
                west = getSurfaceVector(lon - lonStep, lat, useHeightMap),
                south = getSurfaceVector(lon, lat - latStep, useHeightMap);

        Vector3 normal = east.minus(west).crossProduct(south.minus(north)).normalized();
        writeVertex(vertices, vIndex, vertex.x, vertex.y, vertex.z);
        writeNormal(vertices, vIndex, normal.x, normal.y, normal.z);
    }

    private static void writeVertexLine(float[] vertices, int vIndex, double lon, double lat,
            double lonStep, double latStep, boolean useHeightMap, int count) {
        for (int i = 0; i < count; ++i) {
            writeSingleVertex(vertices, vIndex, lon, lat, lonStep, latStep, useHeightMap);
            vIndex += VERTEX_SIZE;
            lon += lonStep;
        }
    }

    private static void writeInterpolatedVertexLine(float[] vertices, int vIndex, double lon,
            double lat, double largeLonStep, int groupSize, double latStep, boolean useHeightMap,
            int largeCount) {

        if (largeCount > 0) {
            writeSingleVertex(vertices, vIndex, lon, lat, largeLonStep, latStep, useHeightMap);
            vIndex += VERTEX_SIZE;
            lon += largeLonStep;
        }

        for (int i = 0; i < largeCount; ++i) {
            writeSingleVertex(vertices, vIndex + groupSize * VERTEX_SIZE, lon, lat, largeLonStep,
                    latStep, useHeightMap);

            for (int j = 0; i < groupSize - 1; ++i) {
                interpolateVertex(vertices, vIndex, vIndex + groupSize * VERTEX_SIZE, vIndex + j
                        * VERTEX_SIZE, (j + 1) / groupSize);
            }

            vIndex += groupSize * VERTEX_SIZE;
            lon += largeLonStep;
        }
    }

    private static void writeIndicesLine(int[] indices, int iIndex, int vIndex, int width) {
        for (int i = 0; i < width-1; ++i) {
            indices[iIndex + 0] = vIndex - width + 1 + i;
            indices[iIndex + 1] = vIndex - width + i;
            indices[iIndex + 2] = vIndex - 2 * width + i;
            indices[iIndex + 3] = vIndex - width + i + 1;
            indices[iIndex + 4] = vIndex - 2 * width + i;
            indices[iIndex + 5] = vIndex - 2 * width + i + 1;
            iIndex += 6;
        }
    }
    
    private int getMaxShrinkCount(int subdivisions) {
        int max = 0;
        double s = subdivisions;
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
        int nRows = subdivisions + 1, 
            direction = tile.getLatitudeFrom() >= 0 ? +1 : -1;
        double lat = direction > 0 ? tile.getLatitudeFrom() : tile.getLatitudeTo(),
               lon = tile.getLongitudeFrom();
        int maxShrinkCount = getMaxShrinkCount(subdivisions),
            shrinkCount = getShrinkCount(lat, maxShrinkCount),
            rowWidth = max(2, (subdivisions + 2) / (int) pow(2, shrinkCount)),
            vIndex = 0, 
            iIndex = 0;

        double lonStep = (tile.getLongitudeTo() - tile.getLongitudeFrom()) / (rowWidth - 1),
               latStep = (tile.getLatitudeTo() - tile.getLatitudeFrom()) / nRows;
        float[] vertices = new float[nRows * rowWidth * VERTEX_SIZE * 2];
        int[] indices = new int[nRows * (rowWidth-1) * 6];
        
        writeVertexLine(vertices, 0, lon, lat, lonStep, latStep, useHeightMap, rowWidth);
        vIndex += rowWidth;
        
        for (int i = 0; i < nRows; ++i) {
            lat += latStep;
            int newRowWidth = max(2, (subdivisions + 2) / (int) pow(2, getShrinkCount(lat, maxShrinkCount)));
            if (newRowWidth < rowWidth) {
                int groupSize = rowWidth / newRowWidth;
                writeInterpolatedVertexLine(vertices, vIndex * VERTEX_SIZE, lon, lat, lonStep, groupSize, latStep,
                        useHeightMap, newRowWidth);
                vIndex += rowWidth;
                writeIndicesLine(indices, iIndex, vIndex, rowWidth);

                rowWidth = newRowWidth;
                writeVertexLine(vertices, vIndex * VERTEX_SIZE, lon,
                        lat, lonStep, latStep, useHeightMap, rowWidth);
                vIndex += rowWidth;
            } else {
                writeVertexLine(vertices, vIndex * VERTEX_SIZE, lon, lat, lonStep,
                        latStep, useHeightMap, rowWidth);
                vIndex += rowWidth;

                writeIndicesLine(indices, iIndex, vIndex, rowWidth);
            }
            iIndex += 6 * (rowWidth-1);
        }

        return new Mesh(VERTEX_FORMAT, vertices, GL_TRIANGLES, indices, iIndex);
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other.getClass() == this.getClass();
    }
        
}
