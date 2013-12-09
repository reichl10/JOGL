package de.joglearth.rendering;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Tile;
import de.joglearth.geometry.Vector3;
import de.joglearth.surface.HeightMap;
import static de.joglearth.rendering.MeshUtils.*;
import static java.lang.Math.*;


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

    private static void writeSphereVertex(float[] vertices, int vIndex, double lon, double lat,
            double lonStep, double latStep, boolean useHeightMap) {
        Vector3 vertex = getSurfaceVector(lon, lat, useHeightMap), east = getSurfaceVector(lon
                + lonStep, lat, useHeightMap), north = getSurfaceVector(lon, lat + latStep,
                useHeightMap), west = getSurfaceVector(lon - lonStep, lat, useHeightMap), south = getSurfaceVector(
                lon, lat - latStep, useHeightMap);
        Vector3 normal = east.minus(west).crossProduct(south.minus(north)).normalized();
        writeVertex(vertices, vIndex, vertex.x, vertex.y, vertex.z);
        writeNormal(vertices, vIndex, normal.x, normal.y, normal.z);
    }

    private static int shrinkCount(double lat) {
        if (abs(lat) >= PI / 2) {
            return 0;
        } else {
            return (int) (log(1 / cos(lat)));
        }
    }

    @Override
    public Mesh tessellateTile(Tile tile, int subdivisions, boolean heightMap) {
        double latStep = 2 * PI / ((1 + subdivisions) * pow(2, tile.getDetailLevel()));   
        int nRows = subdivisions + 2,
            direction = tile.latitudeFrom() >= 0 ? +1 : -1;
        
        for (int i=0; i< nRows; ++i) {
            
        }

        return null;
    }

}
