package de.joglearth.source.srtm;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.util.HTTP;


/**
 * Loads SRTM data from NASA servers. The size of the {@link SRTMTiles} is 90 x 90 meters. The SRTM
 * tiles include all information of a required point about the height above the sea level. Only
 * necessary if the HightProfile is activated.
 * 
 */
public class SRTMBinarySource implements Source<SRTMTileIndex, byte[]> {

    // Die Server, die der Reihe nach nach Daten gefragt werden sollen.
    // K�nnen entwerder einfache URLs a la "domain.tld/folder/to/data/" sein,
    // oder ein Format-String a la "domain.tld/data/%s/%s", der dann mit einer
    // �berschriebenen getURL-Methode aufgel�st wird.

    // Verwaltung? Sollen u.U. unerreichbare Server markiert werden?
    private final String[] servers = null;


    /**
     * Constructor. Initializes the {@link SRTMTileSource}.
     * 
     * @param servers An array of servers delivered as Strings
     */
    public SRTMBinarySource() {}

    @Override
    public SourceResponse<byte[]> requestObject(SRTMTileIndex key,
            SourceListener<SRTMTileIndex, byte[]> sender) {
        // TODO Automatisch generierter Methodenstub
        return null;
    }
}
