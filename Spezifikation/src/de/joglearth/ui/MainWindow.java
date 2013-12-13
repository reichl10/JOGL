package de.joglearth.ui;

import static de.joglearth.util.Resource.loadIcon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Locale;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.joglearth.JoglEarth;
import de.joglearth.geometry.Camera;
import de.joglearth.geometry.CameraListener;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.PlaneGeometry;
import de.joglearth.geometry.ScreenCoordinates;
import de.joglearth.geometry.SphereGeometry;
import de.joglearth.rendering.AntialiasingType;
import de.joglearth.rendering.DisplayMode;
import de.joglearth.rendering.LevelOfDetail;
import de.joglearth.rendering.Renderer;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.settings.SettingsListener;
import de.joglearth.surface.Location;
import de.joglearth.surface.LocationListener;
import de.joglearth.surface.LocationManager;
import de.joglearth.surface.MapLayout;
import de.joglearth.surface.SingleMapType;
import de.joglearth.surface.SurfaceListener;
import de.joglearth.surface.TiledMapType;


/**
 * The main UI window class.
 */
public class MainWindow extends JFrame {

    /**
     * @internal
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            MainWindow dialog = new MainWindow(null);
            dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static ImageIcon hideIcon = loadIcon("icons/hide.png"); //$NON-NLS-1$
    private static ImageIcon showIcon = loadIcon("icons/show.png"); //$NON-NLS-1$
    private GLCanvas glCanvas;

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
    private JTextField textField;
    private JLabel sidebarHideIconLabel;
    private JPanel sideBarHideLinePanel;
    private JPanel mapOptionsPanel;
    private JComboBox<?> mapTypeComboBox;
    private JComboBox<IconizedItem<DisplayMode>> displayModeComboBox;
    private JPanel viewTab, placesTab, settingsTab, detailsPanel, viewPanel;
    private JCheckBox heightMapCheckBox;
    private JComboBox<IconizedItem<Locale>> languageComboBox;
    private JComboBox<NamedItem<AntialiasingType>> antialiasingComboBox;
    private JComboBox<NamedItem<Boolean>> texfilterComboBox;
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
    private JComboBox<IconizedItem<MapTypePair>> paraMapTypeComboBox;
    private JTabbedPane sideBarTabs;
    private JSlider zoomSlider;


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

    private class MapTypePair {

        public MapLayout layout;
        /**
         * Can be {@link SingleMapType} or {@link TildMapType}.
         */
        public Object type;


        public MapTypePair(SingleMapType s) {
            layout = MapLayout.SINGLE;
            type = s;
        }

        public MapTypePair(TiledMapType s) {
            layout = MapLayout.TILED;
            type = s;
        }
    }


    private void initializeWindow() {
        setBackground(UIManager.getColor("inactiveCaption")); //$NON-NLS-1$
        getContentPane().setBackground(UIManager.getColor("inactiveCaption")); //$NON-NLS-1$
        setTitle(JoglEarth.PRODUCT_NAME);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        this.viewEventListener = new ViewEventListener(camera);
        getContentPane().setLayout(
                new FormLayout(new ColumnSpec[] { ColumnSpec.decode("130dlu"), //$NON-NLS-1$
                        ColumnSpec.decode("15px"), //$NON-NLS-1$
                        ColumnSpec.decode("default:grow"), }, //$NON-NLS-1$
                        new RowSpec[] { RowSpec.decode("default:grow"), })); //$NON-NLS-1$

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
                ColumnSpec.decode("default:grow"), //$NON-NLS-1$
                ColumnSpec.decode("5px"), //$NON-NLS-1$
                ColumnSpec.decode("default:grow"), }, //$NON-NLS-1$
                new RowSpec[] { RowSpec.decode("4dlu:grow"), //$NON-NLS-1$
                        RowSpec.decode("0dlu"), })); //$NON-NLS-1$

        sideBarHideLinePanel = new JPanel();
        sideBarHideLinePanel.setBackground(Color.LIGHT_GRAY);
        sideBarHidePanel.add(sideBarHideLinePanel, "2, 1, fill, fill"); //$NON-NLS-1$
        sideBarHideLinePanel.setLayout(new FormLayout(
                new ColumnSpec[] { ColumnSpec.decode("default:grow"), }, //$NON-NLS-1$
                new RowSpec[] { RowSpec.decode("default:grow"), })); //$NON-NLS-1$

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
        userTagButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {}
        });

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

        paraMapTypeComboBox = new JComboBox<IconizedItem<MapTypePair>>();
        mapTypeComboBox = paraMapTypeComboBox;
        mapOptionsPanel.add(paraMapTypeComboBox, "1, 3"); //$NON-NLS-1$
        paraMapTypeComboBox
                .setRenderer(new IconListCellRenderer<IconizedItem<MapTypePair>>());
        paraMapTypeComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    JComboBox<IconizedItem<MapTypePair>> comboBox = (JComboBox<IconizedItem<MapTypePair>>) e
                            .getSource();
                    MapTypePair mtp = ((IconizedItem<MapTypePair>) comboBox
                            .getSelectedItem()).getValue();
                    if (mtp.type instanceof SingleMapType) {
                        SingleMapType mapType = (SingleMapType) mtp.type;
                        Settings.getInstance().putString(
                                SettingsContract.MAP_TYPE, mapType.name());
                    } else if (mtp.type instanceof TiledMapType) {
                        TiledMapType type = (TiledMapType) mtp.type;
                        Settings.getInstance().putString(
                                SettingsContract.MAP_TYPE, type.name());
                    }
                }
            }
        });
        heightMapCheckBox = new JCheckBox(Messages.getString("MainWindow.65")); //$NON-NLS-1$
        heightMapCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox box = (JCheckBox) e.getSource();
                Settings.getInstance().putBoolean(
                        SettingsContract.HEIGHT_MAP_ENABLED,
                        new Boolean(box.isSelected()));
            }
        });
        mapOptionsPanel.add(heightMapCheckBox, "1, 5"); //$NON-NLS-1$

        JLabel logoLabel = new JLabel(""); //$NON-NLS-1$
        viewTab.add(logoLabel, "2, 8, center, bottom"); //$NON-NLS-1$
        logoLabel.setVerticalAlignment(SwingConstants.TOP);
        logoLabel.setIcon(loadIcon("icons/logo.png")); //$NON-NLS-1$
        paraMapTypeComboBox
                .addItem(new IconizedItem<MapTypePair>(
                        Messages.getString("MainWindow.70"), //$NON-NLS-1$
                        loadIcon("icons/mapSatellite.png"), new MapTypePair(SingleMapType.SATELLITE))); //$NON-NLS-1$
        paraMapTypeComboBox
                .addItem(new IconizedItem<MapTypePair>(
                        Messages.getString("MainWindow.72"), //$NON-NLS-1$
                        loadIcon("icons/mapOSM.png"), new MapTypePair(TiledMapType.OSM_MAPNIK))); //$NON-NLS-1$
        paraMapTypeComboBox
        .addItem(new IconizedItem<MapTypePair>(
                Messages.getString("MainWindow.4"), //$NON-NLS-1$
                loadIcon("icons/mapOSM.png"), new MapTypePair(TiledMapType.CYCLING))); //$NON-NLS-1$
        paraMapTypeComboBox
        .addItem(new IconizedItem<MapTypePair>(
                Messages.getString("MainWindow.5"), //$NON-NLS-1$
                loadIcon("icons/mapOSM.png"), new MapTypePair(TiledMapType.HIKING))); //$NON-NLS-1$
        paraMapTypeComboBox
        .addItem(new IconizedItem<MapTypePair>(
                Messages.getString("MainWindow.6"), //$NON-NLS-1$
                loadIcon("icons/mapOSM.png"), new MapTypePair(TiledMapType.SKIING))); //$NON-NLS-1$
        paraMapTypeComboBox
        .addItem(new IconizedItem<MapTypePair>(
               Messages.getString("MainWindow.7"), //$NON-NLS-1$
                loadIcon("icons/mapOSM.png"), new MapTypePair(TiledMapType.OSM2WORLD))); //$NON-NLS-1$
        paraMapTypeComboBox
                .addItem(new IconizedItem<MapTypePair>(
                        Messages.getString("MainWindow.74"), //$NON-NLS-1$
                        loadIcon("icons/mapChildren.png"), new MapTypePair(SingleMapType.CHILDREN))); //$NON-NLS-1$

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
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC, }, new RowSpec[] {
                FormFactory.NARROW_LINE_GAP_ROWSPEC,
                RowSpec.decode("15dlu"), //$NON-NLS-1$
                RowSpec.decode("12dlu"), //$NON-NLS-1$
                FormFactory.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"), //$NON-NLS-1$
                FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        JPanel searchQueryPanel = new JPanel();
        searchPanel.add(searchQueryPanel, "2, 2, fill, top"); //$NON-NLS-1$
        searchQueryPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"), ColumnSpec.decode("2dlu"), //$NON-NLS-1$ //$NON-NLS-2$
                ColumnSpec.decode("20dlu"), }, new RowSpec[] { //$NON-NLS-1$
                FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

        textField = new JTextField();
        searchQueryPanel.add(textField, "1, 1, fill, fill"); //$NON-NLS-1$

        JButton searchButton = new JButton(loadIcon("icons/search.png")); //$NON-NLS-1$
        searchQueryPanel.add(searchButton, "3, 1, fill, fill"); //$NON-NLS-1$

        JPanel panel = new JPanel();
        searchPanel.add(panel, "2, 3, fill, fill"); //$NON-NLS-1$
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));

        localSearchRadioButton = new JRadioButton(
                Messages.getString("MainWindow.96")); //$NON-NLS-1$
        panel.add(localSearchRadioButton);

        globalSearchRadioButton = new JRadioButton(
                Messages.getString("MainWindow.97")); //$NON-NLS-1$
        panel.add(globalSearchRadioButton);

        JScrollPane searchResultScrollPane = new JScrollPane();
        searchResultScrollPane.setMinimumSize(new Dimension(0, 0));
        searchPanel.add(searchResultScrollPane, "2, 5, fill, fill"); //$NON-NLS-1$

        JList<Location> searchResultList = new JList<Location>(
                new DefaultListModel<Location>());
        searchResultScrollPane.setViewportView(searchResultList);

        userTagPanel = new JPanel();
        userTagPanel.setBorder(BorderFactory.createTitledBorder(Messages
                .getString("MainWindow.99"))); //$NON-NLS-1$
        placesTab.add(userTagPanel, "2, 4, fill, fill"); //$NON-NLS-1$
        userTagPanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"), //$NON-NLS-1$
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC, }, new RowSpec[] {
                FormFactory.NARROW_LINE_GAP_ROWSPEC,
                RowSpec.decode("default:grow"), //$NON-NLS-1$
                FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        JScrollPane userTagScrollPane = new JScrollPane();
        userTagScrollPane.setMinimumSize(new Dimension(0, 0));
        userTagPanel.add(userTagScrollPane, "2, 2, fill, fill"); //$NON-NLS-1$

        JList userTagList = new JList();
        userTagScrollPane.setViewportView(userTagList);

        overlayPanel = new JPanel();
        overlayPanel.setBorder(BorderFactory.createTitledBorder(Messages
                .getString("MainWindow.0"))); //$NON-NLS-1$
        placesTab.add(overlayPanel, "2, 6, fill, fill"); //$NON-NLS-1$
        overlayPanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"), //$NON-NLS-1$
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC, }, new RowSpec[] {
                FormFactory.NARROW_LINE_GAP_ROWSPEC,
                RowSpec.decode("default:grow"), //$NON-NLS-1$
                FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        JScrollPane overlayScrollPane = new JScrollPane();
        overlayScrollPane.setMinimumSize(new Dimension(0, 0));
        overlayPanel.add(overlayScrollPane, "2, 2, fill, fill"); //$NON-NLS-1$

        JList overlayList = new JList();
        overlayScrollPane.setViewportView(overlayList);

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

        antialiasingComboBox = new JComboBox<NamedItem<AntialiasingType>>();
        antialiasingComboBox.addItem(new NamedItem<AntialiasingType>(Messages
                .getString("MainWindow.128"), null)); //$NON-NLS-1$
        antialiasingComboBox.addItem(new NamedItem<AntialiasingType>(Messages
                .getString("MainWindow.129"), //$NON-NLS-1$
                AntialiasingType.MSAA_2));
        antialiasingComboBox.addItem(new NamedItem<AntialiasingType>(Messages
                .getString("MainWindow.130"), //$NON-NLS-1$
                AntialiasingType.MSAA_4));
        antialiasingComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    NamedItem<AntialiasingType> item = (NamedItem<AntialiasingType>) e
                            .getItem();
                    AntialiasingType type = item.getValue();
                    if (type == null) {
                        Settings.getInstance().putString(
                                SettingsContract.ANTIALIASING, null);
                    } else {
                        Settings.getInstance().putString(
                                SettingsContract.ANTIALIASING, type.name());
                    }
                }
            }
        });

        graphicsPanel.add(antialiasingComboBox, "4, 2, fill, default"); //$NON-NLS-1$

        texfilterLabel = new JLabel(Messages.getString("MainWindow.132")); //$NON-NLS-1$

        graphicsPanel.add(texfilterLabel, "2, 4, left, default"); //$NON-NLS-1$

        texfilterComboBox = new JComboBox<NamedItem<Boolean>>();
        texfilterComboBox.addItem(new NamedItem<Boolean>(Messages
                .getString("MainWindow.134"), new Boolean(false))); //$NON-NLS-1$
        texfilterComboBox.addItem(new NamedItem<Boolean>(Messages
                .getString("MainWindow.135"), new Boolean(true))); //$NON-NLS-1$
        texfilterComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    NamedItem<Boolean> item = (NamedItem<Boolean>) e.getItem();
                    Boolean valueBoolean = item.getValue();
                    Settings.getInstance().putBoolean(
                            SettingsContract.TEXTURE_FILTER, valueBoolean);
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
                            SettingsContract.LEVEL_OF_DETAILS, detail.name());
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
        memCacheSpinner.setModel(new SpinnerNumberModel(new Integer(100),
                new Integer(100), null, new Integer(1)));
        memCacheSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner spinner = (JSpinner) e.getSource();
                Settings.getInstance().putInteger(
                        SettingsContract.CACHE_SIZE_MEMORY,
                        (Integer) spinner.getValue());
            }
        });
        cachePanel.add(memCacheSpinner, "4, 2"); //$NON-NLS-1$

        fsCacheLabel = new JLabel(Messages.getString("MainWindow.149")); //$NON-NLS-1$
        cachePanel.add(fsCacheLabel, "2, 4"); //$NON-NLS-1$

        JSpinner fsCacheSpinner = new JSpinner();
        fsCacheSpinner.setModel(new SpinnerNumberModel(new Integer(100),
                new Integer(100), null, new Integer(1)));
        fsCacheSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner spinner = (JSpinner) e.getSource();
                Settings.getInstance().putInteger(
                        SettingsContract.CACHE_SIZE_FILESYSTEM,
                        (Integer) spinner.getValue());
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

            public void actionPerformed(ActionEvent arg0) {}
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

    private void initializeViewPanel() {
        viewPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"), //$NON-NLS-1$
                ColumnSpec.decode("center:20dlu"), }, new RowSpec[] { //$NON-NLS-1$
                RowSpec.decode("default:grow"), RowSpec.decode("1dlu"), //$NON-NLS-1$ //$NON-NLS-2$
                        RowSpec.decode("20dlu"), RowSpec.decode("1dlu"), })); //$NON-NLS-1$ //$NON-NLS-2$

        glCanvas = new GLCanvas();
        if (glCanvas == null) {
            System.err.println("Couldn't create Canvas!"); //$NON-NLS-1$
        }
        glCanvas.addGLEventListener(new GLEventListener() {

            @Override
            public void display(GLAutoDrawable arg0) {
                arg0.getGL().getGL2().glClear(GL2.GL_COLOR_BUFFER_BIT);
            }

            @Override
            public void dispose(GLAutoDrawable arg0) {

            }

            @Override
            public void init(GLAutoDrawable arg0) {}

            @Override
            public void reshape(GLAutoDrawable arg0, int arg1, int arg2,
                    int arg3, int arg4) {
                ((Component) arg0).setMinimumSize(new Dimension(0, 0));
            }

        });
        viewPanel.add(glCanvas, "1, 1, fill, fill"); //$NON-NLS-1$

        JPanel statusBar = new JPanel();
        viewPanel.add(statusBar, "1, 3, 2, 1, fill, fill"); //$NON-NLS-1$
        statusBar.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("50dlu"), //$NON-NLS-1$
                ColumnSpec.decode("default:grow"), ColumnSpec.decode("150dlu"), //$NON-NLS-1$ //$NON-NLS-2$
                ColumnSpec.decode("default:grow"), ColumnSpec.decode("70dlu"), //$NON-NLS-1$ //$NON-NLS-2$
                FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] { RowSpec
                .decode("default:grow"), })); //$NON-NLS-1$

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
        scalePanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec
                .decode("center:default:grow"), }, //$NON-NLS-1$
                new RowSpec[] { RowSpec.decode("default:grow"), //$NON-NLS-1$
                        RowSpec.decode("default:grow"), })); //$NON-NLS-1$

        JLabel scaleIcon = new JLabel(""); //$NON-NLS-1$
        scaleIcon.setIcon(loadIcon("icons/scale.png")); //$NON-NLS-1$
        scalePanel.add(scaleIcon, "1, 1"); //$NON-NLS-1$

        JLabel scaleLabel = new JLabel("1 km"); //$NON-NLS-1$
        scalePanel.add(scaleLabel, "1, 2"); //$NON-NLS-1$

        JPanel coordPanel = new JPanel();
        statusBar.add(coordPanel, "4, 1, fill, fill"); //$NON-NLS-1$
        coordPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"), ColumnSpec.decode("2dlu"), //$NON-NLS-1$ //$NON-NLS-2$
                ColumnSpec.decode("default:grow"), ColumnSpec.decode("5dlu"), //$NON-NLS-1$ //$NON-NLS-2$
                ColumnSpec.decode("default:grow"), ColumnSpec.decode("2dlu"), //$NON-NLS-1$ //$NON-NLS-2$
                ColumnSpec.decode("default:grow"), }, new RowSpec[] { RowSpec //$NON-NLS-1$
                .decode("default:grow"), })); //$NON-NLS-1$

        latitudeLabel = new JLabel(Messages.getString("MainWindow.2")); //$NON-NLS-1$
        coordPanel.add(latitudeLabel, "1, 1, right, default"); //$NON-NLS-1$

        latitudeTextField = new JTextField();
        coordPanel.add(latitudeTextField, "3, 1, fill, default"); //$NON-NLS-1$
        latitudeTextField.setColumns(10);

        longitudeLabel = new JLabel(Messages.getString("MainWindow.209")); //$NON-NLS-1$
        coordPanel.add(longitudeLabel, "5, 1, right, default"); //$NON-NLS-1$

        longitudeTextField = new JTextField();
        coordPanel.add(longitudeTextField, "7, 1, fill, default"); //$NON-NLS-1$
        longitudeTextField.setColumns(10);

        JProgressBar progressBar = new JProgressBar();
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
                System.err.println("Change Language!"); //$NON-NLS-1$
            }
        });
        displayModeComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (arg0.getStateChange() == ItemEvent.SELECTED) {
                    if (camera != null) {
                        IconizedItem<DisplayMode> selected = null;
                        if (displayModeComboBox.getSelectedItem() instanceof IconizedItem<?>)
                            selected = (IconizedItem<DisplayMode>) arg0.getItem();
                        if (selected != null) {
                            DisplayMode mode = selected.getValue();
                            mapOptionsPanel
                                    .setVisible(mode != DisplayMode.SOLAR_SYSTEM);
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
                        System.err.println(Messages.getString("MainWindow.51")); //$NON-NLS-1$
                    }
                }

            }
        });
        if (camera != null)
            camera.addCameraListener(new UICameraListener());
        this.addWindowListener(new UIWindowListener());
        glCanvas.addMouseWheelListener(new ZoomAdapter(zoomSlider, true));
        GlMouseListener l = new GlMouseListener();
        glCanvas.addMouseMotionListener(l);
        glCanvas.addMouseListener(l);
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
                paraMapTypeComboBox.removeAllItems();
                paraMapTypeComboBox
                .addItem(new IconizedItem<MapTypePair>(
                        Messages.getString("MainWindow.70"), //$NON-NLS-1$
                        loadIcon("icons/mapSatellite.png"), new MapTypePair(SingleMapType.SATELLITE))); //$NON-NLS-1$
        paraMapTypeComboBox
                .addItem(new IconizedItem<MapTypePair>(
                        Messages.getString("MainWindow.72"), //$NON-NLS-1$
                        loadIcon("icons/mapOSM.png"), new MapTypePair(TiledMapType.OSM_MAPNIK))); //$NON-NLS-1$
        paraMapTypeComboBox
        .addItem(new IconizedItem<MapTypePair>(
                Messages.getString("MainWindow.4"), //$NON-NLS-1$
                loadIcon("icons/mapOSM.png"), new MapTypePair(TiledMapType.CYCLING))); //$NON-NLS-1$
        paraMapTypeComboBox
        .addItem(new IconizedItem<MapTypePair>(
                Messages.getString("MainWindow.5"), //$NON-NLS-1$
                loadIcon("icons/mapOSM.png"), new MapTypePair(TiledMapType.HIKING))); //$NON-NLS-1$
        paraMapTypeComboBox
        .addItem(new IconizedItem<MapTypePair>(
                Messages.getString("MainWindow.6"), //$NON-NLS-1$
                loadIcon("icons/mapOSM.png"), new MapTypePair(TiledMapType.SKIING))); //$NON-NLS-1$
        paraMapTypeComboBox
        .addItem(new IconizedItem<MapTypePair>(
                Messages.getString("MainWindow.7"), //$NON-NLS-1$
                loadIcon("icons/mapOSM.png"), new MapTypePair(TiledMapType.OSM2WORLD))); //$NON-NLS-1$
        paraMapTypeComboBox
                .addItem(new IconizedItem<MapTypePair>(
                        Messages.getString("MainWindow.74"), //$NON-NLS-1$
                        loadIcon("icons/mapChildren.png"), new MapTypePair(SingleMapType.CHILDREN))); //$NON-NLS-1$
                paraMapTypeComboBox.setSelectedIndex(index);
                antialiasingLabel.setText(Messages.getString("MainWindow.126")); //$NON-NLS-1$
                index = antialiasingComboBox.getSelectedIndex();
                antialiasingComboBox.removeAllItems();
                antialiasingComboBox.addItem(new NamedItem<AntialiasingType>(
                        Messages.getString("MainWindow.128"), null)); //$NON-NLS-1$
                antialiasingComboBox.addItem(new NamedItem<AntialiasingType>(
                        Messages.getString("MainWindow.129"), //$NON-NLS-1$
                        AntialiasingType.MSAA_2));
                antialiasingComboBox.addItem(new NamedItem<AntialiasingType>(
                        Messages.getString("MainWindow.130"), //$NON-NLS-1$
                        AntialiasingType.MSAA_4));
                antialiasingComboBox.setSelectedIndex(index);
                texfilterLabel.setText(Messages.getString("MainWindow.132")); //$NON-NLS-1$
                index = texfilterComboBox.getSelectedIndex();
                texfilterComboBox.removeAllItems();
                texfilterComboBox.addItem(new NamedItem<Boolean>(Messages
                        .getString("MainWindow.134"), new Boolean(false))); //$NON-NLS-1$
                texfilterComboBox.addItem(new NamedItem<Boolean>(Messages
                        .getString("MainWindow.135"), new Boolean(true))); //$NON-NLS-1$
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
            }
        });

    }

    /**
     * Constructor.
     * 
     * @param locationManager The <code>LocationManager</code> associated with this window.
     */
    public MainWindow(final LocationManager locationManager) {
        this.locationManager = locationManager;
        String lang = Settings.getInstance().getString(
                SettingsContract.LANGUAGE);
        System.err.println("LangSetting At Start:"+lang);
        Locale.Builder builder = new Locale.Builder();
        builder.setLanguage(lang);
        Locale l = builder.build();
        if (l.getLanguage().equals(Locale.GERMAN.getLanguage())) {
            System.err.println("Set Lang to German at start!"); //$NON-NLS-1$
            Messages.setLocale(Locale.GERMAN);
        } else {
            System.err.println("Set Lang to English at start!"); //$NON-NLS-1$
            Messages.setLocale(Locale.ENGLISH);
        }
        initializeWindow();
        initializeViewTab();
        initializePlacesTab();
        initializeSettingsTab();
        initializeDetailsPanel();
        initializeViewPanel();
        loadLanguage();
        
        renderer = new Renderer(glCanvas, locationManager);
        camera = renderer.getCamera();
        
        if (l.getLanguage().equals(Locale.GERMAN.getLanguage())) {
            System.err.println("Set Lang to German at start!"); //$NON-NLS-1$
            languageComboBox.setSelectedIndex(1);
        } else {
            System.err.println("Set Lang to English at start!"); //$NON-NLS-1$
            languageComboBox.setSelectedIndex(0);
        }
        registerListeners();
    }

    /**
     * Gets the <code>GLCanvas</code> that is displayed in the left half of the window.
     * 
     * @return The GLCanvas used in this window
     */
    public final GLCanvas getGLCanvas() {
        return glCanvas;
    }


    private class UISettingsListener implements SettingsListener {

        @Override
        public void settingsChanged(String key, Object valOld, Object valNew) {}

    }

    private class UICameraListener implements CameraListener {

        @Override
        public void cameraViewChanged() {
           GeoCoordinates geo = camera.getGeoCoordinates(new ScreenCoordinates(0.5d, 0.5d));
           if (geo != null) {
               latitudeTextField.setText(geo.getLatitudeString());
               longitudeTextField.setText(geo.getLongitudeString());
           } else {
               latitudeTextField.setText("");
               longitudeTextField.setText("");
           }
           // TODO: Other sutuff like asking Nomination for Details as soon as it is implemented
        }

    }

    private class GlMouseListener extends MouseAdapter {

        double currentTiltX = 0.0d;
        double currentTiltY = 0.0d;
        private static final double SCALE_TILT = 0.001d;
        Point lastPos;


        @Override
        public void mouseDragged(MouseEvent e) {
            Point p = e.getPoint();
            if (SwingUtilities.isLeftMouseButton(e)) {
                GLCanvas gl = (GLCanvas) e.getSource();
                Dimension dimension = gl.getSize();
                double diffX = p.getX() - lastPos.getX();
                double diffY = p.getY() - lastPos.getY();
                // 0,0 - 1,1
                double xscreen = dimension.width / (p.getX() + diffX);
                double yscreen = dimension.height / (p.getY() + diffY);
                GeoCoordinates newpos = camera
                        .getGeoCoordinates(new ScreenCoordinates(xscreen,
                                yscreen));
                if (newpos != null)
                    camera.setPosition(newpos);
            } else if (SwingUtilities.isRightMouseButton(e)) {
                double diffX = p.getX() - lastPos.getX();
                double diffY = p.getY() - lastPos.getY();
                // -pi/2,pi/2
                currentTiltX += (diffX * SCALE_TILT);
                currentTiltY += (diffY * SCALE_TILT);
                if (currentTiltX < -(Math.PI / 2)) {
                    currentTiltX = -(Math.PI / 2);
                }
                if (currentTiltY < -(Math.PI / 2)) {
                    currentTiltY = -(Math.PI / 2);
                }
                if (currentTiltX > (Math.PI / 2)) {
                    currentTiltX = (Math.PI / 2);
                }
                if (currentTiltY > (Math.PI / 2)) {
                    currentTiltY = (Math.PI / 2);
                }
                camera.setTilt(currentTiltX, currentTiltY);
            }
            lastPos = p;
            super.mouseDragged(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            lastPos = e.getPoint();
            super.mousePressed(e);
        }
    }

    private class UILocationListener implements LocationListener {

        private JList<Location> list;


        public UILocationListener(JList<Location> list) {
            this.list = list;
        }

        @Override
        public void searchResultsAvailable(Collection<Location> results) {
            DefaultListModel<Location> model = (DefaultListModel<Location>) list
                    .getModel();
            model.clear();
            for (Location l : results) {
                model.addElement(l);
            }
        }

    }

    private class UISurfaceListener implements SurfaceListener {

        @Override
        public void surfaceChanged(double lonFrom, double latFrom,
                double lonTo, double latTo) {
            // TODO: Do I really care about this?

        }

    }

    private class ZoomAdapter extends MouseAdapter {

        private boolean increase;
        private JSlider slider;
        private final Double ZOOM_FACTOR = new Double(1.1d/100d);


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
            camera.setDistance((current)*ZOOM_FACTOR+0.1);
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int current = slider.getValue();
            int newCount =  current + e.getWheelRotation();
            if (newCount < slider.getMinimum())
                newCount = slider.getMinimum();
            else if (newCount > slider.getMaximum())
                newCount = slider.getMaximum();
            slider.setValue(newCount);
            camera.setDistance((newCount)*ZOOM_FACTOR+0.1);
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
        }

    }

    private class AddUsertagButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub

        }
    }

    private class RemoveUsertagButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub

        }
    }

    private class UIWindowListener extends WindowAdapter {

        @Override
        public void windowClosed(WindowEvent e) {
            SettingsContract.saveSettings();
            super.windowClosed(e);
        }
    }
}
