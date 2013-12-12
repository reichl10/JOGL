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
        for (int i = 0; i < width; ++i) {
            indices[iIndex + 0] = vIndex - width + 1 + i;
            indices[iIndex + 1] = vIndex - width + i;
            indices[iIndex + 2] = vIndex - 2 * width;
            indices[iIndex + 3] = vIndex - width + 1;
            indices[iIndex + 4] = vIndex - 2 * width;
            indices[iIndex + 5] = vIndex - 2 * width + 1;
            iIndex += 6;
        }
    }

    private static int getShrinkCount(double lat) {
        if (abs(lat) >= PI / 2) {
            return 0;
        } else {
            return (int) (log(1 / cos(lat)));
        }
    }

    
    @Override
    public Mesh tessellateTile(Tile tile, int subdivisions, boolean useHeightMap) {
        int nRows = subdivisions + 2, 
            direction = tile.getLatitudeFrom() >= 0 ? +1 : -1;
        double lat = direction > 0 ? tile.getLatitudeFrom() : tile.getLatitudeTo(),
               lon = tile.getLongitudeFrom();
        int shrinkCount = getShrinkCount(lat),
            rowWidth = max(2, (subdivisions + 2) / (int) pow(2, shrinkCount)),
            vIndex = 0, 
            iIndex = 0;
        System.out.println(rowWidth);
        double lonStep = 2 * PI / ((rowWidth - 1) * pow(2, tile.getDetailLevel())),
               latStep = 2 * PI / ((1 + subdivisions) * pow(2, tile.getDetailLevel()));
        float[] vertices = new float[nRows * rowWidth * VERTEX_SIZE * 2];
        int[] indices = new int[(nRows-1) * (rowWidth-1) * 6];
        
        writeVertexLine(vertices, vIndex, lon, lat, lonStep, latStep, useHeightMap, rowWidth);
        vIndex += rowWidth * VERTEX_SIZE;
        
        for (int i = 0; i < nRows; ++i) {
            lat += latStep;
            int newRowWidth = max(2, (subdivisions + 2) / (int) pow(2, getShrinkCount(lat)));
            if (newRowWidth < rowWidth) {
                int groupSize = rowWidth / newRowWidth;
                writeInterpolatedVertexLine(vertices, vIndex, lon, lat, lonStep, groupSize, latStep,
                        useHeightMap, newRowWidth);
                vIndex += rowWidth * VERTEX_SIZE;
                writeIndicesLine(indices, iIndex, vIndex, rowWidth);
                iIndex += 6 * rowWidth;
                System.out.println("indices: " + indices + "\n" + "iIndex: " + iIndex + "\n" + "vIndex: " + vIndex + "\n" + "rowWidth: " + rowWidth);
                rowWidth = newRowWidth;
                writeVertexLine(vertices, vIndex, lon,
                        lat, lonStep, latStep, useHeightMap, rowWidth);
                vIndex += rowWidth * VERTEX_SIZE;
            } else {
                writeVertexLine(vertices, vIndex, lon, lat, lonStep,
                        latStep, useHeightMap, rowWidth);
                vIndex += rowWidth * VERTEX_SIZE;
                System.out.println("indices: " + indices + "\n" + "iIndex: " + iIndex + "\n" + "vIndex: " + vIndex + "\n" + "rowWidth: " + rowWidth);
                writeIndicesLine(indices, iIndex, vIndex, rowWidth);
            }
        }

        return new Mesh(VERTEX_FORMAT, vertices, GL_TRIANGLES, indices, iIndex);
    }
    
    
    public static void main(String[] args) {

        SphereTessellator p = new SphereTessellator();
        Tile t = new Tile(2, 1, 0);
        int subdivision = 2;
        Mesh m = p.tessellateTile(t, subdivision, false);
        int count = 0;
        for (int i = 0; i < m.vertices.length; ++i) {
            System.out.print(m.vertices[i] + "    ");
            count +=1;
            if (count == 8) {
                System.out.print(m.vertices[i]*m.vertices[i] + m.vertices[i-1]*m.vertices[i-1] + m.vertices[i-2]*m.vertices[i-2]);
                System.out.println();
                count = 0;
            }
        }
    }
}
