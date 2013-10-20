package gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class IconizedNamedItemListCellRenderer<E> extends JLabel implements ListCellRenderer<E> {

	public IconizedNamedItemListCellRenderer() {
		this.setOpaque(true);
	}
	@Override
	public Component getListCellRendererComponent(JList<? extends E> list,
			E value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value instanceof IconizedNamedItem) {
			System.out.println("Is item!");
			IconizedNamedItem item = (IconizedNamedItem) value;
			this.setIcon(item.icon);
			this.setText(item.name);
		} else {
			System.out.println("Is No item!");
			this.setIcon(null);
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
