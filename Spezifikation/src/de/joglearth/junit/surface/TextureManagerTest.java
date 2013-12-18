package de.joglearth.junit.surface;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.joglearth.geometry.Tile;
import de.joglearth.junit.GLTestWindow;
import de.joglearth.surface.SurfaceListener;
import de.joglearth.surface.TextureManager;


public class TextureManagerTest {

    GLTestWindow window;


    @Before
    public final void before() {
        if (window != null)
            window.dispose();
        window = new GLTestWindow();
    }

    @Test
    public final void testTextureManager() {
        TextureManager man = new TextureManager(window.getGL());
    }

    @Test
    public final void testGetTexture() {
        TextureManager man = new TextureManager(window.getGL());
        Integer id = man.getTexture(new Tile(0, 0, 0));
        assertNotNull(id);
    }

    @Test
    public final void testAddSurfaceListener() {
        TextureManager man = new TextureManager(window.getGL());
        man.addSurfaceListener(new TestSurfaceListener());
    }

    @Test
    public final void testRemoveSurfaceListener() {
        TextureManager man = new TextureManager(window.getGL());
        TestSurfaceListener listener = new TestSurfaceListener();
        man.addSurfaceListener(new TestSurfaceListener());
        man.removeSurfaceListener(listener);
    }


    private class TestSurfaceListener implements SurfaceListener {

        @Override
        public void surfaceChanged(double lonFrom, double latFrom, double lonTo, double latTo) {}
    }
}
