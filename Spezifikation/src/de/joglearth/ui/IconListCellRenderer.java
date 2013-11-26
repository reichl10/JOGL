package de.joglearth.ui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class IconListCellRenderer<E> extends JLabel implements ListCellRenderer<E> {

	/**
     * Makes the compiler happy.
     */
    private static final long serialVersionUID = 71902586978221445L;
    
    public IconListCellRenderer() {
		this.setOpaque(true);
	}
    
	@Override
	public Component getListCellRendererComponent(JList<? extends E> list,
			E value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value instanceof IconizedItem) {
			IconizedItem<?> item = (IconizedItem<?>) value;
			this.setIcon(item.getIcon());
		} else {
			this.setIcon(null);
		}
		if (value != null) {
		    this.setText(value.toString());
		}
		if (isSelected) {
			this.setBackground(list.getSelectionBackground());
			this.setForeground(list.getSelectionForeground());
		} else {
			this.setBackground(list.getBackground());
			this.setForeground(list.getForeground());
		}
		return this;
	}

}
