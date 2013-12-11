package de.joglearth.junit.geometry;

import static org.junit.Assert.*;

import org.junit.Test;

import de.joglearth.geometry.Tile;
import de.joglearth.rendering.Mesh;
import de.joglearth.rendering.SphereTessellator;


public class SphereTessellatorTest {

    @Test
    public void test() {

        SphereTessellator p = new SphereTessellator();
        Tile t = new Tile(5, 10, 10);
        int subdivision = 1;

        Mesh m = p.tessellateTile(t, subdivision, false);
        // assertEquals((9*8), m.vertices.length);
        
        /* Number of triangles */
        assertEquals((3 * 8), m.indices.length);

        /* mod 8: Each point in the mesh is represented by 8 vectors */
        assertEquals(0, (m.vertices.length % 8));
    }
}
