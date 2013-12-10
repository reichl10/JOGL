package de.joglearth.ui;

import static de.joglearth.util.Resource.loadIcon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Locale;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.BorderFactory;
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
import de.joglearth.geometry.PlaneGeometry;
import de.joglearth.geometry.SphereGeometry;
import de.joglearth.rendering.AntialiasingType;
import de.joglearth.rendering.DisplayMode;
import de.joglearth.rendering.LevelOfDetail;
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
            MainWindow dialog = new MainWindow(null, null);
            dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static ImageIcon hideIcon = loadIcon("icons/hide.png");
    private static ImageIcon showIcon = loadIcon("icons/show.png");
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
    private JTextField latitudeTextField;
    private JTextField longitudeTextField;
    private JTextField textField;
    private JLabel sidebarHideIconLabel;
    private JPanel sideBarHideLinePanel;
    private JPanel mapOptionsPanel;
    private JComboBox<?> mapTypeComboBox;
    private JComboBox<IconizedItem<DisplayMode>> displayModeComboBox;
    private JPanel viewTab, placesTab, settingsTab, detailsPanel,
            viewPanel;


    private class HideSideBarListener extends MouseAdapter {

        boolean visible = true;


        @Override
        public void mouseClicked(MouseEvent e) {
            visible = !visible;
            ((FormLayout) getContentPane().getLayout()).setColumnSpec(1,
                    ColumnSpec.decode(visible ? "130dlu" : "0dlu"));
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
        setBackground(UIManager.getColor("inactiveCaption"));
        getContentPane().setBackground(UIManager.getColor("inactiveCaption"));
        setTitle(JoglEarth.PRODUCT_NAME);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        this.viewEventListener = new ViewEventListener(camera);
        getContentPane().setLayout(
                new FormLayout(new ColumnSpec[] {
                        ColumnSpec.decode("130dlu"),
                        ColumnSpec.decode("15px"),
                        ColumnSpec.decode("default:grow"), },
                        new RowSpec[] {
                                RowSpec.decode("default:grow"), }));

        JPanel sideBar = new JPanel();
        getContentPane().add(sideBar, "1, 1, left, fill");
        sideBar.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"), },
                new RowSpec[] {
                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                        RowSpec.decode("top:default:grow"),
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("80dlu"),
                        FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        JTabbedPane sideBarTabs = new JTabbedPane(JTabbedPane.TOP);
        sideBar.add(sideBarTabs, "1, 2, fill, fill");
        sideBarTabs.setBackground(UIManager.getColor("menu"));

        viewTab = new JPanel();
        sideBarTabs.addTab("View", loadIcon("icons/view.png"), viewTab,
                null);
        sideBarTabs.setEnabledAt(0, true);
        placesTab = new JPanel();
        sideBarTabs.addTab("Places", loadIcon("icons/places.png"),
                placesTab, null);
        sideBarTabs.setEnabledAt(1, true);
        settingsTab = new JPanel();
        sideBarTabs.addTab("Settings", loadIcon("icons/settings.png"),
                settingsTab, null);
        sideBarTabs.setEnabledAt(2, true);
        detailsPanel = new JPanel();
        sideBar.add(detailsPanel, "1, 4, fill, fill");

        JPanel sideBarHidePanel = new JPanel();
        sideBarHidePanel.addMouseListener(new HideSideBarListener());
        getContentPane().add(sideBarHidePanel, "2, 1, fill, fill");
        sideBarHidePanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("5px"),
                ColumnSpec.decode("default:grow"), },
                new RowSpec[] {
                        RowSpec.decode("4dlu:grow"),
                        RowSpec.decode("0dlu"), }));

        sideBarHideLinePanel = new JPanel();
        sideBarHideLinePanel.setBackground(Color.LIGHT_GRAY);
        sideBarHidePanel.add(sideBarHideLinePanel, "2, 1, fill, fill");
        sideBarHideLinePanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"), },
                new RowSpec[] {
                        RowSpec.decode("default:grow"), }));

        sidebarHideIconLabel = new JLabel("");
        sidebarHideIconLabel.setIcon(hideIcon);
        sideBarHideLinePanel.add(sidebarHideIconLabel, "1, 1");

        viewPanel = new JPanel();
        getContentPane().add(viewPanel, "3, 1, fill, fill");
    }

    private void initializeDetailsPanel() {
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Details"));
        detailsPanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC, },
                new RowSpec[] {
                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("default:grow"),
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        JLabel detailNameLabel = new JLabel("Unknown location");
        detailsPanel.add(detailNameLabel, "2, 2");

        JButton userTagButton = new JButton("Add user tag");
        userTagButton.setHorizontalAlignment(SwingConstants.LEFT);
        userTagButton.setIcon(loadIcon("icons/addTag.png"));
        userTagButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {}
        });

        JLabel detailDescriptionLabel = new JLabel("No description available.");
        detailsPanel.add(detailDescriptionLabel, "2, 4, default, top");
        detailsPanel.add(userTagButton, "2, 6");
    }

    private void initializeViewTab() {
        viewTab.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormFactory.RELATED_GAP_COLSPEC, },
                new RowSpec[] {
                        RowSpec.decode("6dlu"),
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("bottom:default:grow"), }));

        JLabel displayModeLabel = new JLabel("Display mode:");
        viewTab.add(displayModeLabel, "2, 2");
        mapOptionsPanel = new JPanel();

        displayModeComboBox = new JComboBox<IconizedItem<DisplayMode>>();
        displayModeComboBox.setRenderer(new IconListCellRenderer<IconizedItem<DisplayMode>>());
        displayModeComboBox.addActionListener(new ActionListener() {

            @SuppressWarnings("unchecked")
            public void actionPerformed(ActionEvent arg0) {
                IconizedItem<DisplayMode> selected = null;
                if (displayModeComboBox.getSelectedItem() instanceof IconizedItem<?>)
                    selected = ((IconizedItem<DisplayMode>) displayModeComboBox.getSelectedItem());
                if (selected != null) {
                    DisplayMode mode = selected.getValue();
                    mapOptionsPanel.setVisible(mode != DisplayMode.SOLAR_SYSTEM);
                    switch (mode) {
                        case SOLAR_SYSTEM:
                        case GLOBE_MAP:
                            camera.setGeometry(new SphereGeometry());
                            break;
                        case PLANE_MAP:
                            camera.setGeometry(new PlaneGeometry());
                            break;
                        default:
                            // TODO: prob remove this line
                            System.err.println("Unknown DisplayMode in comboBox!");
                            break;
                    }
                }
            }
        });
        displayModeComboBox.addItem(new IconizedItem<DisplayMode>("Solar System",
                loadIcon("icons/modeSolar.png"), DisplayMode.SOLAR_SYSTEM));
        displayModeComboBox.addItem(new IconizedItem<DisplayMode>("Globe Map",
                loadIcon("icons/modeGlobe.png"), DisplayMode.GLOBE_MAP));
        displayModeComboBox.addItem(new IconizedItem<DisplayMode>("Plane Map",
                loadIcon("icons/modePlane.png"), DisplayMode.PLANE_MAP));
        viewTab.add(displayModeComboBox, "2, 4, fill, default");

        mapOptionsPanel.setBorder(null);
        viewTab.add(mapOptionsPanel, "2, 6, fill, fill");
        mapOptionsPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"), },
                new RowSpec[] {
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        RowSpec.decode("8dlu"),
                        FormFactory.DEFAULT_ROWSPEC, }));

        JLabel mapTypeLabel = new JLabel("Map type:");
        mapOptionsPanel.add(mapTypeLabel, "1, 1");

        JComboBox<IconizedItem<MapTypePair>> paraMapTypeComboBox = new JComboBox<IconizedItem<MapTypePair>>();
        mapTypeComboBox = paraMapTypeComboBox;
        mapOptionsPanel.add(paraMapTypeComboBox, "1, 3");
        paraMapTypeComboBox.setRenderer(new IconListCellRenderer<IconizedItem<MapTypePair>>());
        paraMapTypeComboBox.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<IconizedItem<MapTypePair>> comboBox = (JComboBox<IconizedItem<MapTypePair>>) e.getSource();
                MapTypePair mtp = ((IconizedItem<MapTypePair>)comboBox.getSelectedItem()).getValue();
                if (mtp.type instanceof SingleMapType) {
                    SingleMapType mapType = (SingleMapType) mtp.type;
                    Settings.getInstance().putString(SettingsContract.MAP_TYPE, mapType.name());
                } else if (mtp.type instanceof TiledMapType) {
                    TiledMapType type = (TiledMapType) mtp.type;
                    Settings.getInstance().putString(SettingsContract.MAP_TYPE, type.name());
                }
            }
        });
        JCheckBox heightMapCheckBox = new JCheckBox("Enable height map");
        heightMapCheckBox.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox box = (JCheckBox) e.getSource();
                Settings.getInstance().putBoolean(SettingsContract.HEIGHT_MAP_ENABLED, new Boolean(box.isSelected()));
            }
        });
        mapOptionsPanel.add(heightMapCheckBox, "1, 5");

        JLabel logoLabel = new JLabel("");
        viewTab.add(logoLabel, "2, 8, center, bottom");
        logoLabel.setVerticalAlignment(SwingConstants.TOP);
        logoLabel.setIcon(loadIcon("icons/logo.png"));
        paraMapTypeComboBox.addItem(new IconizedItem<MapTypePair>("Satellite",
                loadIcon("icons/mapSatellite.png"), new MapTypePair(SingleMapType.SATELLITE)));
        paraMapTypeComboBox.addItem(new IconizedItem<MapTypePair>("OpenStreetMap",
                loadIcon("icons/mapOSM.png"), new MapTypePair(TiledMapType.OSM_MAPNIK)));
        paraMapTypeComboBox.addItem(new IconizedItem<MapTypePair>("Children's Map",
                loadIcon("icons/mapChildren.png"), new MapTypePair(SingleMapType.CHILDREN)));
    }

    private void initializePlacesTab() {
        placesTab.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("2dlu"),
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("2dlu"), },
                new RowSpec[] {
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("max(60dlu;default):grow"),
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("default:grow"),
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("default:grow"),
                        FormFactory.RELATED_GAP_ROWSPEC, }));

        JPanel searchPanel = new JPanel();
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        placesTab.add(searchPanel, "2, 2, fill, fill");
        searchPanel
                .setLayout(new FormLayout(new ColumnSpec[] {
                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                        ColumnSpec.decode("default:grow"),
                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC, },
                        new RowSpec[] {
                                FormFactory.NARROW_LINE_GAP_ROWSPEC,
                                RowSpec.decode("15dlu"),
                                RowSpec.decode("12dlu"),
                                FormFactory.RELATED_GAP_ROWSPEC,
                                RowSpec.decode("default:grow"),
                                FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        JPanel searchQueryPanel = new JPanel();
        searchPanel.add(searchQueryPanel, "2, 2, fill, top");
        searchQueryPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"), ColumnSpec.decode("2dlu"),
                ColumnSpec.decode("20dlu"), }, new RowSpec[] {
                FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

        textField = new JTextField();
        searchQueryPanel.add(textField, "1, 1, fill, fill");

        JButton searchButton = new JButton(loadIcon("icons/search.png"));
        searchQueryPanel.add(searchButton, "3, 1, fill, fill");

        JPanel panel = new JPanel();
        searchPanel.add(panel, "2, 3, fill, fill");
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));

        JRadioButton localSearchRadioButton = new JRadioButton("Nearby");
        panel.add(localSearchRadioButton);

        JRadioButton globalSearchRadioButton = new JRadioButton("Global");
        panel.add(globalSearchRadioButton);

        JScrollPane searchResultScrollPane = new JScrollPane();
        searchResultScrollPane.setMinimumSize(new Dimension(0, 0));
        searchPanel.add(searchResultScrollPane, "2, 5, fill, fill");

        JList searchResultList = new JList();
        searchResultScrollPane.setViewportView(searchResultList);

        JPanel userTagPanel = new JPanel();
        userTagPanel.setBorder(BorderFactory.createTitledBorder("User Tags"));
        placesTab.add(userTagPanel, "2, 4, fill, fill");
        userTagPanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC, },
                new RowSpec[] {
                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                        RowSpec.decode("default:grow"),
                        FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        JScrollPane userTagScrollPane = new JScrollPane();
        userTagScrollPane.setMinimumSize(new Dimension(0, 0));
        userTagPanel.add(userTagScrollPane, "2, 2, fill, fill");

        JList userTagList = new JList();
        userTagScrollPane.setViewportView(userTagList);

        JPanel overlayPanel = new JPanel();
        overlayPanel.setBorder(BorderFactory.createTitledBorder("Overlays"));
        placesTab.add(overlayPanel, "2, 6, fill, fill");
        overlayPanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC, },
                new RowSpec[] {
                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                        RowSpec.decode("default:grow"),
                        FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        JScrollPane overlayScrollPane = new JScrollPane();
        overlayScrollPane.setMinimumSize(new Dimension(0, 0));
        overlayPanel.add(overlayScrollPane, "2, 2, fill, fill");

        JList overlayList = new JList();
        overlayScrollPane.setViewportView(overlayList);

    }

    private void initializeSettingsTab() {
        settingsTab.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormFactory.RELATED_GAP_COLSPEC, },
                new RowSpec[] {
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC, }));

        JPanel languagePanel = new JPanel();
        languagePanel.setBorder(new TitledBorder(null, "Language", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        settingsTab.add(languagePanel, "2, 2");
        languagePanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC, },
                new RowSpec[] {
                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                        RowSpec.decode("default:grow"),
                        FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        JComboBox<IconizedItem<String>> languageComboBox = new JComboBox<IconizedItem<String>>();
        languageComboBox.setRenderer(new IconListCellRenderer<IconizedItem<String>>());
        languageComboBox.addItem(new IconizedItem<String>("English", loadIcon("icons/flagEng.png"),
                "en"));
        languageComboBox.addItem(new IconizedItem<String>("Deutsch", loadIcon("icons/flagGer.png"),
                "de"));
        languagePanel.add(languageComboBox, "2, 2, fill, top");
        languageComboBox.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<IconizedItem<String>> cBox = (JComboBox<IconizedItem<String>>) e.getSource();
                IconizedItem<String> item = (IconizedItem<String>) cBox.getSelectedItem();
                String language = item.getValue();
                Locale theLang;
                if (language.equals("Deutsch")) {
                    theLang = Locale.GERMAN;
                } else {
                    theLang = Locale.ENGLISH;
                }
                Settings.getInstance().putString(SettingsContract.LANGUAGE, theLang.getLanguage());
                // TODO: Update Userinterface
            }
        });
        JPanel graphicsPanel = new JPanel();
        graphicsPanel.setBorder(BorderFactory.createTitledBorder("Graphics settings"));
        settingsTab.add(graphicsPanel, "2, 4");
        graphicsPanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("left:default"),
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC, },
                new RowSpec[] {
                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        JLabel antialiasingLabel = new JLabel("Antialiasing:");
        graphicsPanel.add(antialiasingLabel, "2, 2, left, default");

        JComboBox<NamedItem<AntialiasingType>> antialiasingComboBox = new JComboBox<NamedItem<AntialiasingType>>();
        antialiasingComboBox.addItem(new NamedItem<AntialiasingType>("Off", null));
        antialiasingComboBox.addItem(new NamedItem<AntialiasingType>("2x MSAA", AntialiasingType.MSAA_2));
        antialiasingComboBox.addItem(new NamedItem<AntialiasingType>("4x MSAA", AntialiasingType.MSAA_4));
        antialiasingComboBox.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<NamedItem<AntialiasingType>> comboBox = (JComboBox<NamedItem<AntialiasingType>>) e.getSource();
                NamedItem<AntialiasingType> item = (NamedItem<AntialiasingType>) comboBox.getSelectedItem();
                AntialiasingType type = item.getValue();
                if (type == null) {
                    Settings.getInstance().putString(SettingsContract.ANTIALIASING, null);
                } else {
                    Settings.getInstance().putString(SettingsContract.ANTIALIASING, type.name());
                }
            }
        });
        graphicsPanel.add(antialiasingComboBox, "4, 2, fill, default");

        JLabel texfilterLabel = new JLabel("Texture Filter:");
        graphicsPanel.add(texfilterLabel, "2, 4, left, default");

        JComboBox<NamedItem<Boolean>> texfilterComboBox = new JComboBox<NamedItem<Boolean>>();
        texfilterComboBox.addItem(new NamedItem<Boolean>("Off", new Boolean(false)));
        texfilterComboBox.addItem(new NamedItem<Boolean>("On", new Boolean(true)));
        texfilterComboBox.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<NamedItem<Boolean>> comboBox = (JComboBox<NamedItem<Boolean>>) e.getSource();
                NamedItem<Boolean> item = (NamedItem<Boolean>) comboBox.getSelectedItem();
                Boolean valueBoolean = item.getValue();
                Settings.getInstance().putBoolean(SettingsContract.TEXTURE_FILTER, valueBoolean);
            }
        });
        graphicsPanel.add(texfilterComboBox, "4, 4, fill, default");

        JLabel lodLabel = new JLabel("Level of Detail:");
        graphicsPanel.add(lodLabel, "2, 6, left, default");

        JComboBox<NamedItem<LevelOfDetail>> lodComboBox = new JComboBox<NamedItem<LevelOfDetail>>();
        lodComboBox.addItem(new NamedItem<LevelOfDetail>("Low", LevelOfDetail.LOW));
        lodComboBox.addItem(new NamedItem<LevelOfDetail>("Medium", LevelOfDetail.MEDIUM));
        lodComboBox.addItem(new NamedItem<LevelOfDetail>("High", LevelOfDetail.HIGH));
        lodComboBox.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<NamedItem<LevelOfDetail>> lodComboBox = (JComboBox<NamedItem<LevelOfDetail>>) e.getSource();
                LevelOfDetail detail = ((NamedItem<LevelOfDetail>) lodComboBox.getSelectedItem()).getValue();
                Settings.getInstance().putString(SettingsContract.LEVEL_OF_DETAILS, detail.name());
            }
        });
        graphicsPanel.add(lodComboBox, "4, 6, fill, default");

        JPanel cachePanel = new JPanel();
        cachePanel.setBorder(new TitledBorder(null, "Cache size", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        settingsTab.add(cachePanel, "2, 6, fill, top");
        cachePanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"), },
                new RowSpec[] {
                        FormFactory.NARROW_LINE_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

        JLabel memCacheLabel = new JLabel("Memory");
        cachePanel.add(memCacheLabel, "2, 2");

        JSpinner memCacheSpinner = new JSpinner();
        memCacheSpinner.setModel(new SpinnerNumberModel(new Integer(100), new Integer(100), null, new Integer(1)));
        memCacheSpinner.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner spinner = (JSpinner) e.getSource();
                Settings.getInstance().putInteger(SettingsContract.CACHE_SIZE_MEMORY, (Integer)spinner.getValue());
            }
        });
        cachePanel.add(memCacheSpinner, "4, 2");

        JLabel fsCacheLabel = new JLabel("File Sytem");
        cachePanel.add(fsCacheLabel, "2, 4");

        JSpinner fsCacheSpinner = new JSpinner();
        fsCacheSpinner.setModel(new SpinnerNumberModel(new Integer(100), new Integer(100), null, new Integer(1)));
        fsCacheSpinner.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner spinner = (JSpinner) e.getSource();
                Settings.getInstance().putInteger(SettingsContract.CACHE_SIZE_FILESYSTEM, (Integer)spinner.getValue());
            }
        });
        cachePanel.add(fsCacheSpinner, "4, 4");

        JPanel manualAboutPanel = new JPanel();
        settingsTab.add(manualAboutPanel, "2, 8, fill, bottom");
        manualAboutPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("5dlu"),
                ColumnSpec.decode("default:grow"), },
                new RowSpec[] {
                        FormFactory.DEFAULT_ROWSPEC, }));

        JButton manualButton = new JButton("Manual");
        manualAboutPanel.add(manualButton, "1, 1");
        manualButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {}
        });
        manualButton.setIcon(loadIcon("icons/manual.png"));

        JButton aboutButton = new JButton("About");
        aboutButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                AboutBox aboutBox = new AboutBox();
                aboutBox.setVisible(true);
            }
        });
        manualAboutPanel.add(aboutButton, "3, 1");
        aboutButton.setIcon(loadIcon("icons/info.png"));
    }

    private void initializeViewPanel() {
        viewPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"),
                ColumnSpec.decode("center:20dlu"), }, new RowSpec[] {
                RowSpec.decode("default:grow"), RowSpec.decode("1dlu"),
                RowSpec.decode("20dlu"), RowSpec.decode("1dlu"), }));

        glCanvas = new GLCanvas();
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
        viewPanel.add(glCanvas, "1, 1, fill, fill");

        JPanel statusBar = new JPanel();
        viewPanel.add(statusBar, "1, 3, 2, 1, fill, fill");
        statusBar.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("50dlu"),
                ColumnSpec.decode("default:grow"), ColumnSpec.decode("150dlu"),
                ColumnSpec.decode("default:grow"), ColumnSpec.decode("70dlu"),
                FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] { RowSpec
                .decode("default:grow"), }));

        JPanel zoomPanel = new JPanel();
        viewPanel.add(zoomPanel, "2, 1, center, fill");
        zoomPanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec
                .decode("center:default"), }, new RowSpec[] {
                FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"),
                FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC, }));

        JLabel zoomPlusLabel = new JLabel("");
        zoomPlusLabel.setIcon(loadIcon("icons/zoomPlus.png"));
        zoomPanel.add(zoomPlusLabel, "1, 2");

        JSlider zoomSlider = new JSlider();
        zoomSlider.setMajorTickSpacing(1);
        zoomSlider.setOrientation(SwingConstants.VERTICAL);
        zoomPanel.add(zoomSlider, "1, 4, default, fill");

        JLabel zoomMinusLabel = new JLabel("");
        zoomMinusLabel.setIcon(loadIcon("icons/zoomMinus.png"));
        zoomPanel.add(zoomMinusLabel, "1, 6");

        JLabel zoomLevelLabel = new JLabel("0");
        zoomPanel.add(zoomLevelLabel, "1, 8");

        JPanel scalePanel = new JPanel();
        statusBar.add(scalePanel, "2, 1, fill, fill");
        scalePanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec
                .decode("center:default:grow"), },
                new RowSpec[] { RowSpec.decode("default:grow"),
                        RowSpec.decode("default:grow"), }));

        JLabel scaleIcon = new JLabel("");
        scaleIcon.setIcon(loadIcon("icons/scale.png"));
        scalePanel.add(scaleIcon, "1, 1");

        JLabel scaleLabel = new JLabel("1 km");
        scalePanel.add(scaleLabel, "1, 2");

        JPanel coordPanel = new JPanel();
        statusBar.add(coordPanel, "4, 1, fill, fill");
        coordPanel.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("default:grow"), ColumnSpec.decode("2dlu"),
                ColumnSpec.decode("default:grow"), ColumnSpec.decode("5dlu"),
                ColumnSpec.decode("default:grow"), ColumnSpec.decode("2dlu"),
                ColumnSpec.decode("default:grow"), }, new RowSpec[] { RowSpec
                .decode("default:grow"), }));

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
        statusBar.add(progressBar, "6, 1");
        progressBar.setStringPainted(true);
        progressBar.setValue(100);
    }

    /**
     * Constructor.
     * 
     * @param locationManager The <code>LocationManager</code> associated with this window.
     * @param camera The <code>Camera</code> used by this window
     */
    public MainWindow(final LocationManager locationManager, final Camera camera) {
        this.camera = camera;
        this.locationManager = locationManager;
        initializeWindow();
        initializeViewTab();
        initializePlacesTab();
        initializeSettingsTab();
        initializeDetailsPanel();
        initializeViewPanel();
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
        public void settingsChanged(String key, Object valOld, Object valNew) {
            // TODO Automatisch generierter Methodenstub

        }

    }

    private class UICameraListener implements CameraListener {

        @Override
        public void cameraViewChanged() {
            // TODO Automatisch generierter Methodenstub

        }

    }

    private class UILocationListener implements LocationListener {

        @Override
        public void searchResultsAvailable(Collection<Location> results) {
            // TODO Automatisch generierter Methodenstub

        }

    }

    private class UISurfaceListener implements SurfaceListener {

        @Override
        public void surfaceChanged(double lonFrom, double latFrom, double lonTo, double latTo) {
            // TODO Automatisch generierter Methodenstub

        }

    }
}
