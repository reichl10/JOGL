package de.joglearth.source.srtm;

import static java.lang.Math.*;

/**
 * Provides access to two-dimensional SRTM height data.
 */
public final class SRTMTile {
    
    /**
     * Maximum level of detail that is supported. i.e. level 10 is the smallest possible level
     * for the details.
     */
    public static final int MAX_LOD = 10;
    
    /**
     * Maximum size of the data in one direction. Defined by the size of the input data.
     */
    public static final short LOD0_WIDTH = 1201;
    
    // If there is no height data available for a point it is set to Short.MinValue.
    private static final short INVALID_HEIGHT = Short.MIN_VALUE;
    
    /* All non interpolated and interpolated versions of the tile.
     * Dimension one is the LOD, from largest (0) to smallest (MAX_LOD)
     * Dimension two and three are x and y coordinate.
     */
    private short[][][] height;


    /**
     * Constructor. Decodes the height information from binary SRTM data.
     * 
     * @param raw The binary data
     */
    public SRTMTile(byte[] raw) {
        if (!isValidTileData(raw)) {
            throw new IllegalArgumentException();
        }

        height = new short[MAX_LOD+1][][];
        
        //Construct lowest level of detail from raw data
        height[0] = new short[LOD0_WIDTH][LOD0_WIDTH];
        int byteIndex = 0;
        for (int i = 0; i < LOD0_WIDTH; ++i) {
            for (int j = 0; j < LOD0_WIDTH; ++j) {
                
                //Input data is given in BigEndian byte order.
                height[0][i][j] = (short) (raw[byteIndex] << 8 | raw[byteIndex + 1] & 0xff);
                byteIndex += 2;
            }
        }

        //Fill missing spots in the data with interpolated data from the surrounding points.
        boolean modified = false;        
        do {
            for (int i = 0; i < LOD0_WIDTH; ++i) {
                for (int j = 0; j < LOD0_WIDTH; ++j) {
                    if (height[0][i][j] == INVALID_HEIGHT) {
                        
                        //Some neighbors may also contain invalid values; they are being skipped.
                       int validNeightbours = 0, neighboursSum = 0;

                        // The points above, under, left and right of the point are treated as
                        // neighbors
                        int[][] offsets = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
                       for (int[] offset : offsets) {
                           int iOff = i + offset[0], jOff = j + offset[1];
                           if (iOff >= 0 && iOff < LOD0_WIDTH && jOff >= 0 && jOff < LOD0_WIDTH) {
                               ++validNeightbours;
                               neighboursSum += height[0][iOff][jOff];
                           }
                       }
                       
                       //Points that don't have valid neighbors are corrected in one of the
                       //following steps.
                       if (validNeightbours != 0) {
                           height[0][i][j] = (short) (neighboursSum / validNeightbours);
                           modified = true;
                       }
                    }
                }
            }
            
        //Check again if corrections have been made
        } while (modified);

        //Interpolate lower LODs
        int width = LOD0_WIDTH;
        //TODO last row/column is ignored!
        for (int lod = 1; lod <= MAX_LOD; ++lod) {
            width /= 2;
            short[][] large = height[lod - 1];
            short[][] small = new short[width][width];
            for (int i = 0; i < width; ++i) {
                for (int j = 0; j < width; ++j) {
                    
                    //The next LOD is half the width and half the height, hence one point is
                    //interpolated from exactly 4.
                    small[i][j] = (short) ((large[2 * i][2 * j] + large[2 * i][(2 * j) + 1]
                            + large[(2 * i) + 1][2 * j] + large[(2 * i) + 1][(2 * j) + 1]) / 4);
                }
            }
            height[lod] = small;
        }
    }

    /**
     * Checks whether a byte array is a valid input for the {@link SRTMTile} constructor.
     * @param raw The array to check
     * @return true if the data is valid, otherwise false
     */
    public static boolean isValidTileData(byte[] raw) {
        if (raw == null || raw.length != 2 * LOD0_WIDTH * LOD0_WIDTH) {
            return false;
        }

        boolean valid = false;
        for (int i = 0; i < 2 * LOD0_WIDTH * LOD0_WIDTH; i += 2) {
            
            //Check if a cell is equal to INVALID_HEIGHT without converting it.
            if (raw[i] != (byte) 0x80 || raw[i + 1] != 0) {
                valid = true;
                break;
            }
        }
        return valid;
    }

    /**
     * Returns a two-dimensional array containing the height data interpolated to 1/(4^lod) the
     * size.
     * 
     * @param lod The <code>LevelOfDetail</code> of the data. Must be inside [0, MAX_LOD].
     * @return Two-dimensional array containing the height data
     */
    public short[][] getTile(int lod) {
        if (lod < 0 || lod > MAX_LOD) {
            throw new IllegalArgumentException();
        }
        lod = min(lod, 10);
        return height[lod];
    }

}
