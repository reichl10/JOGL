package de.joglearth.height.srtm;

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
 * necessary if the HeightProfile is activated.
 */
public class SRTMTileSource implements Source<SRTMTileName, SRTMTile> {

    private Source<SRTMTileName, byte[]> binarySource;


    /**
     * Constructor. Initializes the {@link de.joglearth.height.srtm.SRTMTileSource} as it assigns a
     * value to the {@link de.joglearth.source.Source} of <code>SRTMTileSource</code>.
     * 
     * @param binarySource The <code>Source</code> thats assigned
     */
    public SRTMTileSource(Source<SRTMTileName, byte[]> binarySource) {
        this.binarySource = binarySource;
    }

    
    private byte[] unzipFile(byte[] zipBytes, String fileName) throws IOException {
        ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(zipBytes));
        try {
            ZipEntry entry = zip.getNextEntry();
            if (entry.getName().equals(fileName)) {
                ByteArrayOutputStream tileStream = new ByteArrayOutputStream();
                try {
                    int n = -1;
                    byte[] buf = new byte[4096];
                    while ((n = zip.read(buf)) != -1) {
                        tileStream.write(buf, 0, n);
                    }
                    return tileStream.toByteArray();                    
                } finally {
                    tileStream.close();
                }
            } else { 
                return null;
            }
        } finally {
            zip.close();
        }            
    }
    
    
    @Override
    public SourceResponse<SRTMTile> requestObject(SRTMTileName key,
            final SourceListener<SRTMTileName, SRTMTile> sender) {
        
    	SourceResponse<byte[]> response = binarySource.requestObject(key, 
			new SourceListener<SRTMTileName, byte[]>() {

				@Override
				public void requestCompleted(SRTMTileName key, byte[] zipBytes) {
	                byte[] tileBytes = null;
	                if (zipBytes != null) {
	                    try {
	                        tileBytes = unzipFile(zipBytes, key.toString() + ".hgt");
	                        if (tileBytes.length != 1201*1201*2) {
	                            tileBytes = null;
	                        }
	                    } catch (IOException e) {
	                        tileBytes = null;
	                    }
	                }
	                
					SRTMTile tile = null;
					if (tileBytes != null) {
						tile = new SRTMTile(tileBytes);
					}
					sender.requestCompleted(key, tile);
				}
		});
    	
    	return new SourceResponse<SRTMTile>(response.response, null);
    	
    }

    @Override
    public void dispose() {
        binarySource.dispose();
    }
}
