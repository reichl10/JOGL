package de.joglearth.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import de.joglearth.JoglEarth;

import javax.swing.JTextArea;

import java.awt.Canvas;

import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.awt.Dialog.ModalityType;
import java.awt.Window.Type;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;


public class AboutBox extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextArea aboutTextArea;


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            AboutBox dialog = new AboutBox();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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
     * Create the dialog.
     */
    public AboutBox() {
        setTitle("About " + JoglEarth.PRODUCT_NAME);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 385, 322);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormFactory.RELATED_GAP_COLSPEC, },
                new RowSpec[] {
                        RowSpec.decode("12dlu"),
                        RowSpec.decode("default:grow"),
                        RowSpec.decode("12dlu"),
                        RowSpec.decode("default:grow"),
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC, }));
        {
            JPanel logoPanel = new JPanel();
            contentPanel.add(logoPanel, "2, 2, center, fill");
            logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.X_AXIS));
            {
                JLabel logoLeftLabel = new JLabel("");
                logoLeftLabel.setIcon(loadIconResource("icons/AboutJogl.png"));
                logoPanel.add(logoLeftLabel);
            }
            {
                JPanel logoRightPanel = new JPanel();
                logoPanel.add(logoRightPanel);
                logoRightPanel.setLayout(new FormLayout(new ColumnSpec[] {
                        FormFactory.RELATED_GAP_COLSPEC,
                        ColumnSpec.decode("right:default"), },
                        new RowSpec[] {
                                RowSpec.decode("top:default"),
                                RowSpec.decode("bottom:default"), }));
                {
                    JLabel logoRightLabel = new JLabel("");
                    logoRightLabel.setIcon(loadIconResource("icons/AboutEarth.png"));
                    logoRightPanel.add(logoRightLabel, "2, 1");
                }
                {
                    JLabel versionLabel = new JLabel("v" + JoglEarth.PRODUCT_VERSION);
                    logoRightPanel.add(versionLabel, "2, 2");
                }
            }
        }
        {
            JScrollPane aboutScrollPane = new JScrollPane();
            contentPanel.add(aboutScrollPane, "2, 4, fill, fill");
            {
                aboutTextArea = new JTextArea();
                aboutScrollPane.setViewportView(aboutTextArea);
                aboutTextArea
                        .setText("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
                aboutTextArea.setLineWrap(true);
                aboutTextArea.setEditable(false);
            }
        }
        {
            JPanel buttonPane = new JPanel();
            contentPanel.add(buttonPane, "2, 6");
            FlowLayout fl_buttonPane = new FlowLayout(FlowLayout.RIGHT);
            fl_buttonPane.setVgap(0);
            fl_buttonPane.setHgap(0);
            buttonPane.setLayout(fl_buttonPane);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent arg0) {
                        dispose();
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
        }
    }

}
