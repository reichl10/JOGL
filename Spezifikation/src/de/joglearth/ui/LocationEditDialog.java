package de.joglearth.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.joglearth.location.Location;
import de.joglearth.location.LocationType;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;


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

    private JTextArea descriptionTextArea;
    private Location loc;


    /**
     * Create the dialog for entering information about a location.
     * 
     * @param location The <code>Location</code> object will be changed before closing
     */
    public LocationEditDialog(Location location) {
        loc = location;
        setSize(370, 250);
        setTitle(Messages.getString("LocationEditDialog.title")); //$NON-NLS-1$
        getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"), //$NON-NLS-1$
                FormFactory.RELATED_GAP_COLSPEC, },
                new RowSpec[] {
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("top:default"), //$NON-NLS-1$
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("top:default"), //$NON-NLS-1$
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("top:default"), //$NON-NLS-1$
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("top:default:grow"), //$NON-NLS-1$
                        FormFactory.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("top:default"), //$NON-NLS-1$
                        FormFactory.RELATED_GAP_ROWSPEC, }));

        JLabel longitudeCaptionLabel = new JLabel(Messages.getString("LocationEditDialog.longitude")); //$NON-NLS-1$
        getContentPane().add(longitudeCaptionLabel, "2, 2"); //$NON-NLS-1$

        JLabel longitudeLabel = new JLabel(location.point.getLongitudeString());
        getContentPane().add(longitudeLabel, "4, 2"); //$NON-NLS-1$

        JLabel latitudeCaptionLabel = new JLabel(Messages.getString("LocationEditDialog.latitude")); //$NON-NLS-1$
        getContentPane().add(latitudeCaptionLabel, "2, 4"); //$NON-NLS-1$

        JLabel latitudeLabel = new JLabel(location.point.getLatitudeString());
        getContentPane().add(latitudeLabel, "4, 4"); //$NON-NLS-1$

        JLabel nameCaptionLabel = new JLabel(Messages.getString("LocationEditDialog.name")); //$NON-NLS-1$
        getContentPane().add(nameCaptionLabel, "2, 6, left, default"); //$NON-NLS-1$

        nameTextField = new JTextField();
        getContentPane().add(nameTextField, "4, 6, fill, default"); //$NON-NLS-1$
        nameTextField.setColumns(10);

        JLabel descriptionCaptionLabel = new JLabel(Messages.getString("LocationEditDialog.description")); //$NON-NLS-1$
        descriptionCaptionLabel.setVerticalAlignment(SwingConstants.TOP);
        getContentPane().add(descriptionCaptionLabel, "2, 8, left, default"); //$NON-NLS-1$
        
        JScrollPane scrollPane = new JScrollPane();
        getContentPane().add(scrollPane, "4, 8, fill, fill");

        descriptionTextArea = new JTextArea();
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        scrollPane.setViewportView(descriptionTextArea);
        descriptionTextArea.setColumns(10);
        
        descriptionTextArea.setText(loc.details);

        JPanel buttonPane = new JPanel();
        getContentPane().add(buttonPane, "4, 10, fill, fill"); //$NON-NLS-1$
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        

        JButton okButton = new JButton(Messages.getString("LocationEditDialog.ok")); //$NON-NLS-1$
        okButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                loc.details = descriptionTextArea.getText();
                loc.name = nameTextField.getText();
                loc.type = LocationType.USER_TAG;
                Settings.getInstance().putLocation(SettingsContract.USER_LOCATIONS, loc);
                dispose();
            }
        });
        buttonPane.add(okButton);
        nameTextField.setText(loc.name);
    }

}
