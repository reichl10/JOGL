package de.joglearth.ui;

import static de.joglearth.util.Resource.loadIcon;
import static java.lang.Math.abs;
import static java.lang.Math.signum;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComponentInputMap;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ActionMapUIResource;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.joglearth.JoglEarth;
import de.joglearth.geometry.Camera;
import de.joglearth.geometry.CameraListener;
import de.joglearth.geometry.CameraUtils;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.ScreenCoordinates;
import de.joglearth.height.flat.FlatHeightMap;
import de.joglearth.height.srtm.SRTMHeightMap;
import de.joglearth.location.Location;
import de.joglearth.location.LocationListener;
import de.joglearth.location.LocationManager;
import de.joglearth.location.LocationType;
import de.joglearth.map.MapConfiguration;
import de.joglearth.map.osm.OSMMapConfiguration;
import de.joglearth.map.osm.OSMMapType;
import de.joglearth.map.single.SingleMapConfiguration;
import de.joglearth.map.single.SingleMapType;
import de.joglearth.opengl.Antialiasing;
import de.joglearth.opengl.GLContext;
import de.joglearth.opengl.GLEasel;
import de.joglearth.opengl.TextureFilter;
import de.joglearth.rendering.DisplayMode;
import de.joglearth.rendering.LevelOfDetail;
import de.joglearth.rendering.Renderer;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.settings.SettingsListener;
import de.joglearth.source.ProgressListener;
import de.joglearth.source.ProgressManager;
import de.joglearth.util.Resource;


/**
 * The main UI window class.
 */
public class MainWindow extends JFrame {

    private static final String AC_SEARCH = "action_search"; //$NON-NLS-1$
    private static ImageIcon hideIcon = loadIcon("icons/hide.png"); //$NON-NLS-1$
    private static ImageIcon showIcon = loadIcon("icons/show.png"); //$NON-NLS-1$

    private GLEasel easel;

    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = -7540009258222187987L;

    /**
     * Default minimum width of the window.
     */
    private static final int MIN_WIDTH = 900;

    /**
     * Default minimum height of the window.
     */
    private static final int MIN_HEIGHT = 600;

    private static final double SCALE_TILT = 0.3d;

    /**
     * Stores the reference to the <code>LocationManager</code> that it gets through the
     * Constructor.
     */
    private LocationManager locationManager;

    /**
     * Stores the reference to the <code>ViewEventListener</code> that is created on initialization.
     */
    private ViewEventListener viewEventListener;

    /**
     * Stores the reference to the <code>Camera</code> that it gets through the constructor.
     */
    private Camera camera;
    private Renderer renderer;
    private JTextField latitudeTextField;
    private JTextField longitudeTextField;
    private JTextField searchTextField;
    private JLabel sidebarHideIconLabel;
    private JPanel sideBarHideLinePanel;
    private JPanel mapOptionsPanel;
    private JComboBox<IconizedItem<DisplayMode>> displayModeComboBox;
    private JPanel viewTab, placesTab, settingsTab, detailsPanel, viewPanel;
    private JCheckBox heightMapCheckBox;
    private JComboBox<IconizedItem<Locale>> languageComboBox;
    private JComboBox<NamedItem<Antialiasing>> antialiasingComboBox;
    private JComboBox<NamedItem<TextureFilter>> texfilterComboBox;
    private JComboBox<NamedItem<LevelOfDetail>> lodComboBox_1;
    private JPanel graphicsPanel;
    private JLabel antialiasingLabel;
    private JLabel texfilterLabel;
    private JLabel lodLabel;
    private JSpinner memCacheSpinner;
    private JLabel memCacheLabel;
    private JPanel cachePanel;
    private JLabel fsCacheLabel;
    private JButton aboutButton;
    private JButton manualButton;
    private JPanel languagePanel;
    private JPanel searchPanel;
    private JPanel userTagPanel;
    private JPanel overlayPanel;
    private JLabel detailNameLabel;
    private JLabel detailDescriptionLabel;
    private JButton userTagButton;
    private JLabel latitudeLabel;
    private JLabel longitudeLabel;
    private JRadioButton localSearchRadioButton;
    private JRadioButton globalSearchRadioButton;
    private JLabel displayModeLabel;
    private JLabel mapTypeLabel;
    private JComboBox<IconizedItem<MapConfiguration>> paraMapTypeComboBox;
    private JTabbedPane sideBarTabs;
    private JSlider zoomSlider;
    private UISettingsListener settingsListener;
    private JLabel scaleLabel;
    private static final double ZOOM_FACTOR = 10.d;
    private static final double MAX_DIST = 2.d;
    private static final double MIN_DIST = 1e-8d;
    private JButton searchButton;
    private JList<Location> searchResultList;
    private JPanel overlaysPanel;
    private JScrollPane overlayScrollPane;
    private Map<JCheckBox, LocationType> checkboxToLocationTypeMap = new HashMap<JCheckBox, LocationType>();
    private JProgressBar progressBar;
    private ProgressManager progressManager;
    private JPanel userTagListPanel;
    private JScrollPane scrollPane;
    private Map<JButton, Location> buttonToLocationMap = new HashMap<JButton, Location>();
    private double cTiltX = 0.0d;
    private double cTiltY = 0.0d;
    private Canvas scaleCanvas;


    private class HideSideBarListener extends MouseAdapter {

        boolean visible = true;


        @Override
        public void mouseClicked(MouseEvent e) {
            visible = !visible;
            ((FormLayout) getContentPane().getLayout()).setColumnSpec(1,
                    ColumnSpec.decode(visible ? "130dlu" : "0dlu")); //$NON-NLS-1$ //$NON-NLS-2$
            sidebarHideIconLabel.setIcon(visible ? hideIcon : showIcon);
            getContentPane().revalidate();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            sideBarHideLinePanel.setBackground(new Color(0xa8a8a8));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            sideBarHideLinePanel.setBackground(Color.LIGHT_GRAY);

        }
    };


    private void initializeWindow() {
        setBackground(UIManager.getColor("inactiveCaption")); //$NON-NLS-1$
        getContentPane().setBackground(UIManager.getColor("inactiveCaption")); //$NON-NLS-1$
        setTitle(JoglEarth.PRODUCT_NAME);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        this.viewEventListener = new ViewEventListener(camera);
        this.settingsListener = new UISettingsListener();
        Settings.getInstance().addSettingsListener(SettingsContract.ANTIALIASING, settingsListener);
        getContentPane().setLayout(
                new FormLayout(new ColumnSpec[] {
                        ColumnSpec.decode("right:160dlu"), //$NON-NLS-1$
                        ColumnSpec.decode("15px"), //$NON-NLS-1$
                        ColumnSpec.decode("default:grow"), }, //$NON-NLS-1$
                        new RowSpec[] {
                                RowSpec.decode("default:grow"), })); //$NON-NLS-1$

        JPanel sideBar = new JPanel();
        getContentPane().add(sideBar, "1, 1, left, fill"); //$NON-NLS-1$
        sideBar.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec
                .decode("default:grow"), }, //$NON-NLS-1$
                new RowSpec[] {
                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                        RowSpec.decode("top:default:grow"), //$NON-NLS-1$
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("80dlu"), //$NON-NLS-1$
                        FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        sideBarTabs = new JTabbedPane(JTabbedPane.TOP);
        sideBar.add(sideBarTabs, "1, 2, fill, fill"); //$NON-NLS-1$
        sideBarTabs.setBackground(UIManager.getColor("menu")); //$NON-NLS-1$

        viewTab = new JPanel();
        sideBarTabs
                .addTab(Messages.getString("MainWindow.16"), loadIcon("icons/view.png"), viewTab, //$NON-NLS-1$ //$NON-NLS-2$
                        null);
        sideBarTabs.setEnabledAt(0, true);
        placesTab = new JPanel();
        sideBarTabs
                .addTab(Messages.getString("MainWindow.18"), loadIcon("icons/places.png"), //$NON-NLS-1$ //$NON-NLS-2$
                        placesTab, null);
        sideBarTabs.setEnabledAt(1, true);
        settingsTab = new JPanel();
        sideBarTabs
                .addTab(Messages.getString("MainWindow.20"), loadIcon("icons/settings.png"), //$NON-NLS-1$ //$NON-NLS-2$
                        settingsTab, null);
        sideBarTabs.setEnabledAt(2, true);
        detailsPanel = new JPanel();
        sideBar.add(detailsPanel, "1, 4, fill, fill"); //$NON-NLS-1$

        JPanel sideBarHidePanel = new JPanel();
        sideBarHidePanel.addMouseListener(new HideSideBarListener());
        getContentPane().add(sideBarHidePanel, "2, 1, fill, fill"); //$NON-NLS-1$
        sideBarHidePanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("10px"), }, //$NON-NLS-1$
                new RowSpec[] {
                        RowSpec.decode("4dlu:grow"), })); //$NON-NLS-1$

        sideBarHideLinePanel = new JPanel();
        sideBarHideLinePanel.setBackground(Color.LIGHT_GRAY);
        sideBarHidePanel.add(sideBarHideLinePanel, "1, 1, fill, fill"); //$NON-NLS-1$
        sideBarHideLinePanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"), }, //$NON-NLS-1$
                new RowSpec[] {
                        RowSpec.decode("default:grow"), })); //$NON-NLS-1$

        sidebarHideIconLabel = new JLabel(""); //$NON-NLS-1$
        sidebarHideIconLabel.setIcon(hideIcon);
        sideBarHideLinePanel.add(sidebarHideIconLabel, "1, 1"); //$NON-NLS-1$

        viewPanel = new JPanel();
        getContentPane().add(viewPanel, "3, 1, fill, fill"); //$NON-NLS-1$
    }

    private void initializeDetailsPanel() {
        detailsPanel.setBorder(BorderFactory.createTitledBorder(Messages
                .getString("MainWindow.35"))); //$NON-NLS-1$
        detailsPanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"), //$NON-NLS-1$
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC, }, new RowSpec[] {
                FormFactory.NARROW_LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"), //$NON-NLS-1$
                FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        detailNameLabel = new JLabel(Messages.getString("MainWindow.3")); //$NON-NLS-1$
        detailsPanel.add(detailNameLabel, "2, 2"); //$NON-NLS-1$

        userTagButton = new JButton(Messages.getString("MainWindow.40")); //$NON-NLS-1$
        userTagButton.setHorizontalAlignment(SwingConstants.LEFT);
        userTagButton.setIcon(loadIcon("icons/addTag.png")); //$NON-NLS-1$

        detailDescriptionLabel = new JLabel(Messages.getString("MainWindow.42")); //$NON-NLS-1$
        detailsPanel.add(detailDescriptionLabel, "2, 4, default, top"); //$NON-NLS-1$
        detailsPanel.add(userTagButton, "2, 6"); //$NON-NLS-1$
    }

    private void initializeViewTab() {
        viewTab.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"), //$NON-NLS-1$
                FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
                RowSpec.decode("6dlu"), //$NON-NLS-1$
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.NARROW_LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
                RowSpec.decode("bottom:default:grow"), })); //$NON-NLS-1$

        displayModeLabel = new JLabel(Messages.getString("MainWindow.48")); //$NON-NLS-1$
        viewTab.add(displayModeLabel, "2, 2"); //$NON-NLS-1$
        mapOptionsPanel = new JPanel();

        displayModeComboBox = new JComboBox<IconizedItem<DisplayMode>>();
        displayModeComboBox
                .setRenderer(new IconListCellRenderer<IconizedItem<DisplayMode>>());

        displayModeComboBox.addItem(new IconizedItem<DisplayMode>(Messages
                .getString("MainWindow.52"), //$NON-NLS-1$
                loadIcon("icons/modeSolar.png"), DisplayMode.SOLAR_SYSTEM)); //$NON-NLS-1$
        displayModeComboBox.addItem(new IconizedItem<DisplayMode>(Messages
                .getString("MainWindow.54"), //$NON-NLS-1$
                loadIcon("icons/modeGlobe.png"), DisplayMode.GLOBE_MAP)); //$NON-NLS-1$
        displayModeComboBox.addItem(new IconizedItem<DisplayMode>(Messages
                .getString("MainWindow.56"), //$NON-NLS-1$
                loadIcon("icons/modePlane.png"), DisplayMode.PLANE_MAP)); //$NON-NLS-1$
        viewTab.add(displayModeComboBox, "2, 4, fill, default"); //$NON-NLS-1$

        mapOptionsPanel.setBorder(null);
        viewTab.add(mapOptionsPanel, "2, 6, fill, fill"); //$NON-NLS-1$
        mapOptionsPanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec
                .decode("default:grow"), }, //$NON-NLS-1$
                new RowSpec[] { FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC, RowSpec.decode("8dlu"), //$NON-NLS-1$
                        FormFactory.DEFAULT_ROWSPEC, }));

        mapTypeLabel = new JLabel(Messages.getString("MainWindow.62")); //$NON-NLS-1$
        mapOptionsPanel.add(mapTypeLabel, "1, 1"); //$NON-NLS-1$

        paraMapTypeComboBox = new JComboBox<IconizedItem<MapConfiguration>>();
        mapOptionsPanel.add(paraMapTypeComboBox, "1, 3"); //$NON-NLS-1$
        paraMapTypeComboBox
                .setRenderer(new IconListCellRenderer<IconizedItem<MapConfiguration>>());
        heightMapCheckBox = new JCheckBox(Messages.getString("MainWindow.65")); //$NON-NLS-1$
        heightMapCheckBox.addChangeListener(new ChangeListener() {

            private boolean lastSelected = false;


            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox box = (JCheckBox) e.getSource();
                if (box.isSelected() != lastSelected) {
                    if (box.isSelected()) {
                        renderer.setHeightMap(SRTMHeightMap.getInstance());
                    } else {
                        renderer.setHeightMap(FlatHeightMap.getInstance());
                    }
                    lastSelected = box.isSelected();
                }
            }
        });
        mapOptionsPanel.add(heightMapCheckBox, "1, 5"); //$NON-NLS-1$

        JLabel logoLabel = new JLabel(""); //$NON-NLS-1$
        viewTab.add(logoLabel, "2, 8, center, bottom"); //$NON-NLS-1$
        logoLabel.setVerticalAlignment(SwingConstants.TOP);
        logoLabel.setIcon(loadIcon("icons/logo.png")); //$NON-NLS-1$
    }

    private void initializePlacesTab() {
        placesTab.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("2dlu"), //$NON-NLS-1$
                ColumnSpec.decode("default:grow"), //$NON-NLS-1$
                ColumnSpec.decode("2dlu"), }, //$NON-NLS-1$
                new RowSpec[] {
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("max(60dlu;default):grow"), //$NON-NLS-1$
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("default:grow"), //$NON-NLS-1$
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("default:grow"), //$NON-NLS-1$
                        FormFactory.RELATED_GAP_ROWSPEC, }));

        searchPanel = new JPanel();
        searchPanel.setBorder(BorderFactory.createTitledBorder(Messages
                .getString("MainWindow.82"))); //$NON-NLS-1$
        placesTab.add(searchPanel, "2, 2, fill, fill"); //$NON-NLS-1$
        searchPanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"), //$NON-NLS-1$
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC, },
                new RowSpec[] {
                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                        RowSpec.decode("15dlu"), //$NON-NLS-1$
                        RowSpec.decode("15dlu"), //$NON-NLS-1$
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("default:grow"), //$NON-NLS-1$
                        FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        JPanel searchQueryPanel = new JPanel();
        searchPanel.add(searchQueryPanel, "2, 2, fill, top"); //$NON-NLS-1$
        searchQueryPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"), ColumnSpec.decode("2dlu"), //$NON-NLS-1$ //$NON-NLS-2$
                ColumnSpec.decode("20dlu"), }, new RowSpec[] { //$NON-NLS-1$
                FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

        searchTextField = new JTextField();
        searchQueryPanel.add(searchTextField, "1, 1, fill, fill"); //$NON-NLS-1$

        searchButton = new JButton(loadIcon("icons/search.png")); //$NON-NLS-1$
        searchButton.setMnemonic('S');
        searchQueryPanel.add(searchButton, "3, 1, fill, fill"); //$NON-NLS-1$

        JPanel panel = new JPanel();
        searchPanel.add(panel, "2, 3, fill, fill"); //$NON-NLS-1$
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));

        ButtonGroup searchTypeButtonGroup = new ButtonGroup();

        localSearchRadioButton = new JRadioButton(
                Messages.getString("MainWindow.96")); //$NON-NLS-1$
        localSearchRadioButton.setSelected(true);
        panel.add(localSearchRadioButton);

        globalSearchRadioButton = new JRadioButton(
                Messages.getString("MainWindow.97")); //$NON-NLS-1$
        panel.add(globalSearchRadioButton);

        searchTypeButtonGroup.add(localSearchRadioButton);
        searchTypeButtonGroup.add(globalSearchRadioButton);

        JScrollPane searchResultScrollPane = new JScrollPane();
        searchResultScrollPane.setMinimumSize(new Dimension(0, 0));
        searchPanel.add(searchResultScrollPane, "2, 5, fill, fill"); //$NON-NLS-1$

        searchResultList = new JList<Location>(
                new DefaultListModel<Location>());
        searchResultList.setCellRenderer(new LocationListCellRenderer());
        searchResultScrollPane.setViewportView(searchResultList);

        userTagPanel = new JPanel();
        userTagPanel.setBorder(BorderFactory.createTitledBorder(Messages
                .getString("MainWindow.99"))); //$NON-NLS-1$
        placesTab.add(userTagPanel, "2, 4, fill, fill"); //$NON-NLS-1$
        userTagPanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC, },
                new RowSpec[] {
                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                        RowSpec.decode("default:grow"),
                        FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        scrollPane = new JScrollPane();
        userTagPanel.add(scrollPane, "2, 2, fill, fill");

        userTagListPanel = new JPanel();
        scrollPane.setViewportView(userTagListPanel);
        userTagListPanel.setLayout(new GridLayout(10, 0, 0, 0));

        overlayPanel = new JPanel();
        overlayPanel.setBorder(BorderFactory.createTitledBorder(Messages
                .getString("MainWindow.0"))); //$NON-NLS-1$
        placesTab.add(overlayPanel, "2, 6, fill, fill"); //$NON-NLS-1$
        overlayPanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"), //$NON-NLS-1$
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC, },
                new RowSpec[] {
                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                        RowSpec.decode("default:grow"), //$NON-NLS-1$
                        FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        overlayScrollPane = new JScrollPane();
        overlayPanel.add(overlayScrollPane, "2, 2, fill, fill"); //$NON-NLS-1$

        overlaysPanel = new JPanel();
        overlayScrollPane.setViewportView(overlaysPanel);

        UIOverlaySelectionListener overlayListener = new UIOverlaySelectionListener();

        JCheckBox box = new JCheckBox(Messages.getString("MainWindow.restaurant")); //$NON-NLS-1$
        checkboxToLocationTypeMap.put(box, LocationType.RESTAURANT);
        overlaysPanel.add(box);
        box.addItemListener(overlayListener);
        box = new JCheckBox(Messages.getString("MainWindow.nightlife")); //$NON-NLS-1$
        checkboxToLocationTypeMap.put(box, LocationType.NIGHTLIFE);
        overlaysPanel.add(box);
        box.addItemListener(overlayListener);
        box = new JCheckBox(Messages.getString("MainWindow.bank")); //$NON-NLS-1$
        checkboxToLocationTypeMap.put(box, LocationType.BANK);
        overlaysPanel.add(box);
        box.addItemListener(overlayListener);
        box = new JCheckBox(Messages.getString("MainWindow.toilets")); //$NON-NLS-1$
        checkboxToLocationTypeMap.put(box, LocationType.TOILETS);
        overlaysPanel.add(box);
        box.addItemListener(overlayListener);
        box = new JCheckBox(Messages.getString("MainWindow.grocery_store")); //$NON-NLS-1$
        checkboxToLocationTypeMap.put(box, LocationType.GROCERY_SHOPS);
        overlaysPanel.add(box);
        box.addItemListener(overlayListener);
        box = new JCheckBox(Messages.getString("MainWindow.shops")); //$NON-NLS-1$
        checkboxToLocationTypeMap.put(box, LocationType.SHOPS);
        overlaysPanel.add(box);
        box.addItemListener(overlayListener);
        box = new JCheckBox(Messages.getString("MainWindow.activity")); //$NON-NLS-1$
        checkboxToLocationTypeMap.put(box, LocationType.ACTIVITY);
        overlaysPanel.add(box);
        box.addItemListener(overlayListener);
        box = new JCheckBox(Messages.getString("MainWindow.hiking_and_cycling")); //$NON-NLS-1$
        checkboxToLocationTypeMap.put(box, LocationType.HIKING_AND_CYCLING);
        overlaysPanel.add(box);
        box.addItemListener(overlayListener);
        box = new JCheckBox(Messages.getString("MainWindow.education")); //$NON-NLS-1$
        checkboxToLocationTypeMap.put(box, LocationType.EDUCATION);
        overlaysPanel.add(box);
        box.addItemListener(overlayListener);
        box = new JCheckBox(Messages.getString("MainWindow.health")); //$NON-NLS-1$
        checkboxToLocationTypeMap.put(box, LocationType.HEALTH);
        overlaysPanel.add(box);
        box.addItemListener(overlayListener);
        box = new JCheckBox(Messages.getString("MainWindow.post")); //$NON-NLS-1$
        checkboxToLocationTypeMap.put(box, LocationType.POST);
        overlaysPanel.add(box);
        box.addItemListener(overlayListener);
        box = new JCheckBox(Messages.getString("MainWindow.hotels")); //$NON-NLS-1$
        checkboxToLocationTypeMap.put(box, LocationType.HOTELS);
        overlaysPanel.add(box);
        box.addItemListener(overlayListener);
        box = new JCheckBox(Messages.getString("MainWindow.city")); //$NON-NLS-1$
        checkboxToLocationTypeMap.put(box, LocationType.CITY);
        overlaysPanel.add(box);
        box.addItemListener(overlayListener);
//        box = new JCheckBox(Messages.getString("MainWindow.town")); //$NON-NLS-1$
//        checkboxToLocationTypeMap.put(box, LocationType.TOWN);
//        overlaysPanel.add(box);
//        box.addItemListener(overlayListener);
//        box = new JCheckBox(Messages.getString("MainWindow.village")); //$NON-NLS-1$
//        checkboxToLocationTypeMap.put(box, LocationType.VILLAGE);
//        overlaysPanel.add(box);
//        box.addItemListener(overlayListener);
        box = new JCheckBox(Messages.getString("MainWindow.user_tags")); //$NON-NLS-1$
        checkboxToLocationTypeMap.put(box, LocationType.USER_TAG);
        overlaysPanel.add(box);
        box.addItemListener(overlayListener);
        box = new JCheckBox(Messages.getString("MainWindow.search")); //$NON-NLS-1$
        checkboxToLocationTypeMap.put(box, LocationType.SEARCH);
        overlaysPanel.add(box);
        box.addItemListener(overlayListener);
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                buttonToLocationMap.clear();
                userTagListPanel.removeAll();
                Set<Location> uLocations = Settings.getInstance().getLocations(
                        SettingsContract.USER_LOCATIONS);
                for (final Location l : uLocations) {
                    System.out.println("Name: " + l.name);
                    JButton button = new JButton(l.name);
                    button.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            camera.setPosition(l.point);
                        }
                    });
                    buttonToLocationMap.put(button, l);
                    userTagListPanel.add(button);
                }
            }
        });
    }

    private void initializeSettingsTab() {
        settingsTab
                .setLayout(new FormLayout(new ColumnSpec[] {
                        FormFactory.RELATED_GAP_COLSPEC,
                        ColumnSpec.decode("default:grow"), //$NON-NLS-1$
                        FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC, }));

        languagePanel = new JPanel();
        languagePanel.setBorder(new TitledBorder(null, Messages
                .getString("MainWindow.110"), TitledBorder.LEADING, //$NON-NLS-1$
                TitledBorder.TOP, null, null));
        settingsTab.add(languagePanel, "2, 2"); //$NON-NLS-1$
        languagePanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"), //$NON-NLS-1$
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC, }, new RowSpec[] {
                FormFactory.NARROW_LINE_GAP_ROWSPEC,
                RowSpec.decode("default:grow"), //$NON-NLS-1$
                FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        languageComboBox = new JComboBox<IconizedItem<Locale>>();
        languageComboBox
                .setRenderer(new IconListCellRenderer<IconizedItem<Locale>>());
        languageComboBox.addItem(new IconizedItem<Locale>(Messages
                .getString("MainWindow.114"), loadIcon("icons/flagEng.png"), //$NON-NLS-1$ //$NON-NLS-2$
                Locale.ENGLISH)); //$NON-NLS-1$
        languageComboBox.addItem(new IconizedItem<Locale>("Deutsch", loadIcon("icons/flagGer.png"), //$NON-NLS-1$ //$NON-NLS-2$
                Locale.GERMAN)); //$NON-NLS-1$
        languagePanel.add(languageComboBox, "2, 2, fill, top"); //$NON-NLS-1$
        graphicsPanel = new JPanel();
        graphicsPanel.setBorder(BorderFactory.createTitledBorder(Messages
                .getString("MainWindow.1"))); //$NON-NLS-1$
        settingsTab.add(graphicsPanel, "2, 4"); //$NON-NLS-1$
        graphicsPanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("left:default"), //$NON-NLS-1$
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"), //$NON-NLS-1$
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC, }, new RowSpec[] {
                FormFactory.NARROW_LINE_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        antialiasingLabel = new JLabel(Messages.getString("MainWindow.126")); //$NON-NLS-1$
        graphicsPanel.add(antialiasingLabel, "2, 2, left, default"); //$NON-NLS-1$

        antialiasingComboBox = new JComboBox<NamedItem<Antialiasing>>();
        antialiasingComboBox.addItem(new NamedItem<Antialiasing>(
                Messages.getString("MainWindow.noAntialiasing"), Antialiasing.NONE)); //$NON-NLS-1$
        antialiasingComboBox.addItem(new NamedItem<Antialiasing>(
                Messages.getString("MainWindow.msaa2x"), //$NON-NLS-1$
                Antialiasing.MSAA_2X));
        antialiasingComboBox.addItem(new NamedItem<Antialiasing>(
                Messages.getString("MainWindow.msaa4x"), //$NON-NLS-1$
                Antialiasing.MSAA_4X));
        antialiasingComboBox.addItem(new NamedItem<Antialiasing>(
                Messages.getString("MainWindow.msaa8x"), //$NON-NLS-1$
                Antialiasing.MSAA_8X));
        antialiasingComboBox.addItem(new NamedItem<Antialiasing>(
                Messages.getString("MainWindow.msaa16x"), //$NON-NLS-1$
                Antialiasing.MSAA_16X));
        antialiasingComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    NamedItem<Antialiasing> item = (NamedItem<Antialiasing>) e
                            .getItem();
                    Antialiasing type = item.getValue();
                    Settings.getInstance().putString(
                            SettingsContract.ANTIALIASING, type.name());
                }
            }
        });

        graphicsPanel.add(antialiasingComboBox, "4, 2, fill, default"); //$NON-NLS-1$

        texfilterLabel = new JLabel(Messages.getString("MainWindow.132")); //$NON-NLS-1$

        graphicsPanel.add(texfilterLabel, "2, 4, left, default"); //$NON-NLS-1$

        texfilterComboBox = new JComboBox<NamedItem<TextureFilter>>();
        texfilterComboBox.addItem(new NamedItem<TextureFilter>(Messages
                .getString("MainWindow.nearestNeighbour"), TextureFilter.NEAREST)); //$NON-NLS-1$
        texfilterComboBox.addItem(new NamedItem<TextureFilter>(Messages
                .getString("MainWindow.bilinear"), TextureFilter.BILINEAR)); //$NON-NLS-1$
        texfilterComboBox.addItem(new NamedItem<TextureFilter>(Messages
                .getString("MainWindow.trilinear"), TextureFilter.TRILINEAR)); //$NON-NLS-1$
        texfilterComboBox.addItem(new NamedItem<TextureFilter>(Messages
                .getString("MainWindow.anisotropic2x"), TextureFilter.ANISOTROPIC_2X));//$NON-NLS-1$
        texfilterComboBox.addItem(new NamedItem<TextureFilter>(Messages
                .getString("MainWindow.anisotropic4x"), TextureFilter.ANISOTROPIC_4X));//$NON-NLS-1$
        texfilterComboBox.addItem(new NamedItem<TextureFilter>(Messages
                .getString("MainWindow.anisotropic8x"), TextureFilter.ANISOTROPIC_8X));//$NON-NLS-1$
        texfilterComboBox.addItem(new NamedItem<TextureFilter>(Messages
                .getString("MainWindow.anisotropic16x"), TextureFilter.ANISOTROPIC_16X));//$NON-NLS-1$
        texfilterComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    NamedItem<TextureFilter> item = (NamedItem<TextureFilter>) e.getItem();
                    Settings.getInstance().putString(
                            SettingsContract.TEXTURE_FILTER, item.getValue().name());
                }

            }
        });
        graphicsPanel.add(texfilterComboBox, "4, 4, fill, default"); //$NON-NLS-1$

        lodLabel = new JLabel(Messages.getString("MainWindow.137")); //$NON-NLS-1$
        graphicsPanel.add(lodLabel, "2, 6, left, default"); //$NON-NLS-1$

        lodComboBox_1 = new JComboBox<NamedItem<LevelOfDetail>>();
        lodComboBox_1.addItem(new NamedItem<LevelOfDetail>(Messages
                .getString("MainWindow.139"), LevelOfDetail.LOW)); //$NON-NLS-1$
        lodComboBox_1.addItem(new NamedItem<LevelOfDetail>(Messages
                .getString("MainWindow.140"), LevelOfDetail.MEDIUM)); //$NON-NLS-1$
        lodComboBox_1.addItem(new NamedItem<LevelOfDetail>(Messages
                .getString("MainWindow.141"), LevelOfDetail.HIGH)); //$NON-NLS-1$
        lodComboBox_1.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == e.SELECTED) {
                    LevelOfDetail detail = ((NamedItem<LevelOfDetail>) e.getItem()).getValue();
                    Settings.getInstance().putString(
                            SettingsContract.LEVEL_OF_DETAIL, detail.name());
                }
            }
        });
        graphicsPanel.add(lodComboBox_1, "4, 6, fill, default"); //$NON-NLS-1$

        cachePanel = new JPanel();
        cachePanel.setBorder(new TitledBorder(null, Messages
                .getString("MainWindow.143"), TitledBorder.LEADING, //$NON-NLS-1$
                TitledBorder.TOP, null, null));
        settingsTab.add(cachePanel, "2, 6, fill, top"); //$NON-NLS-1$
        cachePanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"), }, //$NON-NLS-1$
                new RowSpec[] { FormFactory.NARROW_LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        memCacheLabel = new JLabel(Messages.getString("MainWindow.146")); //$NON-NLS-1$
        cachePanel.add(memCacheLabel, "2, 2"); //$NON-NLS-1$

        memCacheSpinner = new JSpinner();
        memCacheSpinner.setModel(new SpinnerNumberModel(Settings.getInstance().getInteger(
                SettingsContract.CACHE_SIZE_MEMORY)
                / (1024 * 1024),
                new Integer(1), null, new Integer(1)));
        memCacheSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner spinner = (JSpinner) e.getSource();
                Settings.getInstance().putInteger(
                        SettingsContract.CACHE_SIZE_MEMORY,
                        (Integer) spinner.getValue() * 1024 * 1024);
            }
        });
        cachePanel.add(memCacheSpinner, "4, 2"); //$NON-NLS-1$

        fsCacheLabel = new JLabel(Messages.getString("MainWindow.149")); //$NON-NLS-1$
        cachePanel.add(fsCacheLabel, "2, 4"); //$NON-NLS-1$

        JSpinner fsCacheSpinner = new JSpinner();
        fsCacheSpinner.setModel(new SpinnerNumberModel(Settings.getInstance().getInteger(
                SettingsContract.CACHE_SIZE_FILESYSTEM)
                / (1024 * 1024),
                new Integer(1), null, new Integer(1)));
        fsCacheSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner spinner = (JSpinner) e.getSource();
                Settings.getInstance().putInteger(
                        SettingsContract.CACHE_SIZE_FILESYSTEM,
                        (Integer) spinner.getValue() * 1024 * 1024);
            }
        });
        cachePanel.add(fsCacheSpinner, "4, 4"); //$NON-NLS-1$

        JPanel manualAboutPanel = new JPanel();
        settingsTab.add(manualAboutPanel, "2, 8, fill, bottom"); //$NON-NLS-1$
        manualAboutPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"), //$NON-NLS-1$
                ColumnSpec.decode("5dlu"), //$NON-NLS-1$
                ColumnSpec.decode("default:grow"), }, //$NON-NLS-1$
                new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, }));

        manualButton = new JButton(Messages.getString("MainWindow.156")); //$NON-NLS-1$
        manualAboutPanel.add(manualButton, "1, 1"); //$NON-NLS-1$
        manualButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                try {
                    File pdf = new File(System.getProperty("java.io.tmpdir") + File.separator
                            + "JoglEarthManual.pdf");
                    if (!pdf.exists()) {
                        FileOutputStream fos = new FileOutputStream(pdf);
                        byte[] bytes = Resource.loadBinary("Handbuch.pdf");
                        fos.write(bytes);
                        fos.close();
                    }
                    Desktop.getDesktop().open(pdf);
                } catch (IOException e) {
                    System.err.println("Error opening manual:");
                    e.printStackTrace();
                }
            }
        });
        manualButton.setIcon(loadIcon("icons/manual.png")); //$NON-NLS-1$

        aboutButton = new JButton(Messages.getString("MainWindow.159")); //$NON-NLS-1$
        aboutButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                AboutBox aboutBox = new AboutBox();
                aboutBox.setVisible(true);
            }
        });
        manualAboutPanel.add(aboutButton, "3, 1"); //$NON-NLS-1$
        aboutButton.setIcon(loadIcon("icons/info.png")); //$NON-NLS-1$
    }

    private void resetGLCanvas() {
        Antialiasing aa = Antialiasing.valueOf(Settings.getInstance().getString(
                SettingsContract.ANTIALIASING));

        GLCanvas canvas = easel.newCanvas(GLProfile.get(GLProfile.GL2), aa);
        GLContext context = new GLContext();
        canvas.addGLEventListener(context);

        if (renderer == null) {
            renderer = new Renderer(context, locationManager);
            camera = renderer.getCamera();
            camera.addCameraListener(new UICameraListener());
        } else {
            renderer.setGLContext(context);
        }

        canvas.addMouseWheelListener(new ZoomAdapter(zoomSlider, true));
        GlMouseListener l = new GlMouseListener();
        canvas.addMouseMotionListener(l);
        canvas.addMouseListener(l);
        GLKeyboardListener keyboardListener = new GLKeyboardListener();
        canvas.addKeyListener(keyboardListener);
    }

    private void initializeViewPanel() {
        viewPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"), //$NON-NLS-1$
                ColumnSpec.decode("center:20dlu"), }, new RowSpec[] { //$NON-NLS-1$
                RowSpec.decode("default:grow"), RowSpec.decode("1dlu"), //$NON-NLS-1$ //$NON-NLS-2$
                        RowSpec.decode("20dlu"), RowSpec.decode("1dlu"), })); //$NON-NLS-1$ //$NON-NLS-2$

        easel = new GLEasel();
        viewPanel.add(easel, "1, 1, fill, fill"); //$NON-NLS-1$
        resetGLCanvas();
        this.addWindowListener(new UIWindowListener());

        JPanel statusBar = new JPanel();
        viewPanel.add(statusBar, "1, 3, 2, 1, fill, fill"); //$NON-NLS-1$
        statusBar.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("50dlu"), //$NON-NLS-1$
                ColumnSpec.decode("7dlu:grow"), //$NON-NLS-1$
                ColumnSpec.decode("max(160dlu;default)"), //$NON-NLS-1$
                ColumnSpec.decode("7dlu:grow"), //$NON-NLS-1$
                ColumnSpec.decode("right:70dlu"), //$NON-NLS-1$
                FormFactory.RELATED_GAP_COLSPEC, },
                new RowSpec[] {
                        RowSpec.decode("default:grow"), })); //$NON-NLS-1$

        JPanel zoomPanel = new JPanel();
        viewPanel.add(zoomPanel, "2, 1, center, fill"); //$NON-NLS-1$
        zoomPanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec
                .decode("center:default"), }, new RowSpec[] { //$NON-NLS-1$
                FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("default:grow"), //$NON-NLS-1$
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC, }));

        JLabel zoomPlusLabel = new JLabel(""); //$NON-NLS-1$
        zoomPlusLabel.setIcon(loadIcon("icons/zoomPlus.png")); //$NON-NLS-1$
        zoomPanel.add(zoomPlusLabel, "1, 2"); //$NON-NLS-1$

        zoomSlider = new JSlider();
        zoomSlider.setMinimum(0);
        zoomSlider.setMaximum(100);
        zoomSlider.setValue(50);
        zoomSlider.setMajorTickSpacing(1);
        zoomSlider.setOrientation(SwingConstants.VERTICAL);
        zoomPanel.add(zoomSlider, "1, 4, default, fill"); //$NON-NLS-1$

        JLabel zoomMinusLabel = new JLabel(""); //$NON-NLS-1$
        zoomMinusLabel.setIcon(loadIcon("icons/zoomMinus.png")); //$NON-NLS-1$
        zoomPanel.add(zoomMinusLabel, "1, 6"); //$NON-NLS-1$

        JLabel zoomLevelLabel = new JLabel("0"); //$NON-NLS-1$
        zoomSlider.addChangeListener(new SliderWatcher(zoomLevelLabel));
        zoomPanel.add(zoomLevelLabel, "1, 8"); //$NON-NLS-1$
        zoomPlusLabel.addMouseListener(new ZoomAdapter(zoomSlider, true));
        zoomMinusLabel.addMouseListener(new ZoomAdapter(zoomSlider, false));
        JPanel scalePanel = new JPanel();
        statusBar.add(scalePanel, "2, 1, fill, fill"); //$NON-NLS-1$
        scalePanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("center:default:grow"), },
                new RowSpec[] {
                        RowSpec.decode("default:grow"),
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        RowSpec.decode("default:grow"), }));

        scaleCanvas = new Canvas();
        scaleCanvas.setBackground(Color.LIGHT_GRAY);
        scalePanel.add(scaleCanvas, "1, 2, fill, fill");

        scaleLabel = new JLabel("1 km"); //$NON-NLS-1$
        scalePanel.add(scaleLabel, "1, 3"); //$NON-NLS-1$

        JPanel coordPanel = new JPanel();
        statusBar.add(coordPanel, "4, 1, fill, fill"); //$NON-NLS-1$
        coordPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"), //$NON-NLS-1$
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("max(20dlu;pref):grow"), //$NON-NLS-1$
                ColumnSpec.decode("5dlu"), //$NON-NLS-1$
                ColumnSpec.decode("default:grow"), //$NON-NLS-1$
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("max(20dlu;pref):grow"), }, //$NON-NLS-1$
                new RowSpec[] {
                        RowSpec.decode("default:grow"), })); //$NON-NLS-1$

        latitudeLabel = new JLabel(Messages.getString("MainWindow.2")); //$NON-NLS-1$
        coordPanel.add(latitudeLabel, "1, 1, right, default"); //$NON-NLS-1$

        latitudeTextField = new JTextField();
        latitudeTextField.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_ENTER) {
                    System.out.println("Change By Lat");
                    try {
                        GeoCoordinates geo = GeoCoordinates.parseCoordinates(
                                longitudeTextField.getText(), latitudeTextField.getText());
                        camera.setPosition(geo);
                    } catch (NumberFormatException nException) {
                        GeoCoordinates geo = camera.getGeoCoordinates(new ScreenCoordinates(0.5d,
                                0.5d));
                        if (geo != null) {
                            latitudeTextField.setText(geo.getLatitudeString());
                            longitudeTextField.setText(geo.getLongitudeString());
                        } else {
                            latitudeTextField.setText(""); //$NON-NLS-1$
                            longitudeTextField.setText(""); //$NON-NLS-1$
                        }
                    }
                }

            }
        });
        coordPanel.add(latitudeTextField, "3, 1, fill, default"); //$NON-NLS-1$
        latitudeTextField.setColumns(8);
        latitudeTextField.setHorizontalAlignment(JTextField.RIGHT);

        longitudeLabel = new JLabel(Messages.getString("MainWindow.209")); //$NON-NLS-1$
        coordPanel.add(longitudeLabel, "5, 1, right, default"); //$NON-NLS-1$

        longitudeTextField = new JTextField();
        coordPanel.add(longitudeTextField, "7, 1, fill, default"); //$NON-NLS-1$
        longitudeTextField.setColumns(8);
        longitudeTextField.setHorizontalAlignment(JTextField.RIGHT);
        longitudeTextField.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_ENTER) {
                    System.out.println("Change By Long");
                    GeoCoordinates geo = GeoCoordinates.parseCoordinates(
                            longitudeTextField.getText(), latitudeTextField.getText());
                    camera.setPosition(geo);
                }

            }
        });

        progressBar = new JProgressBar();
        statusBar.add(progressBar, "6, 1"); //$NON-NLS-1$
        progressBar.setStringPainted(true);
        progressBar.setValue(100);
    }

    private void registerListeners() {
        languageComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<IconizedItem<Locale>> cBox = (JComboBox<IconizedItem<Locale>>) e
                        .getSource();
                IconizedItem<Locale> item = (IconizedItem<Locale>) cBox
                        .getSelectedItem();
                Locale language = item.getValue();
                Messages.setLocale(language);
                Settings.getInstance().putString(SettingsContract.LANGUAGE,
                        language.getLanguage());
                loadLanguage();
                //TODO System.err.println("Change Language!"); //$NON-NLS-1$
            }
        });
        displayModeComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (arg0.getStateChange() == ItemEvent.SELECTED) {
                    if (camera != null) {
                        IconizedItem<DisplayMode> selected = (IconizedItem<DisplayMode>) arg0
                                .getItem();
                        if (selected != null) {
                            DisplayMode mode = selected.getValue();
                            mapOptionsPanel
                                    .setVisible(mode != DisplayMode.SOLAR_SYSTEM);
                            renderer.setDisplayMode(mode);
                            switch (mode) {
                                case SOLAR_SYSTEM:
                                case GLOBE_MAP:
                                case PLANE_MAP:
                                    Settings.getInstance().putString(SettingsContract.DISPLAY_MODE,
                                            mode.name());
                                    break;
                                default:
                                    // TODO: prob remove this line
                                    System.err
                                            .println("Unknown DisplayMode in comboBox!"); //$NON-NLS-1$
                                    break;
                            }
                        }
                    } else {
                        //TODO System.err.println(Messages.getString("MainWindow.51")); //$NON-NLS-1$
                    }
                }

            }
        });
        paraMapTypeComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    IconizedItem<MapConfiguration> item = (IconizedItem<MapConfiguration>) e
                            .getItem();
                    renderer.setMapConfiguration((MapConfiguration) item.getValue());

                }
            }
        });
        heightMapCheckBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                Settings settings = Settings.getInstance();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    settings.putBoolean(SettingsContract.HEIGHT_MAP_ENABLED, new Boolean(true));
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    settings.putBoolean(SettingsContract.HEIGHT_MAP_ENABLED, new Boolean(false));
                }
            }
        });
        Action action = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JoglEarth.shutDown();
                MainWindow.this.dispose();
            }
        };
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Q")); //$NON-NLS-1$
        ActionMap actionMap = new ActionMapUIResource();
        actionMap.put("action_quit", action); //$NON-NLS-1$
        InputMap inputMap = new ComponentInputMap(rootPane);
        inputMap.put(KeyStroke.getKeyStroke("control Q"), "action_quit"); //$NON-NLS-1$ //$NON-NLS-2$
        SwingUtilities.replaceUIActionMap(rootPane, actionMap);
        SwingUtilities.replaceUIInputMap(rootPane, JComponent.WHEN_IN_FOCUSED_WINDOW,
                inputMap);
        searchButton.setActionCommand(AC_SEARCH);
        searchButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String queryString = searchTextField.getText();
                if (localSearchRadioButton.isSelected()) {
                    MapConfiguration configuration = ((IconizedItem<MapConfiguration>) paraMapTypeComboBox
                            .getSelectedItem()).getValue();
                    locationManager.searchLocal(
                            queryString,
                            CameraUtils.getVisibleTiles(camera,
                                    configuration.getOptimalTileLayout(camera, easel.getSize())));
                } else if (globalSearchRadioButton.isSelected()) {
                    locationManager.searchGlobal(queryString);
                }
            }
        });
        locationManager.addLocationListener(new UISearchResultListener(searchResultList));
        progressManager.addProgressListener(new UIProgressListener());
        userTagButton.addActionListener(new UsertagButtonListener());
        Settings.getInstance().addSettingsListener(SettingsContract.USER_LOCATIONS,
                new UIUserLocationListener());
        searchResultList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    Location location = searchResultList.getSelectedValue();
                    System.out.println("SearchResult Setting to lon: "
                            + location.point.getLongitudeString() + " lat: "
                            + location.point.getLatitudeString());
                    camera.setPosition(location.point);
                    updateDetails(location);
                }
            }
        });
    }

    private void loadLanguage() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                overlayPanel.setBorder(BorderFactory
                        .createTitledBorder(Messages.getString("MainWindow.0"))); //$NON-NLS-1$ 
                graphicsPanel.setBorder(BorderFactory
                        .createTitledBorder(Messages.getString("MainWindow.1"))); //$NON-NLS-1$
                userTagPanel.setBorder(BorderFactory
                        .createTitledBorder(Messages.getString("MainWindow.99"))); //$NON-NLS-1$
                searchPanel.setBorder(BorderFactory.createTitledBorder(Messages
                        .getString("MainWindow.82"))); //$NON-NLS-1$
                localSearchRadioButton.setText(Messages
                        .getString("MainWindow.96")); //$NON-NLS-1$
                globalSearchRadioButton.setText(Messages
                        .getString("MainWindow.97")); //$NON-NLS-1$
                languagePanel.setBorder(new TitledBorder(null, Messages
                        .getString("MainWindow.110"), TitledBorder.LEADING, //$NON-NLS-1$
                        TitledBorder.TOP, null, null));
                int index = paraMapTypeComboBox.getSelectedIndex();
                if (index == -1)
                    index = 0;
                paraMapTypeComboBox.removeAllItems();
                paraMapTypeComboBox
                        .addItem(new IconizedItem<MapConfiguration>(
                                Messages.getString("MainWindow.70"), //$NON-NLS-1$
                                loadIcon("icons/mapSatellite.png"), new SingleMapConfiguration(SingleMapType.SATELLITE))); //$NON-NLS-1$
                paraMapTypeComboBox
                        .addItem(new IconizedItem<MapConfiguration>(
                                Messages.getString("MainWindow.72"), //$NON-NLS-1$
                                loadIcon("icons/mapOSM.png"), new OSMMapConfiguration(OSMMapType.OSM_NOLABELS))); //$NON-NLS-1$
                paraMapTypeComboBox
                        .addItem(new IconizedItem<MapConfiguration>(
                                Messages.getString("MainWindow.4"), //$NON-NLS-1$
                                loadIcon("icons/mapOSM.png"), new OSMMapConfiguration(OSMMapType.CYCLING))); //$NON-NLS-1$
                paraMapTypeComboBox
                        .addItem(new IconizedItem<MapConfiguration>(
                                Messages.getString("MainWindow.5"), //$NON-NLS-1$
                                loadIcon("icons/mapOSM.png"), new OSMMapConfiguration(OSMMapType.HIKING))); //$NON-NLS-1$
                paraMapTypeComboBox
                        .addItem(new IconizedItem<MapConfiguration>(
                                Messages.getString("MainWindow.6"), //$NON-NLS-1$
                                loadIcon("icons/mapOSM.png"), new OSMMapConfiguration(OSMMapType.MAPNIK))); //$NON-NLS-1$
                paraMapTypeComboBox
                        .addItem(new IconizedItem<MapConfiguration>(
                                Messages.getString("MainWindow.7"), //$NON-NLS-1$
                                loadIcon("icons/mapOSM.png"), new OSMMapConfiguration(OSMMapType.OSM2WORLD))); //$NON-NLS-1$
                paraMapTypeComboBox
                        .addItem(new IconizedItem<MapConfiguration>(
                                Messages.getString("MainWindow.74"), //$NON-NLS-1$
                                loadIcon("icons/mapChildren.png"), new SingleMapConfiguration(SingleMapType.CHILDREN))); //$NON-NLS-1$
                paraMapTypeComboBox.setSelectedIndex(index);
                antialiasingLabel.setText(Messages.getString("MainWindow.126")); //$NON-NLS-1$
                index = antialiasingComboBox.getSelectedIndex();
                antialiasingComboBox.removeAllItems();
                antialiasingComboBox.addItem(new NamedItem<Antialiasing>(
                        Messages.getString("MainWindow.noAntialiasing"), Antialiasing.NONE)); //$NON-NLS-1$
                antialiasingComboBox.addItem(new NamedItem<Antialiasing>(
                        Messages.getString("MainWindow.msaa2x"), //$NON-NLS-1$
                        Antialiasing.MSAA_2X));
                antialiasingComboBox.addItem(new NamedItem<Antialiasing>(
                        Messages.getString("MainWindow.msaa4x"), //$NON-NLS-1$
                        Antialiasing.MSAA_4X));
                antialiasingComboBox.addItem(new NamedItem<Antialiasing>(
                        Messages.getString("MainWindow.msaa8x"), //$NON-NLS-1$
                        Antialiasing.MSAA_8X));
                antialiasingComboBox.addItem(new NamedItem<Antialiasing>(
                        Messages.getString("MainWindow.msaa16x"), //$NON-NLS-1$
                        Antialiasing.MSAA_16X));
                antialiasingComboBox.setSelectedIndex(index);
                texfilterLabel.setText(Messages.getString("MainWindow.132")); //$NON-NLS-1$
                index = texfilterComboBox.getSelectedIndex();
                texfilterComboBox.removeAllItems();
                texfilterComboBox.addItem(new NamedItem<TextureFilter>(Messages
                        .getString("MainWindow.nearestNeighbour"), TextureFilter.NEAREST)); //$NON-NLS-1$
                texfilterComboBox.addItem(new NamedItem<TextureFilter>(Messages
                        .getString("MainWindow.bilinear"), TextureFilter.BILINEAR)); //$NON-NLS-1$
                texfilterComboBox.addItem(new NamedItem<TextureFilter>(Messages
                        .getString("MainWindow.trilinear"), TextureFilter.TRILINEAR)); //$NON-NLS-1$
                texfilterComboBox.addItem(new NamedItem<TextureFilter>(Messages
                        .getString("MainWindow.anisotropic2x"), TextureFilter.ANISOTROPIC_2X));//$NON-NLS-1$
                texfilterComboBox.addItem(new NamedItem<TextureFilter>(Messages
                        .getString("MainWindow.anisotropic4x"), TextureFilter.ANISOTROPIC_4X));//$NON-NLS-1$
                texfilterComboBox.addItem(new NamedItem<TextureFilter>(Messages
                        .getString("MainWindow.anisotropic8x"), TextureFilter.ANISOTROPIC_8X));//$NON-NLS-1$
                texfilterComboBox.addItem(new NamedItem<TextureFilter>(Messages
                        .getString("MainWindow.anisotropic16x"), TextureFilter.ANISOTROPIC_16X));//$NON-NLS-1$
                texfilterComboBox.setSelectedIndex(index);
                lodLabel.setText(Messages.getString("MainWindow.137")); //$NON-NLS-1$
                index = lodComboBox_1.getSelectedIndex();
                lodComboBox_1.removeAllItems();
                lodComboBox_1.addItem(new NamedItem<LevelOfDetail>(Messages
                        .getString("MainWindow.139"), LevelOfDetail.LOW)); //$NON-NLS-1$
                lodComboBox_1.addItem(new NamedItem<LevelOfDetail>(Messages
                        .getString("MainWindow.140"), LevelOfDetail.MEDIUM)); //$NON-NLS-1$
                lodComboBox_1.addItem(new NamedItem<LevelOfDetail>(Messages
                        .getString("MainWindow.141"), LevelOfDetail.HIGH)); //$NON-NLS-1$
                lodComboBox_1.setSelectedIndex(index);
                cachePanel.setBorder(new TitledBorder(null, Messages
                        .getString("MainWindow.143"), TitledBorder.LEADING, //$NON-NLS-1$
                        TitledBorder.TOP, null, null));
                memCacheLabel.setText(Messages.getString("MainWindow.146")); //$NON-NLS-1$
                fsCacheLabel.setText(Messages.getString("MainWindow.149")); //$NON-NLS-1$
                manualButton.setText(Messages.getString("MainWindow.156")); //$NON-NLS-1$
                aboutButton.setText(Messages.getString("MainWindow.159")); //$NON-NLS-1$
                detailsPanel.setBorder(BorderFactory.createTitledBorder(Messages
                        .getString("MainWindow.35"))); //$NON-NLS-1$
                detailNameLabel.setText(Messages.getString("MainWindow.3")); //$NON-NLS-1$
                detailDescriptionLabel.setText(Messages.getString("MainWindow.42")); //$NON-NLS-1$
                userTagButton.setText(Messages.getString("MainWindow.40")); //$NON-NLS-1$
                displayModeLabel.setText(Messages.getString("MainWindow.48")); //$NON-NLS-1$
                index = displayModeComboBox.getSelectedIndex();
                displayModeComboBox.removeAllItems();
                displayModeComboBox.addItem(new IconizedItem<DisplayMode>(Messages
                        .getString("MainWindow.52"), //$NON-NLS-1$
                        loadIcon("icons/modeSolar.png"), DisplayMode.SOLAR_SYSTEM)); //$NON-NLS-1$
                displayModeComboBox.addItem(new IconizedItem<DisplayMode>(Messages
                        .getString("MainWindow.54"), //$NON-NLS-1$
                        loadIcon("icons/modeGlobe.png"), DisplayMode.GLOBE_MAP)); //$NON-NLS-1$
                displayModeComboBox.addItem(new IconizedItem<DisplayMode>(Messages
                        .getString("MainWindow.56"), //$NON-NLS-1$
                        loadIcon("icons/modePlane.png"), DisplayMode.PLANE_MAP)); //$NON-NLS-1$
                displayModeComboBox.setSelectedIndex(index);
                mapTypeLabel.setText(Messages.getString("MainWindow.62")); //$NON-NLS-1$
                heightMapCheckBox.setText(Messages.getString("MainWindow.65")); //$NON-NLS-1$
                sideBarTabs.setTitleAt(0, Messages.getString("MainWindow.16")); //$NON-NLS-1$
                sideBarTabs.setTitleAt(1, Messages.getString("MainWindow.18")); //$NON-NLS-1$
                sideBarTabs.setTitleAt(2, Messages.getString("MainWindow.20")); //$NON-NLS-1$
                overlaysPanel.setLayout(new GridLayout(17, 1, 0, 0));

                for (Entry<JCheckBox, LocationType> elementEntry : checkboxToLocationTypeMap
                        .entrySet()) {
                    JCheckBox box = elementEntry.getKey();
                    switch (elementEntry.getValue()) {
                        case RESTAURANT:
                            box.setText(Messages.getString("MainWindow.restaurant")); //$NON-NLS-1$
                            break;
                        case NIGHTLIFE:
                            box.setText(Messages.getString("MainWindow.nightlife")); //$NON-NLS-1$
                            break;
                        case BANK:
                            box.setText(Messages.getString("MainWindow.bank")); //$NON-NLS-1$
                            break;
                        case TOILETS:
                            box.setText(Messages.getString("MainWindow.toilets")); //$NON-NLS-1$
                            break;
                        case GROCERY_SHOPS:
                            box.setText(Messages.getString("MainWindow.grocery_store")); //$NON-NLS-1$
                            break;
                        case SHOPS:
                            box.setText(Messages.getString("MainWindow.shops")); //$NON-NLS-1$
                            break;
                        case ACTIVITY:
                            box.setText(Messages.getString("MainWindow.activity")); //$NON-NLS-1$
                            break;
                        case HIKING_AND_CYCLING:
                            box.setText(Messages.getString("MainWindow.hiking_and_cycling")); //$NON-NLS-1$
                            break;
                        case EDUCATION:
                            box.setText(Messages.getString("MainWindow.education")); //$NON-NLS-1$
                            break;
                        case HEALTH:
                            box.setText(Messages.getString("MainWindow.health")); //$NON-NLS-1$
                            break;
                        case POST:
                            box.setText(Messages.getString("MainWindow.post")); //$NON-NLS-1$
                            break;
                        case HOTELS:
                            box.setText(Messages.getString("MainWindow.hotels")); //$NON-NLS-1$
                            break;
                        case CITY:
                            box.setText(Messages.getString("MainWindow.city")); //$NON-NLS-1$
                            break;
                        case TOWN:
                            box.setText(Messages.getString("MainWindow.town")); //$NON-NLS-1$
                            break;
                        case VILLAGE:
                            box.setText(Messages.getString("MainWindow.village")); //$NON-NLS-1$
                            break;
                        case USER_TAG:
                            box.setText(Messages.getString("MainWindow.user_tags")); //$NON-NLS-1$
                            break;
                        case SEARCH:
                            box.setText(Messages.getString("MainWindow.search")); //$NON-NLS-1$
                            break;
                        default:
                            break;
                    }
                }

            }
        });

    }

    private void updateDetails(Location location) {
        if (location.name != null)
            detailNameLabel.setText(location.name);
        else
            detailNameLabel.setText(Messages.getString("MainWindow.3"));
        if (location.details != null)
            detailDescriptionLabel.setText(location.details);
        else
            detailDescriptionLabel.setText(Messages.getString("MainWindow.42"));
    }

    /**
     * Constructor.
     * 
     * @param locationManager The <code>LocationManager</code> associated with this window.
     */
    public MainWindow(final LocationManager locationManager) {
        this.locationManager = locationManager;
        progressManager = ProgressManager.getInstance();
        String lang = Settings.getInstance().getString(
                SettingsContract.LANGUAGE);
        // TODO System.err.println("LangSetting At Start:" + lang);
        Locale.Builder builder = new Locale.Builder();
        builder.setLanguage(lang);
        Locale l = builder.build();
        if (l.getLanguage().equals(Locale.GERMAN.getLanguage())) {
            //TODO System.err.println("Set Lang to German at start!"); //$NON-NLS-1$
            Messages.setLocale(Locale.GERMAN);
        } else {
            //TODO System.err.println("Set Lang to English at start!"); //$NON-NLS-1$
            Messages.setLocale(Locale.ENGLISH);
        }
        initializeWindow();
        initializeViewTab();
        initializePlacesTab();
        initializeSettingsTab();
        initializeDetailsPanel();
        initializeViewPanel();
        loadLanguage();
        // TODO: remove this hack for the GUIEditor

        if (l.getLanguage().equals(Locale.GERMAN.getLanguage())) {
            //TODO System.err.println("Set Lang to German at start!"); //$NON-NLS-1$
            languageComboBox.setSelectedIndex(1);
        } else {
            //TODO System.err.println("Set Lang to English at start!"); //$NON-NLS-1$
            languageComboBox.setSelectedIndex(0);
        }
        registerListeners();
        zoomSlider.setValue(0);
    }


    private class UISettingsListener implements SettingsListener {

        @Override
        public void settingsChanged(String key, Object valOld, Object valNew) {
            if (key.equals(SettingsContract.ANTIALIASING)) {
                resetGLCanvas();
            }
        }

    }

    private class UIUserLocationListener implements SettingsListener {

        @Override
        public void settingsChanged(String key, Object valOld, Object valNew) {
            System.err.println("Got a UserLocation Change: " + key);
            if (key.equals(SettingsContract.USER_LOCATIONS)) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        buttonToLocationMap.clear();
                        userTagListPanel.removeAll();
                        Set<Location> uLocations = Settings.getInstance().getLocations(
                                SettingsContract.USER_LOCATIONS);
                        for (final Location l : uLocations) {
                            System.out.println("Name: " + l.name);
                            JButton button = new JButton(l.name);
                            button.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    camera.setPosition(l.point);
                                }
                            });
                            buttonToLocationMap.put(button, l);
                            userTagListPanel.add(button);
                        }
                    }
                });
            }

        }

    }

    private class UIOverlaySelectionListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            JCheckBox source = (JCheckBox) e.getItemSelectable();
            LocationType locationType = checkboxToLocationTypeMap.get(source);
            locationManager.setLocationTypeActive(locationType,
                    e.getStateChange() == ItemEvent.SELECTED);
        }

    }

    private class UICameraListener implements CameraListener {

        private static final double rad = 6371.009 * 1000;
        private Dimension dimensionCvas = new Dimension();
        private Dimension dimensionScaleCanv = new Dimension();


        @Override
        public void cameraViewChanged() {
            GeoCoordinates geo = camera.getGeoCoordinates(new ScreenCoordinates(0.5d, 0.5d));
            if (geo != null) {
                latitudeTextField.setText(geo.getLatitudeString());
                longitudeTextField.setText(geo.getLongitudeString());
            } else {
                latitudeTextField.setText(""); //$NON-NLS-1$
                longitudeTextField.setText(""); //$NON-NLS-1$
            }
            scaleLabel.setText(Double.toString(camera.getScale()));
            System.out.println(String.valueOf(camera.getScale()));
            System.out.println(String.valueOf(Math.round(camera.getScale() * rad) + "m"));
            easel.getSize(dimensionCvas);
            scaleCanvas.getSize(dimensionScaleCanv);
            System.out.println("ScaleCanvSize Width: " + dimensionScaleCanv.getWidth());
            double sizeScreen = camera.getScale() * rad;
            System.out.println("SizeScreen: " + sizeScreen);
            double scale = dimensionCvas.getWidth() / dimensionScaleCanv.getWidth();
            System.out.println("Scale: " + scale);
            double scaleSize = Math.round(sizeScreen / scale);
            System.out.println("ScaleSize: " + scaleSize);
            scaleLabel.setText(String.valueOf(scaleSize));
            Location location = locationManager.getDetails(camera
                    .getGeoCoordinates(new ScreenCoordinates(0.5d, 0.5d)));
            updateDetails(location);
        }

    }

    private class GlMouseListener extends MouseAdapter {

        ScreenCoordinates lastPos;


        private ScreenCoordinates getScreenCoordinates(Point p) {
            Dimension canvasSize = easel.getCanvas().getSize();
            return new ScreenCoordinates(p.getX() / canvasSize.width,
                    p.getY() / canvasSize.height);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            ScreenCoordinates newPos = getScreenCoordinates(e.getPoint());
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (lastPos != null && newPos != null) {
                    GeoCoordinates lastGeo = camera.getGeoCoordinates(lastPos);
                    GeoCoordinates newGeo = camera.getGeoCoordinates(newPos);
                    if (lastGeo != null && newGeo != null) {
                        double deltaLon = -signum(newPos.x - lastPos.x)
                                * abs(newGeo.getLongitude() - lastGeo.getLongitude());
                        double deltaLat = signum(newPos.y - lastPos.y)
                                * abs(newGeo.getLatitude() - lastGeo.getLatitude());
                        camera.move(deltaLon, deltaLat);
                        /*
                         * TODO System.out.format(
                         * 
                         * "Move: deltaX=%g,  deltaY=%g, deltaLon=%g, deltaLat=%g\n", newPos.x -
                         * lastPos.x, newPos.y - lastPos.y, newGeo.getLongitude() -
                         * lastGeo.getLongitude(), newGeo.getLatitude() - lastGeo.getLatitude());
                         */
                    }
                }
            } else if (SwingUtilities.isRightMouseButton(e)) {
                double diffY = newPos.x - lastPos.x;
                double diffX = newPos.y - lastPos.y;
                // -pi/2,pi/2
                cTiltX += (diffX * SCALE_TILT);
                cTiltY += (diffY * SCALE_TILT);
                if (cTiltX < -(Math.PI / 2)) {
                    cTiltX = -(Math.PI / 2);
                }
                if (cTiltY < -(Math.PI / 2)) {
                    cTiltY = -(Math.PI / 2);
                }
                if (cTiltX > (Math.PI / 2)) {
                    cTiltX = (Math.PI / 2);
                }
                if (cTiltY > (Math.PI / 2)) {
                    cTiltY = (Math.PI / 2);
                }
                camera.setTilt(cTiltX, cTiltY);
            }
            lastPos = newPos;
            super.mouseDragged(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            lastPos = getScreenCoordinates(e.getPoint());
            super.mousePressed(e);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() >= 2) {
                ScreenCoordinates screenCoord = getScreenCoordinates(e.getPoint());
                GeoCoordinates geoCoord = camera.getGeoCoordinates(screenCoord);
                camera.setPosition(geoCoord);
            }

            super.mouseClicked(e);
        }
    }

    private class GLKeyboardListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            ScreenCoordinates lastPos = new ScreenCoordinates(0.5d, 0.5d);
            GeoCoordinates lastGeo = camera.getGeoCoordinates(lastPos);
            GeoCoordinates newGeo = null;
            ScreenCoordinates newPos = null;
            boolean tiltChanged = false;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    newPos = new ScreenCoordinates(0.6d, 0.5d);
                    newGeo = camera.getGeoCoordinates(newPos);
                    break;
                case KeyEvent.VK_RIGHT:
                    newPos = new ScreenCoordinates(0.4d, 0.5d);
                    newGeo = camera.getGeoCoordinates(newPos);
                    break;
                case KeyEvent.VK_UP:
                    newPos = new ScreenCoordinates(0.5d, 0.6d);
                    newGeo = camera.getGeoCoordinates(newPos);
                    break;
                case KeyEvent.VK_DOWN:
                    newPos = new ScreenCoordinates(0.5d, 0.4d);
                    newGeo = camera.getGeoCoordinates(newPos);
                    break;
                case KeyEvent.VK_PAGE_UP:
                    cTiltX += 0.1;
                    tiltChanged = true;
                    break;
                case KeyEvent.VK_PAGE_DOWN:
                    cTiltX -= 0.1;
                    tiltChanged = true;
                    break;
                case KeyEvent.VK_HOME:
                    cTiltY += 0.1;
                    tiltChanged = true;
                    break;
                case KeyEvent.VK_END:
                    cTiltY -= 0.1;
                    tiltChanged = true;
                    break;
                case KeyEvent.VK_0:
                    cTiltY = 0.0d;
                    cTiltX = 0.0d;
                    tiltChanged = true;
                    break;
                case KeyEvent.VK_PLUS:
                case KeyEvent.VK_ADD:
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            zoomSlider.setValue(zoomSlider.getValue() + 1);
                        }
                    });
                    break;
                case KeyEvent.VK_MINUS:
                case KeyEvent.VK_SUBTRACT:
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            zoomSlider.setValue(zoomSlider.getValue() - 1);
                        }
                    });
                    break;
                default:
                    System.out.println("KeyCode: " + e.getKeyCode());
                    break;
            }
            if (newGeo != null) {
                double deltaLon = -signum(newPos.x - lastPos.x)
                        * abs(newGeo.getLongitude() - lastGeo.getLongitude());
                double deltaLat = signum(newPos.y - lastPos.y)
                        * abs(newGeo.getLatitude() - lastGeo.getLatitude());
                camera.move(deltaLon, deltaLat);
            }
            if (tiltChanged) {
                if (cTiltX < -(Math.PI / 2)) {
                    cTiltX = -(Math.PI / 2);
                }
                if (cTiltY < -(Math.PI / 2)) {
                    cTiltY = -(Math.PI / 2);
                }
                if (cTiltX > (Math.PI / 2)) {
                    cTiltX = (Math.PI / 2);
                }
                if (cTiltY > (Math.PI / 2)) {
                    cTiltY = (Math.PI / 2);
                }
                camera.setTilt(cTiltX, cTiltY);
            }
            super.keyPressed(e);
        }
    }

    private class UISearchResultListener implements LocationListener {

        private JList<Location> list;


        public UISearchResultListener(JList<Location> list) {
            this.list = list;
        }

        @Override
        public void searchResultsAvailable(final Collection<Location> results) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    System.err.println("UISearchResultListener");
                    DefaultListModel<Location> model = (DefaultListModel<Location>) list
                            .getModel();
                    model.clear();
                    for (Location l : results) {
                        System.out.println("Add Element Details: " + l.details);
                        model.addElement(l);
                    }
                }
            });

        }

    }

    private class ZoomAdapter extends MouseAdapter {

        private boolean increase;
        private JSlider slider;


        public ZoomAdapter(JSlider slider, boolean increase) {
            this.increase = increase;
            this.slider = slider;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            int current = slider.getValue();
            if (increase) {
                if (current < slider.getMaximum()) {
                    slider.setValue(current + 1);
                }
            } else {
                if (current > slider.getMinimum()) {
                    slider.setValue(current - 1);
                }
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int current = slider.getValue();
            int newCount = current - e.getWheelRotation();
            if (newCount < slider.getMinimum())
                newCount = slider.getMinimum();
            else if (newCount > slider.getMaximum())
                newCount = slider.getMaximum();
            slider.setValue(newCount);
        }
    }

    private class SliderWatcher implements ChangeListener {

        private JLabel label;


        public SliderWatcher(JLabel label) {
            this.label = label;
        }

        @Override
        public void stateChanged(ChangeEvent e) {

            JSlider slider = (JSlider) e.getSource();
            label.setText(Integer.toString(slider.getValue()));
            int value = slider.getValue();
            // TODO System.out.println("Zoom Changed to: "+value);
            double perc = value / (double) slider.getMaximum() * 10;
            // TODO System.out.println("Set Distance to: "+(MIN_DIST + MAX_DIST*perc));
            camera.setDistance(MIN_DIST + (MAX_DIST - MIN_DIST)
                    * (1 / (1 + perc * perc * perc * 10)));

        }

    }

    private class UsertagButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.err.println("ActionPerformed! " + e.toString());
            final GeoCoordinates geo = camera.getGeoCoordinates(new ScreenCoordinates(0.5d, 0.5d));
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    Location loc = new Location(geo, LocationType.USER_TAG, "", "");
                    LocationEditDialog dial = new LocationEditDialog(loc);
                    dial.setVisible(true);
                }
            });
        }
    }

    private class UIWindowListener extends WindowAdapter {

        @Override
        public void windowClosed(WindowEvent e) {
            super.windowClosed(e);
            JoglEarth.shutDown();
        }
    }

    private class UIProgressListener implements ProgressListener {

        @Override
        public void updateProgress(double prog) {
            System.err.println("Progress Upade: " + prog);
            progressBar.setValue((int) (100 * prog));
        }

        @Override
        public void abortPendingRequests() {}

    }

    private class LocationListCellRenderer extends JLabel implements ListCellRenderer<Location> {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;


        public LocationListCellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Location> list,
                Location value, int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setText(value.details);
            return this;
        }

    }

}
