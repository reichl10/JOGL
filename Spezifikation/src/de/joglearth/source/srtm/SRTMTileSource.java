package de.joglearth.source.srtm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;


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

        System.err.println("SRTMTileSource: loading " + key);
        
    	SourceResponse<byte[]> response = binarySource.requestObject(key, 
			new SourceListener<SRTMTileIndex, byte[]>() {

				@Override
				public void requestCompleted(SRTMTileIndex key, byte[] zipBytes) {
	                byte[] tileBytes = null;
	                if (zipBytes != null) {
	                    try {
	                        ZipInputStream zip 
	                            = new ZipInputStream(new ByteArrayInputStream(zipBytes));
	                        ZipEntry entry = zip.getNextEntry();
	                        if (entry.getName().equals(key.toString() + ".hgt")) {
	                            ByteArrayOutputStream tileStream = new ByteArrayOutputStream();
	                            int n = -1;
	                            byte[] buf = new byte[4096];
	                            while ((n = zip.read(buf)) != -1) {
	                                tileStream.write(buf, 0, n);
	                            }
	                            tileBytes = tileStream.toByteArray();
	                            if (tileBytes.length != 1201*1201*2) {
	                                tileBytes = null;
	                            }
	                        }
	                    } catch (IOException e) {
	                        tileBytes = null;
	                    }
	                }

	                if (zipBytes != null && tileBytes == null) {
	                    System.err.println("Loading SRTM data from archive failed for "
	                            + key.toString());
	                }
	                
					SRTMTile tile = null;
					if (tileBytes != null) {
						tile = new SRTMTile(tileBytes);
					}
                    System.err.println("SRTMTileSource: done loading " + key);
					sender.requestCompleted(key, tile);
				}
		});
    	
    	return new SourceResponse<SRTMTile>(response.response, null);
    	
    }
}
