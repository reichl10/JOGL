package de.joglearth;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.joglearth.settings.SettingsContract;
import de.joglearth.source.nominatim.NominatimManager;
import de.joglearth.source.osm.OSMTileManager;
import de.joglearth.source.overpass.OverpassManager;
import de.joglearth.source.srtm.SRTMTileManager;
import de.joglearth.surface.LocationManager;
import de.joglearth.ui.MainWindow;


/**
 * Static main class of the application.
 * Provides application-specific constants and the main method.
 */
public final class JoglEarth {

    /**
     * The product title. Might be altered to mark milestones.
     */
    public static String PRODUCT_NAME = "Jogl Earth";

    /**
     * The current product version. Altered between releases.
     */
    public static String PRODUCT_VERSION = "0.1";

    
    public static void shutDown() {
        SRTMTileManager.shutDown();
        OSMTileManager.shutDown();
        OverpassManager.shutDown();
        NominatimManager.shutDown();
        SettingsContract.saveSettings();
    }
    

    /**
     * Initializes the JoglEarth application. There are no valid command line arguments.
     * 
     * @param args Command line arguments (unused)
     */
    public static void main(String[] args) {
        SettingsContract.setDefaultSettings();
        SettingsContract.loadSettings();
        SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
	            try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (
                    ClassNotFoundException | InstantiationException | IllegalAccessException
                    | UnsupportedLookAndFeelException e) {
                    System.err.println("Swing: Unable to set system look and feel");
                }
	            
	            LocationManager locationManager = new LocationManager();
		        MainWindow gui = new MainWindow(locationManager);
		        gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		        gui.setVisible(true);
			}
		});
    }
}
