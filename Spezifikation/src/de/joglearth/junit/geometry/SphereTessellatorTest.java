package de.joglearth.junit.geometry;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.joglearth.geometry.LinearProjection;
import de.joglearth.geometry.MercatorProjection;
import de.joglearth.height.flat.FlatHeightMap;
import de.joglearth.map.osm.OSMTile;
import de.joglearth.rendering.Mesh;
import de.joglearth.rendering.ProjectedTile;
import de.joglearth.rendering.SphereTessellator;


public class SphereTessellatorTest {

    @Test
    public void test() {

        SphereTessellator p = new SphereTessellator();
        OSMTile t = new OSMTile(5, 10, 10);
        int subdivision = 1;
        ProjectedTile lin = new ProjectedTile(t, new LinearProjection(), 0,
                subdivision, FlatHeightMap.getInstance());
        ProjectedTile merc = new ProjectedTile(t, new MercatorProjection(), 0,
                subdivision, FlatHeightMap.getInstance());

        Mesh mLin = p.tessellateTile(lin);
        Mesh mMerc = p.tessellateTile(merc);
        
        assertEquals((9 * 8), mLin.vertices.length);
        assertEquals((9 * 8), mMerc.vertices.length);
        
        /* Number of triangles */
        assertEquals((3 * 8), mLin.indices.length);
        assertEquals((3 * 8), mMerc.indices.length);

        /* mod 8: Each point in the mesh is represented by 8 vectors */
        assertEquals(0, (mLin.vertices.length % 8));
        assertEquals(0, (mMerc.vertices.length % 8));
    }
}
