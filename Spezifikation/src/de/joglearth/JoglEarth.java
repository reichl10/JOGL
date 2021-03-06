package de.joglearth;

import java.awt.Toolkit;
import java.lang.reflect.Field;

import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.joglearth.height.srtm.SRTMTileManager;
import de.joglearth.location.LocationManager;
import de.joglearth.location.nominatim.NominatimManager;
import de.joglearth.location.overpass.OverpassManager;
import de.joglearth.map.osm.OSMTileManager;
import de.joglearth.settings.SettingsContract;
import de.joglearth.ui.MainWindow;
import de.joglearth.ui.Messages;


/**
 * Static main class of the application. Provides application-specific constants and the main
 * method.
 */
public final class JoglEarth {

    /**
     * The product title. Might be altered to mark milestones.
     */
    public static String PRODUCT_NAME = "Jogl Earth";

    /**
     * The current product version. Altered between releases.
     */
    public static String PRODUCT_VERSION = "0.3";

    /**
     *  Called if the program has been closed to terminate all pending processes.
     */
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
			    // Set the system-specific look and feel
	            try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (
                    ClassNotFoundException | InstantiationException | IllegalAccessException
                    | UnsupportedLookAndFeelException e) {
                }
	            
	            // Set the window manager application name
	            try {
	                Toolkit tk = Toolkit.getDefaultToolkit();
	                Field awtAppName = tk.getClass().getDeclaredField("awtAppClassName");
	                awtAppName.setAccessible(true);
	                awtAppName.set(tk, PRODUCT_NAME);
	            } catch (NoSuchFieldException | IllegalAccessException e) {	                
	            }
	            
	            // Check whether OpenGL2 ES1 is available
	            GLProfile prof = null;
	            try {
	                prof = GLProfile.get(GLProfile.GL2ES1);
	            } catch (GLException e) {
	                JOptionPane.showMessageDialog(null, Messages.getString("JoglEarth.noGL"), 
	                        PRODUCT_NAME, JOptionPane.ERROR_MESSAGE);
	                return;
	            }

	            LocationManager locationManager = new LocationManager();
		        MainWindow gui = new MainWindow(prof, locationManager);
		        gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		        gui.setVisible(true);
			}
		});
    }
}
