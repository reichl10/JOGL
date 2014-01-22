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

    private class ServerSet {

        public String[] servers;


        public ServerSet(String[] servers) {
            this.servers = servers;
        }
    }


    private Map<OSMMapType, ServerSet> serverSets;
    
    private final ExecutorService executor;
    
    
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
        //executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new LIFOBlockingDeque<Runnable>());
        
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

    private byte[] fetchRemoteTile(OSMTile tile, OSMMapType mapType) {
        // TODO System.err.println("OSMTileSource: loading " + tile + " with type " +
        // type.toString());

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

        // TODO
        // //TODO System.out.println("ytile: "+ytile+" latitude: "+y);

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
            /*
             * if (response == null) { set.offset++; }
             * 
             * if (set.offset == set.servers.length) { set.offset = 0; }
             */
        }

        // TODO System.err.println("OSMTileSource: done " + (response == null ? "(null) " : "")
        // + "loading " + tile);

        return response;
    }

    /*
     * public static void main(String[] args) { // Tile tile1 = new Tile(2, 0, 0); // Tile tile2 =
     * new Tile(2, 0, 1); // Tile tile3 = new Tile(2, 0, 2); // Tile tile4 = new Tile(2, 0, 3); //
     * Tile tile5 = new Tile(2, 1, 0); // Tile tile6 = new Tile(2, 1, 1); // Tile tile7 = new
     * Tile(2, 1, 2); // Tile tile8 = new Tile(2, 1, 3); // Tile tile9 = new Tile(2, 2, 0); // Tile
     * tile10 = new Tile(2, 2, 1); // Tile tile11 = new Tile(2, 2, 2); // Tile tile12 = new Tile(2,
     * 2, 3); // Tile tile13 = new Tile(2, 3, 0); // Tile tile14 = new Tile(2, 1, 1); // Tile tile15
     * = new Tile(2, 3, 2); // Tile tile16 = new Tile(2, 3, 3); // // OSMTile osm1 = new
     * OSMTile(tile1, TiledMapType.OSM_MAPNIK); // OSMTile osm2 = new OSMTile(tile2,
     * TiledMapType.OSM_MAPNIK); // OSMTile osm3 = new OSMTile(tile3, TiledMapType.OSM_MAPNIK); //
     * OSMTile osm4 = new OSMTile(tile4, TiledMapType.OSM_MAPNIK); // OSMTile osm5 = new
     * OSMTile(tile5, TiledMapType.OSM_MAPNIK); // OSMTile osm6 = new OSMTile(tile6,
     * TiledMapType.OSM_MAPNIK); // OSMTile osm7 = new OSMTile(tile7, TiledMapType.OSM_MAPNIK); //
     * OSMTile osm8 = new OSMTile(tile8, TiledMapType.OSM_MAPNIK); // OSMTile osm9 = new
     * OSMTile(tile9, TiledMapType.OSM_MAPNIK); // OSMTile osm10 = new OSMTile(tile10,
     * TiledMapType.OSM_MAPNIK); // OSMTile osm11 = new OSMTile(tile11, TiledMapType.OSM_MAPNIK); //
     * OSMTile osm12 = new OSMTile(tile12, TiledMapType.OSM_MAPNIK); // OSMTile osm13 = new
     * OSMTile(tile13, TiledMapType.OSM_MAPNIK); // OSMTile osm14 = new OSMTile(tile14,
     * TiledMapType.OSM_MAPNIK); // OSMTile osm15 = new OSMTile(tile15, TiledMapType.OSM_MAPNIK); //
     * OSMTile osm16 = new OSMTile(tile16, TiledMapType.OSM_MAPNIK);
     * 
     * OSMTileSource source = new OSMTileSource(); final int zoom = 5; int n = (int) Math.pow(2,
     * zoom);
     * 
     * for (int i = 0; i < n; i++) { OSMTile tile = new OSMTile(zoom, n/2 , i); TileName osm = new
     * TileName(tile, OSMMapType.MAPNIK); source.requestObject(osm, new TestRequester());
     * 
     * }
     * 
     * // source.requestObject(osm1, new TestRequester()); // source.requestObject(osm2, new
     * TestRequester()); // source.requestObject(osm3, new TestRequester()); //
     * source.requestObject(osm4, new TestRequester()); // source.requestObject(osm5, new
     * TestRequester()); // source.requestObject(osm6, new TestRequester()); //
     * source.requestObject(osm7, new TestRequester()); // source.requestObject(osm8, new
     * TestRequester()); // source.requestObject(osm9, new TestRequester()); //
     * source.requestObject(osm10, new TestRequester()); // source.requestObject(osm11, new
     * TestRequester()); // source.requestObject(osm12, new TestRequester()); //
     * source.requestObject(osm13, new TestRequester()); // source.requestObject(osm14, new
     * TestRequester()); // source.requestObject(osm15, new TestRequester()); //
     * source.requestObject(osm16, new TestRequester());
     * 
     * // //TODO System.out.println(Math.cos(((1 - Math.log(Math.tan(Math.PI/2) + // 1 /
     * Math.cos(Math.PI/2)) / Math.PI) /2 * Math.pow(2, 6)))); // //TODO
     * System.out.println(Math.cos(((1 - Math.log(Math.tan(Math.PI/2) + //
     * Math.sqrt((Math.tan(Math.PI/2)) + 1)) / Math.PI) /2 * Math.pow(2, 6))));
     * 
     * }
     * 
     * 
     * static class TestRequester implements SourceListener<TileName, byte[]> {
     * 
     * public TestRequester() {
     * 
     * }
     * 
     * @Override public void requestCompleted(TileName key, byte[] value) { // TODO Auto-generated
     * method stub
     * 
     * } }
     */

    @Override
    public void dispose() {
        synchronized (executor) {
            executor.shutdownNow();
        }
    }

    public static String getImageFormatSuffix(OSMMapType mapType) {
        switch (mapType) {
            case MAPNIK: 
                return "jpg";
                
            default: 
                return "png";
        }
    }

    private class LIFOBlockingDeque <C> extends LinkedBlockingDeque<C> {
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
