package de.joglearth.ui;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.CameraListener;
import de.joglearth.geometry.Tile;
import de.joglearth.settings.SettingsListener;
import de.joglearth.surface.Location;
import de.joglearth.surface.LocationListener;
import de.joglearth.surface.LocationManager;
import de.joglearth.surface.SurfaceListener;


/**
 * This class is used to create the main ui window for joglearth.
 */
public class MainWindow extends JFrame implements SurfaceListener,
        LocationListener, CameraListener, SettingsListener {

    /**
     * makes the compiler happy.
     */
    private static final long serialVersionUID = -7540009258222187987L;

    /**
     * Default minimum width of the window.
     */
    private static final int  MIN_WIDTH        = 800;
    /**
     * Default minimum height of the window.
     */
    private static final int  MIN_HEIGTH       = 600;
    /**
     * Stores the reference to the <code>LocationManager</code> that it gets
     * through the Constructor.
     */
    private LocationManager   locationManager;
    /**
     * Stores the reference to the <code>ViewEventListener</code> that is
     * created on initialization.
     */
    private ViewEventListener viewEventListener;
    /**
     * Stores the reference to the <code>GUIEventListener</code> that is created
     * on initialization.
     */
    private GUIEventListener  guiEventListener;
    /**
     * Stores the reference to the <code>Camera</code> that it gets through the
     * constructor.
     */
    private Camera            camera;


    /**
     * Constructor to create he window out of a given {@link LocationManager}
     * and {@link Camera}.
     * 
     * @param locationManager the <code>LocationManager</code> used by this
     *        window
     * @param camera the <code>Camera</code> used by this window
     */
    public MainWindow(final LocationManager locationManager, final Camera camera) {
        this.locationManager = locationManager;
        this.camera = camera;
        this.viewEventListener = new ViewEventListener(camera);
        this.guiEventListener = new GUIEventListener(camera);
    }

    /**
     * Gets the <code>GLCanvas</code> that is displayed in the left half of the
     * window.
     * 
     * @return the GLCanvas used in this window
     */
    public final GLCanvas getGLCanvas() {
        return null;
    }

    @Override
    public void settingsChanged(final String key, final Object valOld, final Object valNew) {}

    @Override
    public void surfaceChanged(final Tile tile) {}

    @Override
    public void cameraViewChanged() {}

    @Override
    public void searchResultsAvailable(final Location[] results) {}
}
