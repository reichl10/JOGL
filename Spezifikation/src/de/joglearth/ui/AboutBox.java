package de.joglearth.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

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

import javax.swing.JTextArea;

import java.awt.Canvas;

import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.awt.Dialog.ModalityType;
import java.awt.Window.Type;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JScrollPane;

public class AboutBox extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextArea txtrLoremIpsumDolor;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	    try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (
            ClassNotFoundException | InstantiationException | IllegalAccessException
            | UnsupportedLookAndFeelException e1) {
        }
		try {
			AboutBox dialog = new AboutBox();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AboutBox() {
	    setTitle("About JoglEarth");
	    setResizable(false);
	    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 385, 322);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
		        FormFactory.RELATED_GAP_COLSPEC,
		        ColumnSpec.decode("default:grow"),
		        FormFactory.RELATED_GAP_COLSPEC,},
		    new RowSpec[] {
		        RowSpec.decode("15dlu"),
		        FormFactory.DEFAULT_ROWSPEC,
		        RowSpec.decode("15dlu"),
		        RowSpec.decode("default:grow"),
		        FormFactory.RELATED_GAP_ROWSPEC,
		        FormFactory.DEFAULT_ROWSPEC,
		        FormFactory.RELATED_GAP_ROWSPEC,}));
		{
		    JLabel lblNewLabel = new JLabel("");
		    lblNewLabel.setIcon(new ImageIcon("/home/trion/Uni/SEP/Git/Spezifikation/AboutLogo.png"));
		    contentPanel.add(lblNewLabel, "2, 2, center, default");
		}
		{
		    JScrollPane scrollPane = new JScrollPane();
		    contentPanel.add(scrollPane, "2, 4, fill, fill");
		    {
		    	txtrLoremIpsumDolor = new JTextArea();
		    	scrollPane.setViewportView(txtrLoremIpsumDolor);
		    	txtrLoremIpsumDolor.setText("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
		    	txtrLoremIpsumDolor.setLineWrap(true);
		    	txtrLoremIpsumDolor.setEditable(false);
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
