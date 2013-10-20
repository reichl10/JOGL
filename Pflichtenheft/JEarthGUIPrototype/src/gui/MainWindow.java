package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractListModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.MatteBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * MainUI.
 */
public class MainWindow implements ActionListener {

	/**
	 * Icon to use for the default point.
	 */
	private static final Icon POINT_DEFAULT = getRescaledIcon(
			"/gui/icons/point_default.png", 24);
	/**
	 * Icon to use as symbol for removing points.
	 */
	private static final Icon POINT_REMOVE = getRescaledIcon(
			"/gui/icons/point_remove.png", 24);
	/**
	 * Default Icon for Zoom In Button.
	 */
	private static final Icon ZOOM_IN = getRescaledIcon(
			"/gui/icons/zoomin.png", 48);
	/**
	 * Default Icon for Zoom Out Button.
	 */
	private static final Icon ZOOM_OUT = getRescaledIcon(
			"/gui/icons/zoomout.png", 48);
	/**
	 * Actioncommand for changing point button to remove.
	 */
	private static final String AC_SWITCHTO_POINT_REMOVE = "remove";
	/**
	 * Actioncommand for changing point button to add.
	 */
	private static final String AC_SWITCHTO_POINT_ADD = "add";
	/**
	 * Actioncommand for Zooming In.
	 */
	private static final String AC_ZOOM_IN = "zoomin";
	/**
	 * Actioncommand for Zooming Out.
	 */
	private static final String AC_ZOOM_OUT = "zoomout";

	/**
	 * The main JFrame.
	 */
	private JFrame frmJogleearth;
	private JButton btnPointOperation;
	private JTextField textFieldLongitudeMap;
	private JTextField textFieldLatitudeMap;
	private JSlider sliderZoom;
	private JComboBox comboBoxViewmode;
	private JComboBox comboBoxMaptype;
	private JTextField textField;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 *            Command line arguments
	 */
	public static void main(final String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		} catch (InstantiationException e2) {
			e2.printStackTrace();
		} catch (IllegalAccessException e2) {
			e2.printStackTrace();
		} catch (UnsupportedLookAndFeelException e2) {
			e2.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmJogleearth.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
		addCustomRenderer();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmJogleearth = new JFrame();
		frmJogleearth.setTitle("JoglEearth");
		frmJogleearth.setBounds(100, 100, 1024, 768);
		frmJogleearth.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		frmJogleearth.getContentPane().add(splitPane, BorderLayout.CENTER);

		JPanel panelLeft = new JPanel();
		panelLeft.setMinimumSize(new Dimension(300, 10));
		splitPane.setRightComponent(panelLeft);
		panelLeft.setLayout(new BorderLayout(0, 0));

		JPanel panelBottom = new JPanel();
		panelLeft.add(panelBottom, BorderLayout.SOUTH);
		panelBottom.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("left:default:grow"),
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("42px"),}));

		JProgressBar progressBarLoad = new JProgressBar();
		progressBarLoad.setStringPainted(true);
		progressBarLoad.setValue(50);
		panelBottom.add(progressBarLoad, "1, 1, fill, center");

		JLabel lblLongitudeMap = new JLabel("Längengrad:");
		panelBottom.add(lblLongitudeMap, "3, 1, left, center");

		textFieldLongitudeMap = new JTextField();
		panelBottom.add(textFieldLongitudeMap, "4, 1, center, center");
		textFieldLongitudeMap.setColumns(10);

		JLabel lblLatitudeMap = new JLabel("Breitengrad:");
		panelBottom.add(lblLatitudeMap, "6, 1, left, center");

		textFieldLatitudeMap = new JTextField();
		panelBottom.add(textFieldLatitudeMap, "7, 1, center, center");
		textFieldLatitudeMap.setColumns(10);

		JLabel lblScale = new JLabel("");
		panelBottom.add(lblScale, "9, 1");
		lblScale.setIcon(new ImageIcon(MainWindow.class
				.getResource("/gui/icons/scale.png")));

		JPanel panelZoom = new JPanel();
		panelLeft.add(panelZoom, BorderLayout.EAST);
		panelZoom.setLayout(new BorderLayout(0, 0));

		sliderZoom = new JSlider();
		sliderZoom.setMajorTickSpacing(1);
		sliderZoom.setOpaque(false);
		sliderZoom.setPaintLabels(true);
		sliderZoom.setPaintTicks(true);
		sliderZoom.setSnapToTicks(true);
		sliderZoom.setValue(10);
		sliderZoom.setMinimumSize(new Dimension(50, 23));
		sliderZoom.setMaximum(20);
		sliderZoom.setMinimum(1);
		sliderZoom.setOrientation(SwingConstants.VERTICAL);
		panelZoom.add(sliderZoom, BorderLayout.CENTER);

		JButton btnZoomIn = new JButton("");
		btnZoomIn.setBorderPainted(false);
		btnZoomIn.setIconTextGap(0);
		btnZoomIn.setContentAreaFilled(false);
		btnZoomIn.setIcon(ZOOM_IN);
		btnZoomIn.setActionCommand(AC_ZOOM_IN);
		btnZoomIn.addActionListener(this);
		panelZoom.add(btnZoomIn, BorderLayout.NORTH);

		JButton btnZoomOut = new JButton("");
		btnZoomOut.setIconTextGap(0);
		btnZoomOut.setContentAreaFilled(false);
		btnZoomOut.setBorderPainted(false);
		btnZoomOut.setIcon(ZOOM_OUT);
		btnZoomOut.setActionCommand(AC_ZOOM_OUT);
		btnZoomOut.addActionListener(this);
		panelZoom.add(btnZoomOut, BorderLayout.SOUTH);

		JPanel panelMenu = new JPanel();
		panelMenu.setPreferredSize(new Dimension(250, 10));
		panelMenu.setMinimumSize(new Dimension(250, 10));
		splitPane.setLeftComponent(panelMenu);
		panelMenu.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		panelMenu.add(tabbedPane);

		JPanel panelView = new JPanel();
		tabbedPane.addTab("Ansicht", null, panelView, null);
		panelView
				.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));

		JLabel lblViewmode = new JLabel("Ansichtsmodus:");
		panelView.add(lblViewmode, "2, 2, fill, default");

		comboBoxViewmode = new JComboBox();
		comboBoxViewmode.setModel(new DefaultComboBoxModel(
				new IconizedNamedItem[] {
						new IconizedNamedItem("Sonnensystem", ZOOM_IN),
						new IconizedNamedItem("Karte", ZOOM_OUT) }));
		panelView.add(comboBoxViewmode, "2, 4, fill, default");
		
				JLabel lblMaptype = new JLabel("Karte:");
				panelView.add(lblMaptype, "2, 6, fill, default");
		
				comboBoxMaptype = new JComboBox();
				comboBoxMaptype.setModel(new DefaultComboBoxModel(new IconizedNamedItem[] {
						new IconizedNamedItem("Satellitenbilder", POINT_DEFAULT),
						new IconizedNamedItem("OpenStreetMap", POINT_REMOVE)
				}));
				panelView.add(comboBoxMaptype, "2, 8, fill, default");
		
				JCheckBox chckbxHeightprofile = new JCheckBox("Höhenprofil");
				panelView.add(chckbxHeightprofile, "2, 10, fill, default");
		
				JCheckBox chckbxThreeDModel = new JCheckBox("3D-Modelle");
				chckbxThreeDModel.setSelected(true);
				panelView.add(chckbxThreeDModel, "2, 12, fill, default");

		JPanel panelSettings = new JPanel();
		tabbedPane.addTab("Einstellungen", null, panelSettings, null);
		panelSettings.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));

		JLabel lblSprache = new JLabel("Sprache:");
		panelSettings.add(lblSprache, "2, 2, 2, 1");

		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Deutsch", "English"}));
		panelSettings.add(comboBox, "2, 4, 3, 1, fill, default");
		
		JLabel lblGrafikeinstellungen = new JLabel("Grafikeinstellungen:");
		panelSettings.add(lblGrafikeinstellungen, "2, 6, 3, 1");
		
		JLabel lblAntialiasing = new JLabel("Antialiasing:");
		panelSettings.add(lblAntialiasing, "3, 8, left, default");
		
		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setModel(new DefaultComboBoxModel(new String[] {"Aus"}));
		panelSettings.add(comboBox_2, "4, 8, fill, default");
		
		JLabel lblTexturfilter = new JLabel("Texturfilter:");
		panelSettings.add(lblTexturfilter, "3, 10, left, default");
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"Aus"}));
		panelSettings.add(comboBox_1, "4, 10, fill, default");

		JLabel lblKartenserver = new JLabel("Kartenserver:");
		panelSettings.add(lblKartenserver, "2, 12, 3, 1");

		JScrollPane scrollPane = new JScrollPane();
		panelSettings.add(scrollPane, "2, 14, 3, 1, fill, fill");

		JPanel panelMapServer = new JPanel();
		scrollPane.setViewportView(panelMapServer);
		panelMapServer
				.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec
						.decode("default:grow"), }, new RowSpec[] {
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, }));

		JCheckBox chckbxOpenStreetMapServer = new JCheckBox(
				"OpenStreetMap Standard");
		chckbxOpenStreetMapServer.setSelected(true);
		panelMapServer.add(chckbxOpenStreetMapServer, "1, 2");

		JCheckBox chckbxNasaServer = new JCheckBox("NASA Default");
		panelMapServer.add(chckbxNasaServer, "1, 4");
				
						JButton btnAddServer = new JButton("Hinzufügen");
						panelSettings.add(btnAddServer, "2, 17, 2, 1");
		
				JButton btnRemoveServer = new JButton("Entfernen");
				panelSettings.add(btnRemoveServer, "4, 17");

		JPanel panelCache = new JPanel();
		panelSettings.add(panelCache, "2, 19, 3, 1, fill, fill");
		panelCache.setLayout(new FormLayout(
				new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"),
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] {
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, }));

		JLabel lblCacheSize = new JLabel("Zwischenspeichergröße:");
		panelCache.add(lblCacheSize, "1, 2, 6, 1");

		JLabel lblRamCache = new JLabel("Arbeitsspeicher:");
		panelCache.add(lblRamCache, "2, 4");

		JSpinner spinnerRam = new JSpinner();
		spinnerRam.setModel(new SpinnerNumberModel(new Integer(50), null, null,
				new Integer(1)));
		panelCache.add(spinnerRam, "4, 4");

		JLabel lblRamUnit = new JLabel("MiB");
		panelCache.add(lblRamUnit, "6, 4");

		JLabel lblFilesystemCache = new JLabel("Dateisystem:");
		panelCache.add(lblFilesystemCache, "2, 6");

		JSpinner spinnerFilesystem = new JSpinner();
		spinnerFilesystem.setModel(new SpinnerNumberModel(new Integer(200),
				null, null, new Integer(1)));
		panelCache.add(spinnerFilesystem, "4, 6");

		JLabel lblFilesystemUnit = new JLabel("MiB");
		panelCache.add(lblFilesystemUnit, "6, 6");
		
		JPanel panelPlaces = new JPanel();
		tabbedPane.addTab("Orte", null, panelPlaces, null);
		panelPlaces.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		JLabel lblSuche = new JLabel("Suche:");
		panelPlaces.add(lblSuche, "2, 2, 2, 1");
		
		textField = new JTextField();
		panelPlaces.add(textField, "2, 4, fill, default");
		textField.setColumns(10);
		
		JButton btnSuchen = new JButton("Suchen");
		panelPlaces.add(btnSuchen, "3, 4");
		
		JLabel lblSuchergebnisse = new JLabel("Suchergebnisse:");
		panelPlaces.add(lblSuchergebnisse, "2, 6");
		
		JList list = new JList();
		list.setModel(new AbstractListModel() {
			String[] values = new String[] {};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		panelPlaces.add(list, "2, 8, 2, 1, fill, fill");
		
		JLabel lblMarkiertePunkte = new JLabel("Markierte Punkte:");
		panelPlaces.add(lblMarkiertePunkte, "2, 10");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		panelPlaces.add(scrollPane_1, "2, 12, 2, 1, fill, fill");
		
		JPanel panel = new JPanel();
		scrollPane_1.setViewportView(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JCheckBox chckbxMeinZuhause = new JCheckBox("Mein Zuhause");
		chckbxMeinZuhause.setSelected(true);
		panel.add(chckbxMeinZuhause);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Uni Passau");
		chckbxNewCheckBox.setSelected(true);
		panel.add(chckbxNewCheckBox);
		
		JCheckBox chckbxSparkasse = new JCheckBox("Sparkasse");
		panel.add(chckbxSparkasse);
		
		JLabel lblAngezeigteBeschriftungen = new JLabel("Angezeigte Beschriftungen:");
		panelPlaces.add(lblAngezeigteBeschriftungen, "2, 14");
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panelPlaces.add(scrollPane_2, "2, 16, 2, 1, fill, fill");
		
		JPanel panel_1 = new JPanel();
		scrollPane_2.setViewportView(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		JCheckBox chckbxLndernamen = new JCheckBox("Ländernamen");
		chckbxLndernamen.setSelected(true);
		panel_1.add(chckbxLndernamen);
		
		JCheckBox chckbxStdtenamen = new JCheckBox("Städtenamen");
		chckbxStdtenamen.setSelected(true);
		panel_1.add(chckbxStdtenamen);
		
		JCheckBox chckbxTankstellen = new JCheckBox("Tankstellen");
		panel_1.add(chckbxTankstellen);

		JPanel panelDetails = new JPanel();
		panelDetails.setPreferredSize(new Dimension(200, 300));
		panelDetails.setMinimumSize(new Dimension(200, 10));
		panelMenu.add(panelDetails, BorderLayout.SOUTH);
		panelDetails
				.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
				JLabel lblDetails = new JLabel("Details:");
				lblDetails.setBorder(new MatteBorder(2, 0, 2, 0, (Color) new Color(0,
						0, 0)));
				panelDetails.add(lblDetails, "2, 2, fill, default");
		
				JLabel lblLongitude = new JLabel("Längengrad: 54° 44′ 19″");
				panelDetails.add(lblLongitude, "2, 4");
		
				JLabel lblLatitude = new JLabel("Breitengrad: 12° 11′ 44″");
				panelDetails.add(lblLatitude, "2, 6");
		
				JTextArea txtInfo = new JTextArea();
				txtInfo.setMinimumSize(new Dimension(4, 100));
				txtInfo.setPreferredSize(new Dimension(4, 100));
				txtInfo.setWrapStyleWord(true);
				txtInfo.setLineWrap(true);
				txtInfo.setText("Für diesen Ort sind keine Informationen verfügbar.");
				txtInfo.setFocusable(false);
				txtInfo.setOpaque(false);
				txtInfo.setEditable(false);
				panelDetails.add(txtInfo, "2, 8, fill, fill");
		
				btnPointOperation = new JButton("Punkt markieren");
				btnPointOperation.setMaximumSize(new Dimension(2222222, 2222222));
				btnPointOperation.setMinimumSize(new Dimension(100, 23));
				btnPointOperation.setActionCommand(AC_SWITCHTO_POINT_REMOVE);
				btnPointOperation.setIcon(POINT_DEFAULT);
				btnPointOperation.addActionListener(this);
				panelDetails.add(btnPointOperation, "2, 9");
	}

	private void addCustomRenderer() {
		comboBoxViewmode.setRenderer(new IconizedNamedItemListCellRenderer<>());
		comboBoxMaptype.setRenderer(new IconizedNamedItemListCellRenderer<>());
	}

	/**
	 * Loads a rescaled version of an image as Icon from the resources.
	 * 
	 * @param path
	 *            resource path of the image
	 * @param size
	 *            the size to rescale to
	 * @return the requested Icon
	 */
	private static Icon getRescaledIcon(final String path, final int size) {
		return new ImageIcon(new ImageIcon(MainWindow.class.getResource(path))
				.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		int min, max, cur;
		String ac = e.getActionCommand();
		switch (ac) {
		case AC_SWITCHTO_POINT_REMOVE:
			btnPointOperation.setActionCommand(AC_SWITCHTO_POINT_ADD);
			btnPointOperation.setIcon(POINT_REMOVE);
			btnPointOperation.setText("Markierung entfernen");
			break;
		case AC_SWITCHTO_POINT_ADD:
			btnPointOperation.setActionCommand(AC_SWITCHTO_POINT_REMOVE);
			btnPointOperation.setText("Punkt markieren");
			btnPointOperation.setIcon(POINT_DEFAULT);
			break;
		case AC_ZOOM_IN:
			max = sliderZoom.getMaximum();
			cur = sliderZoom.getValue();
			if (cur < max) {
				sliderZoom.setValue(cur + 1);
			}
			break;
		case AC_ZOOM_OUT:
			min = sliderZoom.getMinimum();
			cur = sliderZoom.getValue();
			if (cur > min) {
				sliderZoom.setValue(cur - 1);
			}
			break;
		default:
			break;
		}
	}

}
