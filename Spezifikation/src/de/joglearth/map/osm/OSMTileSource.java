package de.joglearth.map.osm;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import de.joglearth.map.TileName;
import de.joglearth.source.ProgressManager;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
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
        executor = Executors.newFixedThreadPool(4);

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
                    executor.execute(new Runnable() {

                        @Override
                        public void run() {
                            byte[] response = fetchRemoteTile((OSMTile) k.tile,
                                    configuration.getMapType());
                            sender.requestCompleted(k, response);
                            ProgressManager.getInstance().requestCompleted();
                        }
                    });
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

    private byte[] fetchRemoteTile(OSMTile tile, OSMMapType mapType) {

        // Spezialfall Kartenrand (Longitude)
        double lonFrom = tile.getLongitudeFrom();
        double lonTo = tile.getLongitudeTo();
        if (lonFrom > lonTo) {
            lonFrom -= Math.PI;
            lonTo -= Math.PI;
        }

        // Mittelpunkt der Kachel
        double x = ((lonFrom + lonTo) / 2) * 180 / Math.PI;

        /*
         * n = 2 ^ zoom
         * 
         * xtile = n * ((lon_deg + 180) / 360)
         * 
         * ytile = n * (1 - (log(tan(lat_rad) + sec(lat_rad)) / π)) / 2
         * 
         * sec = 1/cos(lat_rad)
         * 
         * Als ytile kann der latitudeIndex der Tile verwendet werden.
         */
        int zoom = tile.getDetailLevel();

        int n = (int) Math.pow(2, zoom);
        int xtile = (int) (n * ((x + 180) / 360));

        // Blödsinn den die Formel macht abfangen
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

        return response;
    }

    @Override
    public void dispose() {
        executor.shutdownNow();
    }

    /**
     * TODO
     * @param mapType
     * @return
     */
    public static String getImageFormatSuffix(OSMMapType mapType) {
        switch (mapType) {
            case MAPNIK:
                return "jpg";

            default:
                return "png";
        }
    }

    //TODO: Vorlage für prio
    private class LIFOBlockingDeque<C> extends LinkedBlockingDeque<C> {

        @Override
        public boolean offer(C e) {
            return super.offerFirst(e);
        }

        @Override
        public boolean offer(C e, long timeout, TimeUnit unit) throws InterruptedException {
            return super.offerFirst(e, timeout, unit);
        }

        @Override
        public boolean add(C e) {
            return super.offerFirst(e);
        }

        @Override
        public void put(C e) throws InterruptedException {
            super.putFirst(e);
        }
    }
}
