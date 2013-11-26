package de.joglearth.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.joglearth.surface.Location;


/**
 * Dialog for entering information about a location. You can add a <code>WindowListener</code> to
 * this dialog reaction to it closing. The given <code>Location</code> object will be manipulated
 * before closing to represent the users input.
 */
public class MarkerDialogue extends JDialog {

    private final JPanel contentPanel = new JPanel();


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            MarkerDialogue dialog = new MarkerDialogue(null);
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog for entering information about a location.
     * @param location the {@link Location} object will be changed before closing
     */
    public MarkerDialogue(Location location) {
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setLayout(new FlowLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

}