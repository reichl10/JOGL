package de.joglearth.junit.source.opengl;

import static org.junit.Assert.*;
import static javax.media.opengl.GL2.*;

import javax.media.opengl.GL2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.joglearth.geometry.Tile;
import de.joglearth.junit.GLTestWindow;
import de.joglearth.opengl.GLError;
import de.joglearth.opengl.VertexBuffer;
import de.joglearth.rendering.PlaneTessellator;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.source.opengl.TileMeshSource;
import de.joglearth.source.opengl.VertexBufferCache;
import de.joglearth.util.AWTInvoker;
import de.joglearth.util.RunnableWithResult;


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
                final GL2 gl = window.getGL();
                final TileMeshSource source = new TileMeshSource(gl, new PlaneTessellator());
                source.setTileSubdivisions(19);
                final VertexBufferCache<Tile> cache = new VertexBufferCache<Tile>(gl);
                final Tile tile = new Tile(0, 0, 0);

                SourceResponse<VertexBuffer> response;

                // Acquire Mesh
                response = source.requestObject(tile, null);
                assertEquals(response.response, SourceResponseType.SYNCHRONOUS);
                assertNotNull(response.value);

                VertexBuffer vbo = response.value;
                assertTrue(vbo.indices > 0);
                assertTrue(vbo.vertices > 0);
                assertEquals(vbo.primitiveType, GL_TRIANGLES);
                assertEquals(vbo.indexCount, 800);

                // Put into cache
                cache.putObject(tile, vbo);
                assertTrue(cache.getExistingObjects().iterator().hasNext());

                response = cache.requestObject(tile, null);
                assertEquals(response.response, SourceResponseType.SYNCHRONOUS);
                assertEquals(response.value, vbo);

                // Bind vertex buffer
                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo.vertices);
                assertEquals(gl.glGetError(), GL_NO_ERROR);

                // Set vertex / normal / texcoord pointers
                gl.glEnableClientState(GL_VERTEX_ARRAY);
                assertEquals(gl.glGetError(), GL_NO_ERROR);

                gl.glVertexPointer(3, GL_FLOAT, 8*4, 5*4);
                assertEquals(gl.glGetError(), GL_NO_ERROR);

                gl.glEnableClientState(GL_NORMAL_ARRAY);
                assertEquals(gl.glGetError(), GL_NO_ERROR);

                gl.glNormalPointer(GL_FLOAT, 8*4, 2*4);
                assertEquals(gl.glGetError(), GL_NO_ERROR);

                gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
                assertEquals(gl.glGetError(), GL_NO_ERROR);

                gl.glTexCoordPointer(2, GL_FLOAT, 8*4, 0);
                assertEquals(gl.glGetError(), GL_NO_ERROR);
                
                // Bind index buffer
                gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo.indices);
                assertEquals(gl.glGetError(), GL_NO_ERROR);

                // Draw
                gl.glDrawElements(vbo.primitiveType, vbo.indexCount, GL_UNSIGNED_INT, 0);
                assertEquals(gl.glGetError(), GL_NO_ERROR);

                // Disable pointers
                gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
                assertEquals(gl.glGetError(), GL_NO_ERROR);
                
                gl.glDisableClientState(GL_NORMAL_ARRAY);
                assertEquals(gl.glGetError(), GL_NO_ERROR);
                
                gl.glDisableClientState(GL_VERTEX_ARRAY);
                assertEquals(gl.glGetError(), GL_NO_ERROR);

                gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
                assertEquals(gl.glGetError(), GL_NO_ERROR);

                // Drop all meshes
                cache.dropAll();
                assertFalse(cache.getExistingObjects().iterator().hasNext());
            }
        });
    }
}
