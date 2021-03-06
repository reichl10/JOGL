package de.joglearth.height.srtm;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.joglearth.source.Priorized;
import de.joglearth.source.ProgressManager;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.PriorizedRunnableQueue;
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
public class SRTMBinarySource implements Source<SRTMTileName, byte[]>, Priorized {

    private final static String serverURL = "http://dds.cr.usgs.gov/srtm/version2_1/SRTM3/";
    private final static Map<String, String> tileRegionMap = Resource.loadCSVMap("srtm_map.csv",
            "\\s");
    private PriorizedRunnableQueue queue;
    private ExecutorService executor;


    /**
     * Constructor. Initializes the {@link de.joglearth.height.srtm.SRTMTileSource}.
     * 
     */
    public SRTMBinarySource() {
        queue = new PriorizedRunnableQueue();
        executor = new ThreadPoolExecutor(2, 2, 0, TimeUnit.MILLISECONDS, queue);
    }

    @Override
    public SourceResponse<byte[]> requestObject(final SRTMTileName key,
            final SourceListener<SRTMTileName, byte[]> sender) {

        if (key == null) {
            throw new IllegalArgumentException();
        }

        ProgressManager.getInstance().requestArrived();

        String region = tileRegionMap.get(key.toString());
        if (region == null) {
            return new SourceResponse<byte[]>(SourceResponseType.MISSING, null);
        }

        /* builds the url to gets the srtm data from internet */
        final String url = serverURL + tileRegionMap.get(key.toString()) + "/" + key.toString()
                + ".hgt.zip";

        synchronized (executor) {
            if (!executor.isShutdown()) {
                executor.execute(new Runnable() {
        
                    @Override
                    public void run() {
                        byte[] zipBytes = HTTP.get(url, null);
        
                        if (zipBytes == null) {
                            System.err.println("Loading SRTM server data for "
                                    + key.toString() + " failed");
                       }
        
                        sender.requestCompleted(key, zipBytes);
                        ProgressManager.getInstance().requestCompleted();
                    }
                });
            }
        }
        
        return new SourceResponse<byte[]>(SourceResponseType.ASYNCHRONOUS, null);
    }

    @Override
    public void dispose() {
        synchronized (executor) {
            executor.shutdownNow();
        }
    }

    @Override
    public void increasePriority() {
        queue.increasePriority();
    }

}
