package de.joglearth.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.joglearth.location.Location;
import de.joglearth.location.LocationType;


/**
 * Dialog for entering information about a location. You can add a <code>WindowListener</code> to
 * this dialog reaction to it closing. The given <code>Location</code> object will be manipulated
 * before closing to represent the users input.
 */
public class LocationEditDialog extends JDialog {

    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = -8715326019757123990L;

    private JTextField nameTextField;

    private JTextField descriptionTextField;
    private Location loc;


    /**
     * Create the dialog for entering information about a location.
     * 
     * @param location The <code>Location</code> object will be changed before closing
     */
    public LocationEditDialog(Location location) {
        loc = location;
        setSize(370, 250);
        setTitle("Add user tag");
        getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormFactory.RELATED_GAP_COLSPEC, },
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
                        FormFactory.RELATED_GAP_ROWSPEC, }));

        JLabel longitudeCaptionLabel = new JLabel("Longitude");
        getContentPane().add(longitudeCaptionLabel, "2, 2");

        JLabel longitudeLabel = new JLabel(location.point.getLongitudeString());
        getContentPane().add(longitudeLabel, "4, 2");

        JLabel latitudeCaptionLabel = new JLabel("Latitude");
        getContentPane().add(latitudeCaptionLabel, "2, 4");

        JLabel latitudeLabel = new JLabel(location.point.getLatitudeString());
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
        

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                loc.details = descriptionTextField.getText();
                loc.name = nameTextField.getText();
                loc.type = LocationType.USER_TAG;
                dispose();
            }
        });
        buttonPane.add(okButton);

    }

}
