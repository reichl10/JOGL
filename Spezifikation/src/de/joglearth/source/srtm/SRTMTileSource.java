package de.joglearth.source.srtm;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.util.HTTP;



/**
 * Uses the {@link HTTP} to get the SRTM data from NASA server. The level of detail of a pixel
 * in the SRTM tiles is 90 x 90 meters. The SRTM tiles include all information of a required point
 * about the height. As 'standard elevation zero' the WGS84 spheroid is used. Necessary when the HeightProfile is activated. Only
 * necessary if the HightProfile is activated.
 */
public class SRTMTileSource implements Source<SRTMTileIndex, SRTMTile> {

    private Source<SRTMTileIndex, byte[]> binary;


    public SRTMTileSource(Source<SRTMTileIndex, byte[]> binarySource) {
        binary = binarySource;
    }

    @Override
    public SourceResponse<SRTMTile> requestObject(SRTMTileIndex k,
            SourceListener<SRTMTileIndex, SRTMTile> sender) {
        return null;
    }
}
