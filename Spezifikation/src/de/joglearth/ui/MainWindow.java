package de.joglearth.ui;

import javax.imageio.ImageIO;
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
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import java.awt.Panel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JRadioButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



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

    private static ImageIcon loadIconResource(String name) {
        try {
            return new ImageIcon(ImageIO.read(Thread.currentThread().getContextClassLoader()
                    .getResource(name)));
        } catch (IOException e) {
            return null;
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
    private JTextField latitudeTextField;
    private JTextField longitudeTextField;
    private JTextField textField;


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
                ColumnSpec.decode("130dlu"),
                ColumnSpec.decode("2dlu"),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("2dlu"),
                ColumnSpec.decode("default:grow"),},
            new RowSpec[] {
                RowSpec.decode("default:grow"),}));
        
        JPanel panel_2 = new JPanel();
        getContentPane().add(panel_2, "1, 1, left, fill");
        panel_2.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"),},
            new RowSpec[] {
                RowSpec.decode("top:45dlu"),
                RowSpec.decode("top:default:grow"),}));
        
        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
        lblNewLabel.setIcon(loadIconResource("icons/logo.png"));
        panel_2.add(lblNewLabel, "1, 1, center, bottom");
        
        JTabbedPane sideBar = new JTabbedPane(JTabbedPane.TOP);
        panel_2.add(sideBar, "1, 2, left, default");
        sideBar.setBackground(UIManager.getColor("menu"));
        
        JPanel viewTab = new JPanel();
        sideBar.addTab("View", loadIconResource("icons/view.png"), viewTab, null);
        sideBar.setEnabledAt(0, true);
        viewTab.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormFactory.RELATED_GAP_COLSPEC,},
            new RowSpec[] {
                RowSpec.decode("6dlu"),
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.NARROW_LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                RowSpec.decode("8dlu"),
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.NARROW_LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                RowSpec.decode("10dlu"),
                FormFactory.DEFAULT_ROWSPEC,}));
        
        JLabel displayModeLabel = new JLabel("Display mode:");
        viewTab.add(displayModeLabel, "2, 2");
        
        JComboBox displayModeComboBox = new JComboBox();
        viewTab.add(displayModeComboBox, "2, 4, fill, default");
        
        JLabel mapTypeLabel = new JLabel("Map type:");
        viewTab.add(mapTypeLabel, "2, 6");
        
        JComboBox mapTypeComboBox = new JComboBox();
        viewTab.add(mapTypeComboBox, "2, 8, fill, default");
        
        JCheckBox heightMapCheckBox = new JCheckBox("Enable height map");
        viewTab.add(heightMapCheckBox, "2, 10");
        
        JPanel placesTab = new JPanel();
        sideBar.addTab("Places", loadIconResource("icons/places.png"), placesTab, null);
        sideBar.setEnabledAt(1, true);
        placesTab.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("2dlu"),
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("2dlu"),},
            new RowSpec[] {
                RowSpec.decode("5dlu"),
                RowSpec.decode("100dlu"),
                RowSpec.decode("10px"),
                RowSpec.decode("100dlu:grow"),}));
        
        JPanel searchPanel = new JPanel();
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        placesTab.add(searchPanel, "2, 2, fill, fill");
        searchPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("2dlu"),
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("2dlu"),},
            new RowSpec[] {
                FormFactory.NARROW_LINE_GAP_ROWSPEC,
                RowSpec.decode("15dlu"),
                RowSpec.decode("15dlu"),
                FormFactory.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"),
                FormFactory.LINE_GAP_ROWSPEC,}));
        
        JPanel searchQueryPanel = new JPanel();
        searchPanel.add(searchQueryPanel, "2, 2, fill, top");
        searchQueryPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("2dlu"),
                ColumnSpec.decode("20dlu"),},
            new RowSpec[] {
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,}));
        
        textField = new JTextField();
        searchQueryPanel.add(textField, "1, 1, fill, default");
        
        JButton btnNewButton = new JButton(loadIconResource("icons/search.png"));
        searchQueryPanel.add(btnNewButton, "3, 1, fill, fill");
        
        JPanel panel = new JPanel();
        searchPanel.add(panel, "2, 3, fill, fill");
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
        
        JRadioButton rdbtnNewRadioButton = new JRadioButton("Nearby");
        panel.add(rdbtnNewRadioButton);
        
        JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("Global");
        panel.add(rdbtnNewRadioButton_1);
        
        JScrollPane scrollPane = new JScrollPane();
        searchPanel.add(scrollPane, "2, 5, fill, fill");
        
        JList list = new JList();
        scrollPane.setRowHeaderView(list);
        
        JPanel panel_1 = new JPanel();
        panel_1.setBorder(BorderFactory.createTitledBorder("User Tags"));
        placesTab.add(panel_1, "2, 4, fill, fill");
        
        JLabel label = new JLabel("New label");
        panel_1.add(label);
        
        JPanel settingsTab = new JPanel();
        sideBar.addTab("Settings", loadIconResource("icons/settings.png"), settingsTab, null);
        sideBar.setEnabledAt(2, true);
        settingsTab.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormFactory.RELATED_GAP_COLSPEC,},
            new RowSpec[] {
                FormFactory.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"),}));
        
        JPanel panel_3 = new JPanel();
        settingsTab.add(panel_3, "2, 2, fill, fill");
        panel_3.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("5dlu"),
                ColumnSpec.decode("default:grow"),},
            new RowSpec[] {
                RowSpec.decode("22dlu"),}));
        
        JButton btnNewButton_1 = new JButton("Manual");
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            }
        });
        btnNewButton_1.setIcon(loadIconResource("icons/manual.png"));
        panel_3.add(btnNewButton_1, "1, 1");
        
        JButton btnNewButton_2 = new JButton("About");
        btnNewButton_2.setIcon(loadIconResource("icons/info.png"));
        panel_3.add(btnNewButton_2, "3, 1");
        
        JPanel sideBarHidePanel = new JPanel();
        sideBarHidePanel.setBackground(Color.LIGHT_GRAY);
        FlowLayout fl_sideBarHidePanel = (FlowLayout) sideBarHidePanel.getLayout();
        fl_sideBarHidePanel.setVgap(0);
        fl_sideBarHidePanel.setHgap(0);
        getContentPane().add(sideBarHidePanel, "3, 1, fill, fill");
        
        JPanel viewPanel = new JPanel();
        getContentPane().add(viewPanel, "5, 1, fill, fill");
        viewPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"),},
            new RowSpec[] {
                RowSpec.decode("default:grow"),
                RowSpec.decode("1dlu"),
                RowSpec.decode("20dlu"),
                RowSpec.decode("1dlu"),}));
        
        GLCanvas glCanvas = new GLCanvas();
        glCanvas.addGLEventListener(new GLEventListener() {

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
        viewPanel.add(glCanvas, "1, 1, fill, fill");
        
        JPanel statusBar = new JPanel();
        viewPanel.add(statusBar, "1, 3, fill, fill");
        statusBar.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("50dlu:grow"),
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("120dlu:grow"),
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("50dlu:grow"),
                FormFactory.RELATED_GAP_COLSPEC,},
            new RowSpec[] {
                RowSpec.decode("default:grow"),}));
        
        JPanel scalePanel = new JPanel();
        statusBar.add(scalePanel, "2, 1, fill, fill");
        scalePanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("center:default:grow"),},
            new RowSpec[] {
                RowSpec.decode("default:grow"),
                RowSpec.decode("default:grow"),}));
        
        JLabel scaleIcon = new JLabel("");
        scaleIcon.setIcon(loadIconResource("icons/scale.png"));
        scalePanel.add(scaleIcon, "1, 1");
        
        JLabel scaleLabel = new JLabel("1 km");
        scalePanel.add(scaleLabel, "1, 2");
        
        JPanel coordPanel = new JPanel();
        statusBar.add(coordPanel, "4, 1, fill, fill");
        coordPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("2dlu"),
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("5dlu"),
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("2dlu"),
                ColumnSpec.decode("default:grow"),},
            new RowSpec[] {
                RowSpec.decode("default:grow"),}));
        
        JLabel latitudeLabel = new JLabel("Latitude:");
        coordPanel.add(latitudeLabel, "1, 1, right, default");
        
        latitudeTextField = new JTextField();
        coordPanel.add(latitudeTextField, "3, 1, fill, default");
        latitudeTextField.setColumns(10);
        
        JLabel longitudeLabel = new JLabel("Longitude:");
        coordPanel.add(longitudeLabel, "5, 1, right, default");
        
        longitudeTextField = new JTextField();
        coordPanel.add(longitudeTextField, "7, 1, fill, default");
        longitudeTextField.setColumns(10);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setValue(100);
        statusBar.add(progressBar, "6, 1");
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
