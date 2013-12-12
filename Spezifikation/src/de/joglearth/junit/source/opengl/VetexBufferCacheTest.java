package de.joglearth.junit.source.opengl;

import static org.junit.Assert.*;

import org.junit.Test;

import de.joglearth.junit.GLTestWindow;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.source.opengl.VertexBuffer;
import de.joglearth.source.opengl.VertexBufferCache;


public class VetexBufferCacheTest {
    GLTestWindow window = new GLTestWindow();
    @Test
    public void testDropObject() {
        VertexBufferCache<Integer> vbc = new VertexBufferCache<Integer>(window.getGL());
        vbc.putObject(new Integer(10), new VertexBuffer());
        SourceResponse<VertexBuffer> res = vbc.requestObject(new Integer(10), null);
        assertTrue(res.response == SourceResponseType.SYNCHRONOUS);
        assertNotNull(res.value);
        vbc.dropObject(new Integer(10));
        SourceResponse<VertexBuffer> res2 = vbc.requestObject(new Integer(10), new SourceListener<Integer, VertexBuffer>() {
            
            @Override
            public void requestCompleted(Integer key, VertexBuffer value) {
                return;
            }
        });
        assertTrue(res2.response == SourceResponseType.ASYNCHRONOUS);
        assertNull(res2.value);
    }

}
