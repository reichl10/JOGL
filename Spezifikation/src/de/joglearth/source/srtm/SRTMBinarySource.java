package de.joglearth.source.srtm;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.util.HTTP;


/**
 * Loads SRTM data from NASA servers. The level of detail of a pixel in the SRTM tiles is 90 x 90
 * meters. The SRTM tiles include all information of a required point about the height. As 'standard
 * elevation zero' the WGS84 spheroid is used. Only necessary if the HightProfile is activated.
 * 
 */
public class SRTMBinarySource implements Source<SRTMTileIndex, byte[]> {

    private static final int SMALLEST_NUMBER_WITH_THREE_DIGITS = 100;
    private static final int SMALLEST_NUMBER_WITH_TWO_DIGITS = 10;
    private final String server = "http://dds.cr.usgs.gov/srtm/version2_1/SRTM3/";


    /**
     * Constructor. Initializes the {@link de.joglearth.source.srtm.SRTMTileSource}.
     * 
     */
    public SRTMBinarySource() {}

    @Override
    public SourceResponse<byte[]> requestObject(SRTMTileIndex key,
            SourceListener<SRTMTileIndex, byte[]> sender) {

        byte[] response = null;

        // TODO ExecutorService?!
        response = getSRTMTile(key);

        if (sender != null) {
            sender.requestCompleted(key, response);
        }

        return new SourceResponse<byte[]>(SourceResponseType.ASYNCHRONOUS, response);
    }

    private byte[] getSRTMTile(SRTMTileIndex tile) {

        String[] url = getUrl(tile.longitude, tile.latitude);

        for (int i = 0; i < url.length; ++i) {

            if (url[i] == null) {
                return null;
            }

            byte[] response = HTTP.get(url[i], null);

            if (response != null) {
                return response;
            }

        }

        return null;
    }

    /*
     * Nordamerika: W170 - W040; N15 - N60.
     * 
     * Südamerika: W095 - W030; N10 - S60.
     * 
     * Europa und Asien: W015 - E180 && W180 - W135; N60 - S15.
     * 
     * Afrika: W035 - E65; N40 - S35.
     * 
     * Australien: E110 - E180 && W180 - W105; S10 - S45.
     * 
     * Alle mit einer Auflösung von 3 Bogensekunden (SRTM-3).
     * 
     * Wenn nichts gefunden wird, dann immer noch bei den Inseln gucken.
     * 
     * Äquator spezifiziert durch N00, Längengrad 0 spezifiziert durch E000.
     */
    private String[] getUrl(int longitude, int latitude) {
        int offset = 0;
        //TODO Parse Lon/Lat
        int lon = longitude; // E or -W
        int lat = latitude; // N or -S
        String[] url = new String[6];

        StringBuilder builder = null;

        if (lon >= -170 && lon <= -40 && lat >= 15 && lat <= 60) {
            // Nordamerika

            builder = new StringBuilder(server);

            builder.append("North_America/");

            parseLat(builder, lat);
            parseLon(builder, lon);

            builder.append(".hgt.zip");

            url[offset] = builder.toString();

            offset++;

        } else if (lon >= -95 && lon <= -30 && lat <= 10 && lat >= -60) {
            // Südamerika

            builder = new StringBuilder(server);

            builder.append("South_America/");

            parseLat(builder, lat);
            parseLon(builder, lon);

            builder.append(".hgt.zip");

            url[offset] = builder.toString();

            offset++;

        } else if (((lon >= -15 && lon <= 180) || (lon >= -180 && lon <= -135)) && lat <= 60
                && lat >= -15) {
            // Europa und Asien

            builder = new StringBuilder(server);

            builder.append("Eurasia/");

            parseLat(builder, lat);
            parseLon(builder, lon);

            builder.append(".hgt.zip");

            url[offset] = builder.toString();

            offset++;

        } else if (lon >= -35 && lon <= 65 && lat <= 40 && lat >= -35) {
            // Afrika

            builder = new StringBuilder(server);

            builder.append("Africa/");

            parseLat(builder, lat);
            parseLon(builder, lon);

            builder.append(".hgt.zip");

            url[offset] = builder.toString();

            offset++;

        } else if (((lon >= 110 && lon <= 180) || (lon >= -180 && lon <= -105)) && lat <= -10
                && lat >= -45) {
            // Australien

            builder = new StringBuilder(server);

            builder.append("Australia/");

            parseLat(builder, lat);
            parseLon(builder, lon);

            builder.append(".hgt.zip");

            url[offset] = builder.toString();

            offset++;

        } else {

            builder = new StringBuilder(server);

            builder.append("Islands/");

            parseLat(builder, lat);
            parseLon(builder, lon);

            builder.append(".hgt.zip");

            url[offset] = builder.toString();

            offset++;

        }

        return url;
    }

    private void parseLon(StringBuilder builder, int lon) {
        if (Math.abs(lon) < SMALLEST_NUMBER_WITH_TWO_DIGITS) {
            if (lon < 0) {
                builder.append("W00");
            } else {
                builder.append("E00");
            }

        } else if (lon < SMALLEST_NUMBER_WITH_THREE_DIGITS) {
            if (lon < 0) {
                builder.append("W0");
            } else {
                builder.append("E0");
            }

        } else {
            if (lon < 0) {
                builder.append("W");
            } else {
                builder.append("E");
            }
        }

        builder.append(Math.abs(lon));
    }

    private void parseLat(StringBuilder builder, int lat) {
        if (Math.abs(lat) < SMALLEST_NUMBER_WITH_TWO_DIGITS) {
            if (lat < 0) {
                builder.append("S0");
            } else {
                builder.append("N0");
            }

        } else {
            if (lat < 0) {
                builder.append("S");
            } else {
                builder.append("N");
            }
        }

        builder.append(Math.abs(lat));
    }
}
