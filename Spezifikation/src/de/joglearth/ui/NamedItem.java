package de.joglearth.ui;

/**
 * Aggregation of a string caption and a generic value used to map values to captions inside
 * ComboBoxes or Lists.
 * 
 * @param E The generic type.
 */
public class NamedItem<E> {

    private String caption;
    private E value;


    /**
     * Constructor.
     * @param caption The caption, later returned by {@link toString().}
     * @param value The value to map to.
     */
    public NamedItem(String caption, E value) {
        this.caption = caption;
        this.value = value;
    }

    /**
     * Returns the caption.
     */
    @Override
    public String toString() {
        return caption;
    }

    /**
     * Returns the item's svalue.
     */
    public E getValue() {
        return value;
    }
}
