package de.joglearth.source.srtm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import sun.misc.IOUtils;

import com.jogamp.common.util.IOUtil;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.util.HTTP;
import de.joglearth.util.Resource;


/**
 * Loads SRTM data from NASA servers. The level of detail of a pixel in the SRTM tiles is 90 x 90
 * meters. The SRTM tiles include all information of a required point about the height. As 'standard
 * elevation zero' the WGS84 spheroid is used. Only necessary if the HightProfile is activated.
 * 
 */
public class SRTMBinarySource implements Source<SRTMTileIndex, byte[]> {

    private final static String serverURL = "http://dds.cr.usgs.gov/srtm/version2_1/SRTM3/";

    private final static Map<String, String> tileRegionMap = Resource.loadCSVMap("srtm_map.csv",
            "\\s");

    private ExecutorService executor;


    /**
     * Constructor. Initializes the {@link de.joglearth.source.srtm.SRTMTileSource}.
     * 
     */
    public SRTMBinarySource() {
        executor = Executors.newFixedThreadPool(2);
    }

    @Override
    public SourceResponse<byte[]> requestObject(final SRTMTileIndex key,
            final SourceListener<SRTMTileIndex, byte[]> sender) {
        
        if (key == null) { 
            throw new IllegalArgumentException();
        }

        String region = tileRegionMap.get(key.toString());
        if (region == null) {
            return new SourceResponse<byte[]>(SourceResponseType.MISSING, null);
        }
        
        final String url = serverURL + tileRegionMap.get(key.toString()) + "/" + key.toString()
                + ".hgt.zip";
        
        /*executor.execute(*/new Runnable() {
            @Override
            public void run() {
                byte[] zipBytes = HTTP.get(url);

                if (zipBytes == null) {
                    System.err.println("Loading SRTM server data for " + key.toString() + " failed");
                }

                byte[] tileBytes = null;
                if (zipBytes != null) {
                    try {
                        ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(zipBytes));
                        tileBytes = new byte[1201 * 1201 * 2];
                        if (zip.read(tileBytes) != tileBytes.length) {
                            tileBytes = null;
                        }
                    } catch (IOException e) {
                        tileBytes = null;
                    }
                }

                if (zipBytes != null && tileBytes == null) {
                    System.err.println("Loading SRTM data from archive failed for "
                            + key.toString());
                }

                sender.requestCompleted(key, tileBytes);
            }
        }.run();
        
        return new SourceResponse<byte[]>(SourceResponseType.ASYNCHRONOUS, null);
    }
}
