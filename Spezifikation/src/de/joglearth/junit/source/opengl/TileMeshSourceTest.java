package de.joglearth.junit.source.opengl;

import static org.junit.Assert.*;

import org.junit.Test;

import de.joglearth.geometry.Tile;
import de.joglearth.junit.GLTestWindow;
import de.joglearth.rendering.Mesh;
import de.joglearth.rendering.Tessellator;
import de.joglearth.source.SourceListener;
import de.joglearth.source.opengl.TileMeshSource;
import de.joglearth.source.opengl.VertexBuffer;


public class TileMeshSourceTest {
    GLTestWindow window = new GLTestWindow();
    @Test(timeout=100)
    public void testTileMeshSource() {
        TestTessellator tessl = new TestTessellator();
        TileMeshSource s = new TileMeshSource(window.getGL(), tessl);
        s.setTileSubdivisions(2732);
        s.setHeightMapEnabled(true);
        TestSourceListener l = new TestSourceListener();
        s.requestObject(new Tile(3, 1, 1), l);
        while (l.buffer == null);
        assertEquals(tessl.m, l.buffer);
        assertTrue(tessl.lastSubDiv == 2732);
        assertTrue(tessl.lastHeightMap);
    }

    @Test
    public void testSetTileSubdivisions() {
        fail("Not yet implemented");
    }

    @Test
    public void testRequestObject() {
        fail("Not yet implemented");
    }

    private class TestTessellator implements Tessellator {
        public int lastSubDiv = 0;
        public boolean lastHeightMap = false;
        public Mesh m = new Mesh();
        @Override
        public Mesh tessellateTile(Tile tile, int subdivisions, boolean heightMap) {
            return m;
        }
        
    }
    private class TestSourceListener implements SourceListener<Tile, VertexBuffer> {
        public VertexBuffer buffer;
        @Override
        public void requestCompleted(Tile key, VertexBuffer value) {
            buffer = value;
        }
    }
}
