package de.joglearth.junit.source.opengl;

import static org.junit.Assert.*;
import static javax.media.opengl.GL2.*;

import java.awt.EventQueue;

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

    @Test
    public void test() {
        GL2 gl = window.getGL();
        TileMeshSource source = new TileMeshSource(gl, new PlaneTessellator());
        source.setTileSubdivisions(19);
        VertexBufferCache<Tile> cache = new VertexBufferCache<Tile>(gl);
        Tile tile = new Tile(0, 0, 0);
        
        SourceResponse<VertexBuffer> response = source.requestObject(tile, null);        
        assertEquals(response.response, SourceResponseType.SYNCHRONOUS);
        assertNotNull(response.value);
        
        VertexBuffer vbo = response.value;
        assertTrue(vbo.indices > 0);
        assertTrue(vbo.vertices > 0);
        assertEquals(vbo.primitiveType, GL_TRIANGLES);
        assertEquals(vbo.primitiveCount, 400);
        
        cache.putObject(tile, vbo);
        assertTrue(cache.getExistingObjects().iterator().hasNext());
        
        response = cache.requestObject(tile, null);  
        assertEquals(response.response, SourceResponseType.SYNCHRONOUS);
        assertEquals(response.value, vbo);

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

}
