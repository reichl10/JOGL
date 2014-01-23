package de.joglearth.junit.source.opengl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.joglearth.geometry.LinearProjection;
import de.joglearth.geometry.MercatorProjection;
import de.joglearth.geometry.Tile;
import de.joglearth.height.flat.FlatHeightMap;
import de.joglearth.height.srtm.SRTMHeightMap;
import de.joglearth.junit.GLTestWindow;
import de.joglearth.map.osm.OSMTile;
import de.joglearth.opengl.VertexBuffer;
import de.joglearth.rendering.Mesh;
import de.joglearth.rendering.PlaneTessellator;
import de.joglearth.rendering.ProjectedTile;
import de.joglearth.rendering.Tessellator;
import de.joglearth.rendering.VertexBufferLoader;
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
        
        
        OSMTile t = new OSMTile(2, 1, 0);
        int subdivision = 1;
        ProjectedTile lin = new ProjectedTile(t, new LinearProjection(), 0,
        		subdivision, SRTMHeightMap.getInstance());

        s.requestObject(lin, l);
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
        public Mesh tessellateTile(ProjectedTile tile) {
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
