package de.joglearth.location.nominatim;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.location.Location;
import de.joglearth.location.nominatim.NominatimQuery.Type;
import de.joglearth.settings.SettingsContract;
import de.joglearth.source.SourceListener;


public class NominatimManagerTest {

    NominatimManager manager;
    
    @BeforeClass
    public static final void setUpClass() {
        SettingsContract.setDefaultSettings();
    }
    
    @Before
    public final void setUp() {
        manager = NominatimManager.getInstance();
    }
    
    @Test
    public final void testGetInstance() {
        NominatimManager manager2 = NominatimManager.getInstance();
        assertNotNull(manager2);
        assertEquals(manager, manager2);
    }

    @Test
    public final void test() {
        NominatimQuery query = new NominatimQuery(Type.POINT);
        query.point = new GeoCoordinates(0.1d, 0.2d);
        manager.requestObject(query, new SourceListener<NominatimQuery, Collection<Location>>() {
            
            @Override
            public void requestCompleted(NominatimQuery key, Collection<Location> value) {
                synchronized (NominatimManagerTest.this) {
                    NominatimManagerTest.this.notify();
                }
            }
        });
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                fail("InterruptException");
            }
        }
        manager.shutDown();
    }

}
