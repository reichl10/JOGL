package de.joglearth.ui;

import javax.swing.Icon;

public class IconizedItem<E> extends NamedItem<E> {
	private Icon icon;
	
    public IconizedItem(String name, Icon icon) {
        super(name, null);
        this.icon = icon;
    }

    public IconizedItem(String name, Icon icon, E value) {
        super(name, value);
        this.icon = icon;
    }
    
    public Icon getIcon() {
        return icon;
    }
	
}
