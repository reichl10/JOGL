package de.joglearth.ui;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import de.joglearth.JoglEarth;
import de.joglearth.geometry.Camera;
import de.joglearth.geometry.CameraListener;
import de.joglearth.geometry.Tile;
import de.joglearth.settings.SettingsListener;
import de.joglearth.surface.Location;
import de.joglearth.surface.LocationListener;
import de.joglearth.surface.LocationManager;
import de.joglearth.surface.SurfaceListener;

import javax.swing.JSplitPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.JButton;

import java.awt.FlowLayout;

import javax.swing.UIManager;

import com.jgoodies.forms.factories.FormFactory;

import javax.media.nativewindow.WindowClosingProtocol.WindowClosingMode;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.Font;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;



/**
 * This class is used to create the main ui window for joglearth.
 */
public class MainWindow extends JFrame implements SurfaceListener,
        LocationListener, CameraListener, SettingsListener {


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            MainWindow dialog = new MainWindow(null, null);
            dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * makes the compiler happy.
     */
    private static final long serialVersionUID = -7540009258222187987L;

    /**
     * Default minimum width of the window.
     */
    private static final int  MIN_WIDTH        = 900;
    /**
     * Default minimum height of the window.
     */
    private static final int  MIN_HEIGHT       = 600;
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
    private JTextField textField;
    private JTextField textField_1;


    /**
     * Constructor to create he window out of a given {@link LocationManager}
     * and {@link Camera}.
     * 
     * @param locationManager the <code>LocationManager</code> used by this
     *        window
     * @param camera the <code>Camera</code> used by this window
     */
    public MainWindow(final LocationManager locationManager, final Camera camera) {
        setBackground(UIManager.getColor("inactiveCaption"));
        getContentPane().setBackground(UIManager.getColor("inactiveCaption"));
        setTitle(JoglEarth.PRODUCT_NAME);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        this.locationManager = locationManager;
        this.camera = camera;
        this.viewEventListener = new ViewEventListener(camera);
        this.guiEventListener = new GUIEventListener(camera);
        getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("left:110dlu"),
                ColumnSpec.decode("2dlu"),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("2dlu"),
                ColumnSpec.decode("default:grow"),},
            new RowSpec[] {
                RowSpec.decode("default:grow"),}));
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(UIManager.getColor("menu"));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        getContentPane().add(tabbedPane, "1, 1, fill, fill");
        
        JPanel panel_1 = new JPanel();
        tabbedPane.addTab("View", null, panel_1, null);
        
        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Places", null, panel_2, null);
        
        JPanel panel_3 = new JPanel();
        tabbedPane.addTab("Settings", null, panel_3, null);
        
        JPanel panel_6 = new JPanel();
        panel_6.setBackground(new Color(211, 211, 211));
        FlowLayout flowLayout = (FlowLayout) panel_6.getLayout();
        flowLayout.setVgap(0);
        flowLayout.setHgap(0);
        getContentPane().add(panel_6, "3, 1, fill, fill");
        
        JPanel panel = new JPanel();
        getContentPane().add(panel, "5, 1, fill, fill");
        panel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"),},
            new RowSpec[] {
                RowSpec.decode("default:grow"),
                RowSpec.decode("1dlu"),
                RowSpec.decode("20dlu"),
                RowSpec.decode("1dlu"),}));
        
        GLCanvas panel_4 = new GLCanvas();
        panel_4.addGLEventListener(new GLEventListener() {

            @Override
            public void display(GLAutoDrawable arg0) {
                arg0.getGL().getGL2().glClear(GL2.GL_COLOR_BUFFER_BIT);
            }

            @Override
            public void dispose(GLAutoDrawable arg0) {
                
            }

            @Override
            public void init(GLAutoDrawable arg0) {                
            }

            @Override
            public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
                ((Component) arg0).setMinimumSize(new Dimension(0, 0));
            }
            
        });
        panel.add(panel_4, "1, 1, fill, fill");
        
        JPanel panel_5 = new JPanel();
        panel.add(panel_5, "1, 3, fill, fill");
        panel_5.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("50dlu:grow"),
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("120dlu:grow"),
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("50dlu:grow"),
                FormFactory.RELATED_GAP_COLSPEC,},
            new RowSpec[] {
                RowSpec.decode("default:grow"),}));
        
        JPanel panel_7 = new JPanel();
        panel_5.add(panel_7, "2, 1, fill, fill");
        panel_7.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("center:default:grow"),},
            new RowSpec[] {
                RowSpec.decode("default:grow"),
                RowSpec.decode("default:grow"),}));
        
        JLabel lblNewLabel_3 = new JLabel("");
        lblNewLabel_3.setIcon(new ImageIcon("/home/trion/Uni/SEP/Git/Spezifikation/res/icons/scale.png"));
        panel_7.add(lblNewLabel_3, "1, 1");
        
        JLabel lblNewLabel = new JLabel("1 km");
        panel_7.add(lblNewLabel, "1, 2");
        
        JPanel panel_8 = new JPanel();
        panel_5.add(panel_8, "4, 1, fill, fill");
        panel_8.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("2dlu"),
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("5dlu"),
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("2dlu"),
                ColumnSpec.decode("default:grow"),},
            new RowSpec[] {
                RowSpec.decode("default:grow"),}));
        
        JLabel lblNewLabel_1 = new JLabel("Latitude:");
        panel_8.add(lblNewLabel_1, "1, 1, right, default");
        
        textField = new JTextField();
        panel_8.add(textField, "3, 1, fill, default");
        textField.setColumns(10);
        
        JLabel lblNewLabel_2 = new JLabel("Longitude:");
        panel_8.add(lblNewLabel_2, "5, 1, right, default");
        
        textField_1 = new JTextField();
        panel_8.add(textField_1, "7, 1, fill, default");
        textField_1.setColumns(10);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setValue(100);
        panel_5.add(progressBar, "6, 1");
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
