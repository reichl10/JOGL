package de.joglearth.util;

/**
 * 
 * Provides the communication via HTTP to get the OpenStreetMap data, NASA SRTM data etc.; offers
 * two static methods (GET, POST) for HTTP queries.
 * 
 */
public final class HTTP {

    /**
     * Gathers information via a HTTP get-request and is used for synchronous HTTP queries.
     * 
     * @param url Address of a server
     * @return Content of the HTTP response
     */
    public static byte[] get(String url) {
        return null;
    }

    /**
     * Creates a HTTP post-request and is used for synchronous HTTP queries.
     * 
     * @param url Address of a server
     * @param request Post request
     * @return Content of the HTTP response
     */
    public static byte[] post(String url, String request) {
        return null;
    }
}