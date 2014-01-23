package de.joglearth.junit.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.util.ArrayList;

import org.junit.Test;

import de.joglearth.util.HTTP;


public class HTTPTest {

    @Test
    public final void testPost() {
        ArrayList<String> get = new ArrayList<String>();
        get.add("dumb");
        get.add("");
        ArrayList<String> post = new ArrayList<String>();
        post.add("test1"); post.add("testµtest");
        byte[] data = HTTP.post("http://posttestserver.com/post.php", get, post);
        assertNotNull("PostRequest Failed", data);
        String string = new String(data, Charset.forName("UTF-8"));
        assertTrue("Whole PostRequest Failed.", string.contains("QUERY_STRING = dump"));
        assertTrue("Encoding failed.", string.contains("test1=test%C2%B5test"));
    }
    @Test
    public final void testGet() {
        ArrayList<String> get = new ArrayList<String>();
        get.add("dumb");
        get.add("");
        get.add("test1"); get.add("testµtest");
        byte[] data = HTTP.get("http://posttestserver.com/post.php", get);
        assertNotNull("GetRequest Failed", data);
        String string = new String(data, Charset.forName("UTF-8"));
        assertTrue("Whole GetRequest failed.", string.contains("QUERY_STRING"));
        assertTrue("Encoding failed.", string.contains("QUERY_STRING = dump&test1test%C2%B5test"));
    }

}
