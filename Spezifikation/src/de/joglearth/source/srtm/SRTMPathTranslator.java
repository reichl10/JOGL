package de.joglearth.source.srtm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.joglearth.source.caching.PathTranslator;


/**
 * Implements the {@link de.joglearth.source.caching.PathTranslator} interface for SRTM height data tiles.
 */
public class SRTMPathTranslator implements PathTranslator<SRTMTileIndex> {
    
    private static final int SMALLEST_NUMBER_WITH_THREE_DIGITS = 100;
    private static final int SMALLEST_NUMBER_WITH_TWO_DIGITS = 10;

    @Override
    public String toFileSystemPath(SRTMTileIndex k) {
        
        String lon = parseLon(k.longitude);
        String lat = parseLat(k.latitude);
        
        String path = "srtm/";
        
        String filename = lat+lon+".hgt.zip";
        
        return path+filename;
    }

    @Override
    public SRTMTileIndex fromFileSystemPath(String s) {
        //TODO REGEXP so richtig?!
        Pattern p = Pattern.compile("(srtm/)([NS][0-9]{2})([EW][0-9]{3}).hgt.zip");
        Matcher m = p.matcher(s);
        
        int longitude = 0;
        int latitude = 0;
        
        if(m.matches()) {
            //Lat
            String lat = m.group(2);
            if(lat.charAt(0) == 'S') {
                latitude = (-1) * Integer.parseInt(s.substring(1));
            } else {
                latitude = Integer.parseInt(s.substring(1));
            }
            
            //Lon
            String lon = m.group(3);
            if(lon.charAt(0) == 'W') {
                longitude = (-1) * Integer.parseInt(s.substring(1));
            } else {
                longitude = Integer.parseInt(s.substring(1));
            }
            
            return new SRTMTileIndex(longitude, latitude);
        }
        
        return null;
    }
    
    private String parseLon(int lon) {
        StringBuilder builder = new StringBuilder();
        if (Math.abs(lon) < SMALLEST_NUMBER_WITH_TWO_DIGITS) {
            if (lon < 0) {
                builder.append("W00");
            } else {
                builder.append("E00");
            }

        } else if (lon < SMALLEST_NUMBER_WITH_THREE_DIGITS) {
            if (lon < 0) {
                builder.append("W0");
            } else {
                builder.append("E0");
            }

        } else {
            if (lon < 0) {
                builder.append("W");
            } else {
                builder.append("E");
            }
        }

        builder.append(Math.abs(lon));
        
        return builder.toString();
    }

    private String parseLat(int lat) {
        StringBuilder builder = new StringBuilder();
        
        if (Math.abs(lat) < SMALLEST_NUMBER_WITH_TWO_DIGITS) {
            if (lat < 0) {
                builder.append("S0");
            } else {
                builder.append("N0");
            }

        } else {
            if (lat < 0) {
                builder.append("S");
            } else {
                builder.append("N");
            }
        }

        builder.append(Math.abs(lat));
        
        return builder.toString();
    }
    
    public static void main(String[] args) {
        SRTMPathTranslator trans = new SRTMPathTranslator();
        SRTMTileIndex tile = new SRTMTileIndex(22, 45);
        System.out.println(trans.toFileSystemPath(tile));
//        System.out.println(trans.fromFileSystemPath("srtm/N22W044.hgt.zip"));
    }

}
