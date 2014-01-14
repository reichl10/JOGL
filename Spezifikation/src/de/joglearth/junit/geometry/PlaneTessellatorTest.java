package de.joglearth.junit.geometry;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.SurfaceListener;
import de.joglearth.height.HeightMap;
import de.joglearth.map.osm.OSMTile;
import de.joglearth.rendering.Mesh;
import de.joglearth.rendering.PlaneTessellator;


public class PlaneTessellatorTest {

    @Test
    public void test() {

        PlaneTessellator p = new PlaneTessellator();
        OSMTile t = new OSMTile(2, 1, 0);
        int subdivision = 1;
        HeightMap h = new HeightMap() {
            
            @Override
            public void removeSurfaceListener(SurfaceListener l) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public double getHeight(GeoCoordinates coords, double angularResolution) {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public void addSurfaceListener(SurfaceListener l) {
                // TODO Auto-generated method stub
                
            }
        };

        Mesh m = p.tessellateTile(t, subdivision, h);
        assertEquals((9 * 8), m.vertices.length);
        assertEquals((8 * 3), m.indices.length);
    }
}
