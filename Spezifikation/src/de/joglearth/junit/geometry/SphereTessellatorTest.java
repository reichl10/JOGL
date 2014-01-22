package de.joglearth.junit.geometry;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.SurfaceListener;
import de.joglearth.height.HeightMap;
import de.joglearth.map.osm.OSMTile;
import de.joglearth.rendering.Mesh;
import de.joglearth.rendering.SphereTessellator;


public class SphereTessellatorTest {

    @Test
    public void test() {

        SphereTessellator p = new SphereTessellator();
        OSMTile t = new OSMTile(5, 10, 10);
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
        // assertEquals((9*8), m.vertices.length);
        
        
        /* Number of triangles */
        assertEquals((3 * 8), m.indices.length);

        /* mod 8: Each point in the mesh is represented by 8 vectors */
        assertEquals(0, (m.vertices.length % 8));
    }
}
