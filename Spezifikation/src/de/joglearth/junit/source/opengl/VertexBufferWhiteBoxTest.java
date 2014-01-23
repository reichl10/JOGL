package de.joglearth.junit.source.opengl;

import static javax.media.opengl.GL.GL_TRIANGLES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.joglearth.geometry.LinearProjection;
import de.joglearth.height.flat.FlatHeightMap;
import de.joglearth.junit.GLTestWindow;
import de.joglearth.map.osm.OSMTile;
import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.VertexBuffer;
import de.joglearth.rendering.PlaneTessellator;
import de.joglearth.rendering.ProjectedTile;
import de.joglearth.rendering.VertexBufferLoader;
import de.joglearth.rendering.VertexBufferPool;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;


public class VertexBufferWhiteBoxTest {

    private GLTestWindow window;


    @Before
    public void setUp() throws Exception {
        window = new GLTestWindow();
    }

    @After
    public void tearDown() throws Exception {
        if (window != null) {
            window.dispose();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test() throws Throwable {
        window.display(new Runnable() {

            @Override
            public void run() {
                final GLContext gl = window.getGLContext();
                final VertexBufferLoader source = new VertexBufferLoader(gl, new PlaneTessellator());
                source.setTessellator(new PlaneTessellator());
                final VertexBufferPool<ProjectedTile> cache = new VertexBufferPool<ProjectedTile>(gl);
                final ProjectedTile tile = new ProjectedTile(new OSMTile(0, 0, 0), new LinearProjection(), 5, 10, FlatHeightMap.getInstance());
                        
                SourceResponse<VertexBuffer> response;

                // Acquire Mesh
                response = source.requestObject(tile, null);
                assertEquals(response.response, SourceResponseType.SYNCHRONOUS);
                assertNotNull(response.value);

                VertexBuffer vbo = response.value;
                assertTrue(vbo.getIndices() > 0);
                assertTrue(vbo.getVertices() > 0);
                assertEquals(vbo.getPrimitiveType(), GL_TRIANGLES);
                assertEquals(vbo.getIndexCount(), 800);

                // Put into cache
                cache.putObject(tile, vbo);
                assertTrue(cache.getExistingObjects().iterator().hasNext());

                response = cache.requestObject(tile, null);
                assertEquals(response.response, SourceResponseType.SYNCHRONOUS);
                assertEquals(response.value, vbo);
                gl.drawVertexBuffer(vbo, null);


                // Drop all meshes
                cache.dropAll();
                assertFalse(cache.getExistingObjects().iterator().hasNext());
            }
        });
    }
}
