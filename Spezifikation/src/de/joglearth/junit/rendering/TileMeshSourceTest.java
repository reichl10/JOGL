package de.joglearth.junit.rendering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.joglearth.geometry.LinearProjection;
import de.joglearth.height.HeightMap;
import de.joglearth.height.flat.FlatHeightMap;
import de.joglearth.height.srtm.SRTMHeightMap;
import de.joglearth.junit.GLTestWindow;
import de.joglearth.map.osm.OSMTile;
import de.joglearth.opengl.VertexBuffer;
import de.joglearth.rendering.Mesh;
import de.joglearth.rendering.ProjectedTile;
import de.joglearth.rendering.Tessellator;
import de.joglearth.rendering.VertexBufferLoader;
import de.joglearth.source.SourceListener;


public class TileMeshSourceTest {
    GLTestWindow window = new GLTestWindow();
    @Test(timeout=100)
    public void testTileMeshSource() {
        TestTessellator tessl = new TestTessellator();
        VertexBufferLoader s = new VertexBufferLoader(window.getGLContext(), tessl);
        TestSourceListener l = new TestSourceListener();
        
        
        OSMTile t = new OSMTile(2, 1, 0);
        int subdivision = 4;
        ProjectedTile lin = new ProjectedTile(t, new LinearProjection(), 0,
        		subdivision, SRTMHeightMap.getInstance());

        s.requestObject(lin, l);
        while (l.buffer == null);
        assertTrue(tessl.lastSubDiv == 4);
        assertEquals(tessl.lastHeightMap, SRTMHeightMap.getInstance());
    }

    private class TestTessellator implements Tessellator {
        public int lastSubDiv = 0;
        public HeightMap lastHeightMap = FlatHeightMap.getInstance();
        public Mesh m = new Mesh();
        
        @Override
        public Mesh tessellateTile(ProjectedTile tile) {
        	this.lastHeightMap = tile.heightMap;
        	this.lastSubDiv = tile.equatorSubdivisions;
        	
        	// tesselate...
        	
            return m;
        }
        
    }
    private class TestSourceListener implements SourceListener<ProjectedTile, VertexBuffer> {
        public VertexBuffer buffer;
        @Override
        public void requestCompleted(ProjectedTile key, VertexBuffer value) {
            buffer = value;
        }
    }
}
