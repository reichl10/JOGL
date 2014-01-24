package de.joglearth.map.osm;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import de.joglearth.map.TileName;
import de.joglearth.source.ProgressManager;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.PriorizedRunnableQueue;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.util.HTTP;
import de.joglearth.util.Resource;


/**
 * Loads OpenStreetMap image tiles by their coordinates via HTTP.
 */
public class OSMTileSource implements Source<TileName, byte[]> {

    private Map<OSMMapType, ServerSet> serverSets;
    private final ExecutorService executor;
    private PriorizedRunnableQueue queue;


    private class ServerSet {

        public String[] servers;

        public ServerSet(String[] servers) {
            this.servers = servers;
        }
    }


    private static byte[] loadLocalOSMTile(OSMMapType map, String type) {
        return Resource.loadBinary(String.format("osmLocal/%s-%s.%s", map, type,
                getImageFormatSuffix(map)));
    }

    /**
     * Constructor. Initializes the {@link de.joglearth.map.osm.OSMTileSource}.
     * 
     * @param servers An array containing the server strings
     */
    public OSMTileSource() {
        queue = new PriorizedRunnableQueue();
        executor = new ThreadPoolExecutor(4, 4, 0, TimeUnit.MILLISECONDS, queue);

        serverSets = new HashMap<>();
        serverSets.put(OSMMapType.CYCLING, new ServerSet(new String[] {
                "http://a.tile.opencyclemap.org/cycle/",
                "http://b.tile.opencyclemap.org/cycle/" }));
        serverSets.put(OSMMapType.HIKING, new ServerSet(new String[] {
                "http://a.www.toolserver.org/tiles/hikebike/",
                "http://b.www.toolserver.org/tiles/hikebike/",
                "http://c.www.toolserver.org/tiles/hikebike/" }));
        serverSets.put(OSMMapType.OSM_NOLABELS, new ServerSet(new String[] {
                "http://a.www.toolserver.org/tiles/osm-no-labels/",
                "http://b.www.toolserver.org/tiles/osm-no-labels/",
                "http://c.www.toolserver.org/tiles/osm-no-labels/" }));
        serverSets.put(OSMMapType.OSM2WORLD, new ServerSet(new String[] {
                "http://a.tiles.osm2world.org/osm/pngtiles/n/",
                "http://b.tiles.osm2world.org/osm/pngtiles/n/",
                "http://c.tiles.osm2world.org/osm/pngtiles/n/",
                "http://d.tiles.osm2world.org/osm/pngtiles/n/" }));
        serverSets.put(OSMMapType.MAPNIK, new ServerSet(new String[] {
                "http://otile1.mqcdn.com/tiles/1.0.0/osm/",
                "http://otile2.mqcdn.com/tiles/1.0.0/osm/" }));
        serverSets.put(OSMMapType.SATELLITE, new ServerSet(new String[] {
                "http://otile1.mqcdn.com/tiles/1.0.0/sat/",
                "http://otile2.mqcdn.com/tiles/1.0.0/sat/",
                "http://otile3.mqcdn.com/tiles/1.0.0/sat/",
                "http://otile4.mqcdn.com/tiles/1.0.0/sat/" }));

    }

    @Override
    public SourceResponse<byte[]> requestObject(final TileName k,
            final SourceListener<TileName, byte[]> sender) {
        
        if (k.configuration instanceof OSMMapConfiguration) {
            final OSMMapConfiguration configuration = (OSMMapConfiguration) k.configuration;
            if (k.tile instanceof OSMTile) {
                OSMTile tile = (OSMTile) k.tile;

                if (tile.getDetailLevel() == 0) {
                    return new SourceResponse<byte[]>(SourceResponseType.SYNCHRONOUS,
                            loadLocalOSMTile(configuration.getMapType(), "0"));
                } else {
                    ProgressManager.getInstance().requestArrived();
                    synchronized (executor) {
                        if (!executor.isShutdown()) {
                            executor.execute(new Runnable() {

                                @Override
                                public void run() {
                                    byte[] response = fetchRemoteTile((OSMTile) k.tile,
                                            configuration.getMapType());
                                    sender.requestCompleted(k, response);
                                    ProgressManager.getInstance().requestCompleted();
                                }
                            });

                        }
                    }
                    return new SourceResponse<byte[]>(SourceResponseType.ASYNCHRONOUS, null);
                }

            } else if (k.tile instanceof OSMPole) {
                OSMPole pole = (OSMPole) k.tile;
                return new SourceResponse<byte[]>(SourceResponseType.SYNCHRONOUS,
                        loadLocalOSMTile(configuration.getMapType(),
                                pole.getPole() == OSMPole.NORTH ? "north" : "south"));
            }
        }
        return new SourceResponse<byte[]>(SourceResponseType.MISSING, null);
    }

    
    private static void colorCorrectImage(BufferedImage img) {           
        Raster raster = img.getData();
        DataBuffer buffer = raster.getDataBuffer();
        if (buffer.getDataType() != DataBuffer.TYPE_BYTE) 
            System.err.println("Fuck");
        DataBufferByte bytebuf = (DataBufferByte) buffer;
        byte[] data = bytebuf.getData();
        System.out.println(bytebuf.getNumBanks());
        float[] hsb = new float[3];
        for (int j = 0; j < data.length; j += 3) {
            
            int red = data[j+2] < 0 ? 0x100 +data[j+2] : data[j+2];
            int green = data[j+1] < 0 ? 0x100 + data[j+1] : data[j+1];
            int blue = data[j] < 0 ? 0x100 + data[j] : data[j];

            green = (int) (green * 1.2);
            if (green > 255) green = 255;
                                    
            Color.RGBtoHSB(red, green, blue, hsb);
            
            hsb[1] += 0.1;
            if (hsb[1] > 1) hsb[1] = 1;
            hsb[2] -= 0.15;
            if (hsb[2] < 0) hsb[2] = 0;
            
            int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
            data[j] = (byte) (rgb & 0xff);
            data[j+1] = (byte) ((rgb >> 8) & 0xff);
            data[j+2] = (byte) ((rgb >> 16) & 0xff);
                
        }
        img.setData(raster);
    }
    
    
    private byte[] fetchRemoteTile(OSMTile tile, OSMMapType mapType) {

        // Spezialfall Kartenrand (Longitude)
        double lonFrom = tile.getLongitudeFrom();
        double lonTo = tile.getLongitudeTo();
        if (lonFrom > lonTo) {
            lonFrom -= Math.PI;
            lonTo -= Math.PI;
        }

        //Centered Point of the tile
        double x = ((lonFrom + lonTo) / 2) * 180 / Math.PI;

        int zoom = tile.getDetailLevel();

        int n = (int) Math.pow(2, zoom);
        int xtile = (int) (n * ((x + 180) / 360));

        if (xtile < 0) {
            xtile = 0;
        }
        if (xtile >= (1 << zoom)) {
            xtile = ((1 << zoom) - 1);
        }

        int ytile = tile.getLatitudeIndex();

        // Build URL
        ServerSet set = serverSets.get(mapType);
        StringBuilder builder = new StringBuilder();
        byte[] response = null;

        int i = 0;
        while (response == null && i < set.servers.length) {
            builder = new StringBuilder();

            builder.append(set.servers[i]);
            builder.append(zoom);
            builder.append("/");
            builder.append(xtile);
            builder.append("/");
            builder.append(ytile);
            builder.append(".");
            builder.append(getImageFormatSuffix(mapType));

            response = HTTP.get(builder.toString(), null);

            ++i;
        }
        
        
        if (response != null && mapType == OSMMapType.SATELLITE && tile.getDetailLevel() > 8) {
            BufferedImage img;
            try {
                img = ImageIO.read(new ByteArrayInputStream(response));
            } catch (IOException e) {
                return null;
            }
            
            colorCorrectImage(img);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            try {
                ImageIO.write(img, "jpg", out);
            } catch (IOException e) {
                return null;
            }
            
            response = out.toByteArray();
        }
        

        return response;
    }

    @Override
    public void dispose() {
        synchronized (executor) {
            executor.shutdownNow();
        }
    }

    /**
     * Returns the file suffix describing the format for a given map type.
     * 
     * @param mapType The map type
     * @return The suffix
     */
    public static String getImageFormatSuffix(OSMMapType mapType) {
        switch (mapType) {
            case MAPNIK:
            case SATELLITE:
                return "jpg";

            default:
                return "png";
        }
    }

    @Override
    public void increasePriority() {
        queue.increasePriority();
    }

}
