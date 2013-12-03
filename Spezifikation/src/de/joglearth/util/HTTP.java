package de.joglearth.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 * 
 * Provides the communication via HTTP to get the OpenStreetMap data, NASA SRTM data etc.; offers
 * two static methods (GET, POST) for HTTP queries.
 * 
 */
public final class HTTP {

    private static int HTTP_OK = 200;


    private HTTP() {

    }

    /**
     * Gathers information via a HTTP get-request and is used for synchronous HTTP queries.
     * 
     * @param url Address of a server
     * @return Content of the HTTP response
     */
    public static byte[] get(String url) {
        if (url == null) {
            throw new IllegalArgumentException("URL was null!");
        }

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
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            /* Value -1 shows the end of the HTTP content */
            int n = -1;

            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }

            response = out.toByteArray();

            in.close();
        } catch (IOException ioe) {
            return null;
        }

        return response;
    }

    /**
     * Creates a HTTP post-request and is used for synchronous HTTP queries.
     * 
     * @param url Address of a server
     * @param request Post request
     * @return Content of the HTTP response
     */
    public static byte[] post(String url, String request) {
        if (url == null) {
            throw new IllegalArgumentException("URL was null!");
        }

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
        DataOutputStream dos;
        try {
            dos = new DataOutputStream(connection.getOutputStream());
            dos.writeBytes(request);
            dos.flush();
            dos.close();
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
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            /* Value -1 shows the end of the HTTP content */
            int n = -1;

            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }

            response = out.toByteArray();

            in.close();
        } catch (IOException ioe) {
            return null;
        }

        return response;
    }
}
