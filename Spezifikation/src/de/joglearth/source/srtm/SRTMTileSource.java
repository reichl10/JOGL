package de.joglearth.source.srtm;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.util.HTTP;


/**
 * Loads SRTM data from NASA servers. The level of detail of a pixel in the SRTM tiles is 90 x 90
 * meters. The SRTM tiles include all information of a required point about the height. As 'standard
 * elevation zero' the WGS84 spheroid is used. Necessary when the HeightProfile is activated. Only
 * necessary if the HightProfile is activated.
 */
public class SRTMTileSource implements Source<SRTMTileIndex, SRTMTile> {

    private Source<SRTMTileIndex, byte[]> binary;

    /**
     * Constructor. Initializes the {@link SRTMTileSource} as it assigns a value to the
     * {@link Source} of <code>SRTMTileSource</code>
     * @param binarySource The <code>Source</code> thats assigned
     */
    public SRTMTileSource(Source<SRTMTileIndex, byte[]> binarySource) {
        binary = binarySource;
    }

    @Override
    public SourceResponse<SRTMTile> requestObject(SRTMTileIndex k,
            SourceListener<SRTMTileIndex, SRTMTile> sender) {
        return null;
    }
}
