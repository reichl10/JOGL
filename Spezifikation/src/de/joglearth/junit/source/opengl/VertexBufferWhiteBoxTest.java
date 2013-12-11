package de.joglearth.junit.source.opengl;

import static org.junit.Assert.*;
import static javax.media.opengl.GL2.*;

import javax.media.opengl.GL2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.joglearth.geometry.Tile;
import de.joglearth.junit.GLTestWindow;
import de.joglearth.rendering.PlaneTessellator;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.source.opengl.TileMeshSource;
import de.joglearth.source.opengl.VertexBuffer;
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
    public void test() {
        final GL2 gl = window.getGL();
        final TileMeshSource source = new TileMeshSource(gl, new PlaneTessellator());
        source.setTileSubdivisions(19);
        final VertexBufferCache<Tile> cache = new VertexBufferCache<Tile>(gl);
        final Tile tile = new Tile(0, 0, 0);

        SourceResponse<VertexBuffer> response;

        response = (SourceResponse<VertexBuffer>) AWTInvoker.invoke(new RunnableWithResult() {

            @Override
            public Object run() {
                return source.requestObject(tile, null);
            }
        });
        assertEquals(response.response, SourceResponseType.SYNCHRONOUS);
        assertNotNull(response.value);

        final VertexBuffer vbo = response.value;
        assertTrue(vbo.indices > 0);
        assertTrue(vbo.vertices > 0);
        assertEquals(vbo.primitiveType, GL_TRIANGLES);
        assertEquals(vbo.primitiveCount, 400);

        cache.putObject(tile, vbo);
        assertTrue(cache.getExistingObjects().iterator().hasNext());

        response = cache.requestObject(tile, null);
        assertEquals(response.response, SourceResponseType.SYNCHRONOUS);
        assertEquals(response.value, vbo);

        AWTInvoker.invoke(new Runnable() {
            @Override
            public void run() {                
                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo.vertices);
                assertEquals(gl.glGetError(), GL_NO_ERROR);

                gl.glEnableClientState(GL_VERTEX_ARRAY);
                assertEquals(gl.glGetError(), GL_NO_ERROR);

                gl.glVertexPointer(3, GL_FLOAT, 0, 0);
                assertEquals(gl.glGetError(), GL_NO_ERROR);

                gl.glDrawElements(vbo.primitiveType, vbo.primitiveCount, GL_UNSIGNED_INT, vbo.indices);
                assertEquals(gl.glGetError(), GL_NO_ERROR);

                gl.glDisableClientState(GL_VERTEX_ARRAY);
                assertEquals(gl.glGetError(), GL_NO_ERROR);

                gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
                assertEquals(gl.glGetError(), GL_NO_ERROR);

                cache.dropAll();
                assertFalse(cache.getExistingObjects().iterator().hasNext());

                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo.indices);
                assertNotEquals(gl.glGetError(), GL_NO_ERROR);

                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo.vertices);
                assertNotEquals(gl.glGetError(), GL_NO_ERROR);                
            }
        });
    }
}
