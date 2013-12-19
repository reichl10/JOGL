package de.joglearth.source.osm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.joglearth.geometry.Tile;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.surface.MapLayout;
import de.joglearth.surface.SingleMapType;
import de.joglearth.surface.TiledMapType;
import de.joglearth.util.HTTP;


/**
 * Loads OpenStreetMap image tiles by their coordinates via HTTP.
 */
public class OSMTileSource implements Source<OSMTile, byte[]> {

    private String[] servers;
    private TiledMapType type;
    private int offset = 0;
    private final ExecutorService executor;


    /**
     * Constructor. Initializes the {@link de.joglearth.source.osm.OSMTileSource}.
     * 
     * @param servers An array containing the server strings
     */
    public OSMTileSource() {
        this.servers = new String[0];
        executor = Executors.newFixedThreadPool(2);
    }

    @Override
    public SourceResponse<byte[]> requestObject(final OSMTile k,
            final SourceListener<OSMTile, byte[]> sender) {

        if (k.type != type) {
            setTileType(k.type);
        }

        executor.execute(new Runnable() {

            @Override
            public void run() {
                byte[] response = getOSMTile(k.tile);

                sender.requestCompleted(k, response);
            }
        });

        return new SourceResponse<byte[]>(SourceResponseType.ASYNCHRONOUS, null);
    }

    private byte[] getOSMTile(Tile tile) {
        System.err.println("OSMTileSource: loading " + tile + " with type " + type.toString());

        double lonFrom = tile.getLongitudeFrom();
        double lonTo = tile.getLongitudeTo();
        if(lonFrom > lonTo) {
            lonFrom -= Math.PI;
            lonTo -= Math.PI;
        }
        double y = ((tile.getLatitudeFrom() + tile.getLatitudeTo()) / 2);
        double x = ((lonFrom + lonTo) / 2) *180 / Math.PI;

        
        int zoom = tile.getDetailLevel();

        int n = (int) Math.pow(2, zoom);
        int xtile = (int) (n * ((x + 180) / 360));
        int ytile = (int) (n * (1 - (Math.log(Math.tan(y) + 1 / Math.cos(y)) / Math.PI)) / 2);

        StringBuilder builder = new StringBuilder();
        byte[] response = null;

        int i = 0;
        while (response == null && i < servers.length) {
            builder.append(servers[offset]);
            builder.append(zoom);
            builder.append("/");
            builder.append(xtile);
            builder.append("/");
            builder.append(ytile);
            builder.append(".png");

            response = HTTP.get(builder.toString(), null);

            i++;

            if (response == null) {
                offset++;
            }

            if (offset == servers.length) {
                offset = 0;
            }
        }
        System.err.println("OSMTileSource: done " + (response == null ? "(null) " : "") + "loading " + tile);

        return response;
    }

    /**
     * Sets the type of an OpenStreetMap tile.
     * 
     * @param type <code>TiledMapType</code> of the tile
     */
    public void setTileType(TiledMapType type) {
        if (this.type != type) {
            this.type = type;
            this.servers = getServer(type);
        }
    }

    // TODO evtl. woanders hinschieben
    private String[] getServer(TiledMapType type) {

        String[] cycling = { "http://a.tile.opencyclemap.org/cycle/",
                "http://b.tile.opencyclemap.org/cycle/" };
        String[] hiking = { "http://tile.waymarkedtrails.org/hiking/" };
        String[] osm2world = { "http://a.tiles.osm2world.org/osm/pngtiles/n/",
                "http://b.tiles.osm2world.org/osm/pngtiles/n/",
                "http://c.tiles.osm2world.org/osm/pngtiles/n/",
                "http://d.tiles.osm2world.org/osm/pngtiles/n/" };
        String[] mapnik = { "http://otile1.mqcdn.com/tiles/1.0.0/osm/",
                "http://otile2.mqcdn.com/tiles/1.0.0/osm/" };
        String[] skiing = { "http://tiles.openpistemap.org/nocontours/" };

        switch (type) {
            case CYCLING:
                return cycling;
            case HIKING:
                return hiking;
            case OSM2WORLD:
                return osm2world;
            case OSM_MAPNIK:
                return mapnik;
            case SKIING:
                return skiing;
            default:
                return new String[0];
        }
    }
    
    
    public static void main(String[] args) {
        Tile tile1 = new Tile(2, 0, 0);
        Tile tile2 = new Tile(2, 0, 1);
        Tile tile3 = new Tile(2, 0, 2);
        Tile tile4 = new Tile(2, 0, 3);
        Tile tile5 = new Tile(2, 1, 0);
        Tile tile6 = new Tile(2, 1, 1);
        Tile tile7 = new Tile(2, 1, 2);
        Tile tile8 = new Tile(2, 1, 3);
        Tile tile9 = new Tile(2, 2, 0);
        Tile tile10 = new Tile(2, 2, 1);
        Tile tile11 = new Tile(2, 2, 2);
        Tile tile12 = new Tile(2, 2, 3);
        Tile tile13 = new Tile(2, 3, 0);
        Tile tile14 = new Tile(2, 1, 1);
        Tile tile15 = new Tile(2, 3, 2);
        Tile tile16 = new Tile(2, 3, 3);



        
        OSMTile osm1 = new OSMTile(tile1, TiledMapType.OSM_MAPNIK);
        OSMTile osm2 = new OSMTile(tile2, TiledMapType.OSM_MAPNIK);
        OSMTile osm3 = new OSMTile(tile3, TiledMapType.OSM_MAPNIK);
        OSMTile osm4 = new OSMTile(tile4, TiledMapType.OSM_MAPNIK);
        OSMTile osm5 = new OSMTile(tile5, TiledMapType.OSM_MAPNIK);
        OSMTile osm6 = new OSMTile(tile6, TiledMapType.OSM_MAPNIK);
        OSMTile osm7 = new OSMTile(tile7, TiledMapType.OSM_MAPNIK);
        OSMTile osm8 = new OSMTile(tile8, TiledMapType.OSM_MAPNIK);
        OSMTile osm9 = new OSMTile(tile9, TiledMapType.OSM_MAPNIK);
        OSMTile osm10 = new OSMTile(tile10, TiledMapType.OSM_MAPNIK);
        OSMTile osm11 = new OSMTile(tile11, TiledMapType.OSM_MAPNIK);
        OSMTile osm12 = new OSMTile(tile12, TiledMapType.OSM_MAPNIK);
        OSMTile osm13 = new OSMTile(tile13, TiledMapType.OSM_MAPNIK);
        OSMTile osm14 = new OSMTile(tile14, TiledMapType.OSM_MAPNIK);
        OSMTile osm15 = new OSMTile(tile15, TiledMapType.OSM_MAPNIK);
        OSMTile osm16 = new OSMTile(tile16, TiledMapType.OSM_MAPNIK);


        
        OSMTileSource source = new OSMTileSource();
        
        source.requestObject(osm1, new TestRequester());
        source.requestObject(osm2, new TestRequester());
        source.requestObject(osm3, new TestRequester());
        source.requestObject(osm4, new TestRequester());
        source.requestObject(osm5, new TestRequester());
        source.requestObject(osm6, new TestRequester());
        source.requestObject(osm7, new TestRequester());
        source.requestObject(osm8, new TestRequester());
        source.requestObject(osm9, new TestRequester());
        source.requestObject(osm10, new TestRequester());
        source.requestObject(osm11, new TestRequester());
        source.requestObject(osm12, new TestRequester());
        source.requestObject(osm13, new TestRequester());
        source.requestObject(osm14, new TestRequester());
        source.requestObject(osm15, new TestRequester());
        source.requestObject(osm16, new TestRequester());
            
        }
        
    
    static class TestRequester implements SourceListener<OSMTile, byte[]>{
        
        public TestRequester() {
            
        }

        @Override
        public void requestCompleted(OSMTile key, byte[] value) {
            // TODO Auto-generated method stub
            
        } 
    }
}
