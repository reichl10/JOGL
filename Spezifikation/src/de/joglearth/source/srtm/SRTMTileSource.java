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

    private Source<SRTMTileIndex, byte[]> binarySource;


    /**
     * Constructor. Initializes the {@link de.joglearth.source.srtm.SRTMTileSource} as it assigns a
     * value to the {@link de.joglearth.source.Source} of <code>SRTMTileSource</code>
     * 
     * @param binarySource The <code>Source</code> thats assigned
     */
    public SRTMTileSource(Source<SRTMTileIndex, byte[]> binarySource) {
        this.binarySource = binarySource;
    }

    @Override
    public SourceResponse<SRTMTile> requestObject(SRTMTileIndex key,
            final SourceListener<SRTMTileIndex, SRTMTile> sender) {

    	SourceResponse<byte[]> response = binarySource.requestObject(key, 
			new SourceListener<SRTMTileIndex, byte[]>() {

				@Override
				public void requestCompleted(SRTMTileIndex key, byte[] value) {
					SRTMTile tile = null;
					if (value != null) {
						tile = new SRTMTile(value);
					}
					sender.requestCompleted(key, tile);
				}
		
		});
    	
    	return new SourceResponse<SRTMTile>(response.response, null);
    	
    }
}
