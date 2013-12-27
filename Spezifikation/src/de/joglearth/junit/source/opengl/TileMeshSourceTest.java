package de.joglearth.junit.source.opengl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.joglearth.geometry.Tile;
import de.joglearth.junit.GLTestWindow;
import de.joglearth.opengl.VertexBufferLoader;
import de.joglearth.opengl.VertexBuffer;
import de.joglearth.rendering.Mesh;
import de.joglearth.rendering.Tessellator;
import de.joglearth.source.SourceListener;


public class TileMeshSourceTest {
    GLTestWindow window = new GLTestWindow();
    @Test(timeout=100)
    public void testTileMeshSource() {
        TestTessellator tessl = new TestTessellator();
        VertexBufferLoader s = new VertexBufferLoader(window.getGL(), tessl);
        s.setTileSubdivisions(2732);
        s.setHeightMapEnabled(true);
        TestSourceListener l = new TestSourceListener();
        s.requestObject(new Tile(3, 1, 1), l);
        while (l.buffer == null);
        assertTrue(tessl.m.indexCount == l.buffer.indexCount);
        assertTrue(tessl.lastSubDiv == 2732);
        assertTrue(tessl.lastHeightMap);
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
