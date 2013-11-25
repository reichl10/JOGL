package de.joglearth.source;

import de.joglearth.geometry.Tile;


/**
 * Uses the {@link HTTPUtils} to get the SRTM data from NASA servers. The size of the SRTM tiles is
 * 90 x 90 meters. The SRTM tiles include all information of a required point about the height above
 * the sea level. Only necessary if the HightProfile is activated.
 * 
 */
public class SRTMTileSource implements Source<Tile, byte[]> {

    // Die Server, die der Reihe nach nach Daten gefragt werden sollen.
    // K�nnen entwerder einfache URLs a la "domain.tld/folder/to/data/" sein,
    // oder ein Format-String a la "domain.tld/data/%s/%s", der dann mit einer
    // �berschriebenen getURL-Methode aufgel�st wird.

    // Verwaltung? Sollen u.U. unerreichbare Server markiert werden?
    private String[] servers;


    /**
     * Constructor. Initializes the {@link SRTMTileSource}.
     * 
     * @param servers An array of servers delivered as Strings
     */
    public SRTMTileSource(String[] servers) {
        this.servers = servers;
    }

    @Override
    public SourceResponse<byte[]> requestObject(Tile k, SourceListener<Tile, byte[]> sender) {
        return null;
    }
}
