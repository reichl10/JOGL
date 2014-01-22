package de.joglearth.junit.geometry;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


import de.joglearth.geometry.LinearProjection;
import de.joglearth.geometry.MercatorProjection;
import de.joglearth.height.flat.FlatHeightMap;
import de.joglearth.map.osm.OSMTile;
import de.joglearth.rendering.Mesh;
import de.joglearth.rendering.PlaneTessellator;
import de.joglearth.rendering.ProjectedTile;


public class PlaneTessellatorTest {

    @Test
    public void test() {

        PlaneTessellator p = new PlaneTessellator();
        OSMTile t = new OSMTile(2, 1, 0);
        int subdivision = 1;
        ProjectedTile lin = new ProjectedTile(t, new LinearProjection(), 0,
        		subdivision, FlatHeightMap.getInstance());
        ProjectedTile merc = new ProjectedTile(t, new MercatorProjection(), 0,
        		subdivision, FlatHeightMap.getInstance());
        

        System.out.println("latFrom: " + t.getLatitudeFrom() + "  latTo: " + t.getLatitudeTo()
                + "  longFrom: " + t.getLongitudeFrom() + "  longTo: " + t.getLongitudeTo());
        Mesh mLin = p.tessellateTile(lin);
        Mesh mMerc = p.tessellateTile(merc);
        
        
        assertEquals((9 * 8), mLin.vertices.length);
        assertEquals((8 * 3), mLin.indices.length);
        
        assertEquals((9 * 8), mMerc.vertices.length);
        assertEquals((8 * 3), mMerc.indices.length);
    }
}
