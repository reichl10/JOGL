package de.joglearth.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;


/**
 * 
 * Provides the communication via HTTP to get the OpenStreetMap data, NASA SRTM data etc.; offers
 * two static methods (GET, POST) for HTTP queries.
 * 
 */
public final class HTTP {

    private static int HTTP_OK = 200;

    // Constructor
    private HTTP() {
    }

    /**
     * Gathers information via a HTTP get-request and is used for synchronous HTTP queries.
     * 
     * @param url Address of a server
     * @param getRequest An <code>Iterable</code> of get parameters in the form {key1, value1, key2,
     *        value2, ...}
     * @return Content of the HTTP response. Null if no content is available or a HTTP Error
     *         occurred.
     * @throws IllegalArgumentException If the url is not well formed, e.g. <code>null</code>
     */
    public static byte[] get(String url, Iterable<String> getRequest)
            throws IllegalArgumentException {
        System.err.println("HTTP: beginning GET request for " + url);
        if (url == null) {
            throw new IllegalArgumentException("URL was null!");
        }

        if (url.contains("?")) {
            throw new IllegalArgumentException("Not wellformed URL");
        }

        StringBuilder request = new StringBuilder();
        if (getRequest != null) {
            int i = 0;
            for (String s : getRequest) {
                try {
                    if (i == 0) {
                        request.append('?');
                    } else if (i % 2 == 0) {
                        request.append('&');
                    }
                    request.append(URLEncoder.encode(s, "UTF-8"));
                    if (i % 2 == 0) {
                        request.append('=');
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalArgumentException("Request not well formed");
                }
                i++;
            }
        }

        url = url + request.toString();
                
        URL serverUrl;
        try {
            serverUrl = new URL(url);
        } catch (MalformedURLException mue) {
            throw new IllegalArgumentException("Not an URL!");
        }

        /* Establishing connection and HTTP exception handling */
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) serverUrl.openConnection();
            if (connection.getResponseCode() != HTTP_OK) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }

        /* Handle InputStream */
        byte[] response = null;
        byte[] buffer = new byte[4096];
        try {
            InputStream in = connection.getInputStream();
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    /* Value -1 shows the end of the HTTP content */
                    int n = -1;
        
                    while ((n = in.read(buffer)) != -1) {
                        out.write(buffer, 0, n);
                    }
        
                    response = out.toByteArray();
                } finally {
                    out.close();
                } 
            } finally {
                in.close();
            }
        } catch (IOException ioe) {
            return null;
        }

        return response;
    }

    /**
     * Creates a HTTP post-request and is used for synchronous HTTP queries.
     * 
     * @param url Address of a server
     * @param request Post request. Not allowed to be <code>null</code>;
     * @return Content of the HTTP response. Null if no content is available or a HTTP Error
     *         occurred.
     * @throws IllegalArgumentException If the url or request is not well formed, e.g.
     *         <code>null</code>
     */
    public static byte[] post(String url, Iterable<String> getRequest, Iterable<String> postRequest)
            throws IllegalArgumentException {

        if (url == null) {
            throw new IllegalArgumentException("URL was null!");
        }

        if (url.contains("?")) {
            throw new IllegalArgumentException("Not wellformed URL");
        }

        if (postRequest == null) {
            throw new IllegalArgumentException("Post request was null");
        }

        StringBuilder request = new StringBuilder();
        if (getRequest != null) {
            int i = 0;
            for (String s : getRequest) {
                try {
                    if (i == 0) {
                        request.append('?');
                    } else if (i % 2 == 0) {
                        request.append('&');
                    }
                    request.append(URLEncoder.encode(s, "UTF-8"));
                    if (i % 2 == 0) {
                        request.append('=');
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalArgumentException("Request not well formed");
                }
                i++;
            }
        }

        StringBuilder post = new StringBuilder();
        int i = 0;
        for (String s : postRequest) {
            try {
                if (i % 2 == 0 && i > 0) {
                    post.append('&');
                }
                post.append(URLEncoder.encode(s, "UTF-8"));
                if (i % 2 == 0) {
                    post.append('=');
                }
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Request not well formed");
            }
            i++;
        }

        url = url + request.toString();

        URL serverUrl;
        try {
            serverUrl = new URL(url);
        } catch (MalformedURLException mue) {
            throw new IllegalArgumentException("Not an URL!");
        }

        /* Establishing connection */
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) serverUrl.openConnection();
        } catch (IOException e) {
            return null;
        }

        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException pe) {
            return null;
        }
        connection.setDoOutput(true);

        /* Handle OutputStream */
        try {
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            try {
                dos.writeBytes(post.toString());
                dos.flush();
            } finally {
                dos.close();
            }
        } catch (IOException e) {
            return null;
        }

        /* HTTP exception handling */
        try {
            if (connection.getResponseCode() != HTTP_OK) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }

        /* Handle InputStream */
        byte[] response = null;
        byte[] buffer = new byte[4096];
        try {
            InputStream in = connection.getInputStream();
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
        
                    /* Value -1 shows the end of the HTTP content */
                    int n = -1;
        
                    while ((n = in.read(buffer)) != -1) {
                        out.write(buffer, 0, n);
                    }
        
                    response = out.toByteArray();
                } finally {
                    out.close();
                }
            } finally {
                in.close();
            }
        } catch (IOException ioe) {
            return null;
        }

        return response;
    }
}
