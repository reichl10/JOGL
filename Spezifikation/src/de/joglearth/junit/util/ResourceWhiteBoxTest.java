package de.joglearth.junit.util;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import de.joglearth.util.Resource;


public class ResourceWhiteBoxTest {

    @Test
    public void testLoadCSV() {
        Map<String, String> map = Resource.loadCSVMap("srtm_map.csv", "\\s");
        assertNotNull(map);
        assertEquals(map.size(), 14546);
        
        final String[] regions =  { "Eurasia", "Africa", "North_America", "South_America", 
                                    "Australia", "Islands" };
        
        for (Map.Entry<String, String> entry : map.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
            
            boolean validRegion = false;
            for (String r : regions) {
                if (entry.getValue().equals(r)) {
                    validRegion = true;
                }
            }
            assertTrue(validRegion);
            assertTrue(entry.getKey().matches("[NS]\\d{2}[WE]\\d{3}"));            
        }
    }
}
