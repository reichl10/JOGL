package de.joglearth.junit.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;

import org.junit.Test;

import de.joglearth.util.HTTP;


public class HTTPTest {

    @Test
    public final void testPost() {
        byte[] data = HTTP.post("http://posttestserver.com/post.php?dump", "test1=testµtest");
        assertNotNull("PostRequest Failed", data);
        String string = new String(data, Charset.forName("UTF-8"));
        assertTrue("Whole PostRequest Failed.", string.contains("QUERY_STRING = dump"));
        assertTrue("Encoding failed.", string.contains("test1=test%C2%B5test"));
    }
    @Test
    public final void testGet() {
        byte[] data = HTTP.get("http://posttestserver.com/post.php?dump&test1=testµtest");
        assertNotNull("GetRequest Failed", data);
        String string = new String(data, Charset.forName("UTF-8"));
        assertTrue("Whole GetRequest failed.", string.contains("QUERY_STRING = "));
        assertTrue("Encoding failed.", string.contains("QUERY_STRING = dump&test1=test%C2%B5test"));
    }

}
