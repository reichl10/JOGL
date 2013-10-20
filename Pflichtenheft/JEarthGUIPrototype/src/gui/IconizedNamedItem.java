package gui;

import javax.swing.Icon;

public class IconizedNamedItem {
	public Icon icon;
	public String name;

	public IconizedNamedItem(String name, Icon icon) {
		this.icon = icon;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
