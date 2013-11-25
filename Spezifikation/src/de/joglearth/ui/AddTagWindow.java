package de.joglearth.ui;


import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.FlowLayout;

import javax.swing.SwingConstants;

public class AddTagWindow extends JDialog {
	private JTextField nameTextField;
	private JTextField descriptionTextField;
	public AddTagWindow() {
		setSize(370, 250);
		setTitle("Add user tag");
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("top:default"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("top:default"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("top:default"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("top:default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("top:default"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		JLabel longitudeCaptionLabel = new JLabel("Longitude");
		getContentPane().add(longitudeCaptionLabel, "2, 2");
		
		JLabel longitudeLabel = new JLabel("12° 36' 13\" E");
		getContentPane().add(longitudeLabel, "4, 2");
		
		JLabel latitudeCaptionLabel = new JLabel("Latitude");
		getContentPane().add(latitudeCaptionLabel, "2, 4");
		
		JLabel latitudeLabel = new JLabel("54° 11' 54\" N");
		getContentPane().add(latitudeLabel, "4, 4");
		
		JLabel nameCaptionLabel = new JLabel("Name");
		getContentPane().add(nameCaptionLabel, "2, 6, left, default");
		
		nameTextField = new JTextField();
		getContentPane().add(nameTextField, "4, 6, fill, default");
		nameTextField.setColumns(10);
		
		JLabel descriptionCaptionLabel = new JLabel("Description");
		descriptionCaptionLabel.setVerticalAlignment(SwingConstants.TOP);
		getContentPane().add(descriptionCaptionLabel, "2, 8, left, default");
		
		descriptionTextField = new JTextField();
		getContentPane().add(descriptionTextField, "4, 8, fill, fill");
		descriptionTextField.setColumns(10);
		
		JPanel buttonPane = new JPanel();
		getContentPane().add(buttonPane, "4, 10, fill, fill");
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		
		JButton cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		
		JButton okButton = new JButton("OK");
		buttonPane.add(okButton);
		
	}

	public static void main(String[] args) {
		new AddTagWindow().setVisible(true);;

	}

}
