package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
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

	/**
	 * Launch the application.
	 * 
	 * @param args
	 *            Command line arguments
	 */
	public static void main(final String[] args) {
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
		splitPane.setContinuousLayout(true);
		splitPane.setOneTouchExpandable(true);
		frmJogleearth.getContentPane().add(splitPane, BorderLayout.CENTER);

		JPanel panelLeft = new JPanel();
		splitPane.setRightComponent(panelLeft);
		panelLeft.setLayout(new BorderLayout(0, 0));

		JPanel panelBottom = new JPanel();
		panelLeft.add(panelBottom, BorderLayout.SOUTH);
		panelBottom.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("146px:grow"),
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
		panelBottom.add(progressBarLoad, "1, 1, center, center");
								
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
		lblScale.setIcon(new ImageIcon(MainWindow.class.getResource("/gui/icons/scale.png")));

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
		panelMenu.setPreferredSize(new Dimension(200, 10));
		panelMenu.setMinimumSize(new Dimension(200, 10));
		splitPane.setLeftComponent(panelMenu);
		panelMenu.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		panelMenu.add(tabbedPane);

		JPanel panelView = new JPanel();
		tabbedPane.addTab("Ansicht", null, panelView, null);
		panelView
				.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec
						.decode("default:grow"), }, new RowSpec[] {
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
						FormFactory.DEFAULT_ROWSPEC, }));

		JLabel lblViewmode = new JLabel("Ansichtsmodus:");
		panelView.add(lblViewmode, "1, 2, fill, default");

		JComboBox comboBoxViewmode = new JComboBox();
		comboBoxViewmode.setModel(new DefaultComboBoxModel(new String[] {
				"Sonnensystem", "Karte" }));
		panelView.add(comboBoxViewmode, "1, 4, fill, default");

		JLabel lblMaptype = new JLabel("Karte:");
		panelView.add(lblMaptype, "1, 6, fill, default");

		JComboBox comboBoxMaptype = new JComboBox();
		comboBoxMaptype.setModel(new DefaultComboBoxModel(new String[] {
				"Satellitenbild", "OpenStreetMap" }));
		panelView.add(comboBoxMaptype, "1, 8, fill, default");

		JCheckBox chckbxHeightprofile = new JCheckBox("Höhenprofil");
		panelView.add(chckbxHeightprofile, "1, 10, fill, default");

		JCheckBox chckbxThreeDModel = new JCheckBox("3D-Modelle");
		chckbxThreeDModel.setSelected(true);
		panelView.add(chckbxThreeDModel, "1, 12, fill, default");

		JPanel panelSettings = new JPanel();
		tabbedPane.addTab("Einstellungen", null, panelSettings, null);
		panelSettings.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("default:grow"),
				ColumnSpec.decode("default:grow"), }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), }));

		JLabel lblSprache = new JLabel("Sprache:");
		panelSettings.add(lblSprache, "1, 2, 2, 1");

		JComboBox comboBox = new JComboBox();
		panelSettings.add(comboBox, "1, 4, 2, 1, fill, default");

		JLabel lblKartenserver = new JLabel("Kartenserver:");
		panelSettings.add(lblKartenserver, "1, 6, 2, 1");

		JScrollPane scrollPane = new JScrollPane();
		panelSettings.add(scrollPane, "1, 8, 2, 1, fill, fill");

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
		panelSettings.add(btnAddServer, "1, 11");

		JButton btnRemoveServer = new JButton("Entfernen");
		panelSettings.add(btnRemoveServer, "2, 11");

		JPanel panelCache = new JPanel();
		panelSettings.add(panelCache, "1, 13, 2, 1, fill, fill");
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

		JPanel panelDetails = new JPanel();
		panelDetails.setPreferredSize(new Dimension(100, 300));
		panelDetails.setMinimumSize(new Dimension(100, 300));
		panelMenu.add(panelDetails, BorderLayout.SOUTH);
		panelDetails
				.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec
						.decode("default:grow"), }, new RowSpec[] {
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						RowSpec.decode("default:grow"),
						FormFactory.DEFAULT_ROWSPEC, }));

		JLabel lblDetails = new JLabel("Details:");
		lblDetails.setBorder(new MatteBorder(2, 0, 2, 0, (Color) new Color(0,
				0, 0)));
		panelDetails.add(lblDetails, "1, 2, fill, default");

		JLabel lblLongitude = new JLabel("Längengrad:");
		panelDetails.add(lblLongitude, "1, 4");

		JLabel lblLatitude = new JLabel("Breitengrad:");
		panelDetails.add(lblLatitude, "1, 6");

		JTextArea txtInfo = new JTextArea();
		txtInfo.setPreferredSize(new Dimension(4, 100));
		txtInfo.setWrapStyleWord(true);
		txtInfo.setLineWrap(true);
		txtInfo.setText("Für diesen Ort sind keine Informationen verfügbar.");
		txtInfo.setFocusable(false);
		txtInfo.setOpaque(false);
		txtInfo.setEditable(false);
		panelDetails.add(txtInfo, "1, 8, fill, fill");

		btnPointOperation = new JButton("Punkt markieren");
		btnPointOperation.setActionCommand(AC_SWITCHTO_POINT_REMOVE);
		btnPointOperation.setIcon(POINT_DEFAULT);
		btnPointOperation.addActionListener(this);
		panelDetails.add(btnPointOperation, "1, 9");
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
