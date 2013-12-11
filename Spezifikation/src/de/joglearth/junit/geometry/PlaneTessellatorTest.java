package de.joglearth.junit.geometry;

import static org.junit.Assert.*;

import org.junit.Test;

import de.joglearth.geometry.Tile;
import de.joglearth.rendering.Mesh;
import de.joglearth.rendering.PlaneTessellator;


public class PlaneTessellatorTest {

    @Test
    public void test() {

        PlaneTessellator p = new PlaneTessellator();
        Tile t = new Tile(2, 1, 0);
        int subdivision = 1;

        Mesh m = p.tessellateTile(t, subdivision, false);
        assertEquals((9 * 8), m.vertices.length);
        assertEquals((8 * 3), m.indices.length);
    }
}
