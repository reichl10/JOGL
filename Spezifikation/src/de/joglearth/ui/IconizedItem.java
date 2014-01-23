package de.joglearth.ui;

import javax.swing.Icon;


/**
 * An aggregation of a generic value, a caption and a Swing icon, used with a
 * {@link IconListCellRenderer} to display iconized List and ComboBox items.
 * 
 * @param E The generic value type.
 */
public class IconizedItem<E> extends NamedItem<E> {

    private Icon icon;


    /**
     * Constructor. Creates an IconizedItem with value <code>null</code>.
     * 
     * @param name The caption, later returned by toString().
     * @param icon The icon.
     */
    public IconizedItem(String name, Icon icon) {
        super(name, null);
        this.icon = icon;
    }

    /**
     * Constructor.
     * 
     * @param name The caption, later returned by toString().
     * @param icon The icon.
     * @param value The value.
     */
    public IconizedItem(String name, Icon icon, E value) {
        super(name, value);
        this.icon = icon;
    }

    /**
     * Returns the icon passed to the constructor.
     * 
     * @return The icon.
     */
    public Icon getIcon() {
        return icon;
    }
}
