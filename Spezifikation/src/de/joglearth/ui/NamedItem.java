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
     * 
     * @param caption The caption, later returned by <code>toString()</code>
     * @param value The value to map to
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
     * Returns the item's value.
     */
    public E getValue() {
        return value;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() == this.getClass()) {
            NamedItem<E> item = (NamedItem<E>) obj;
            return value.equals(item.value);
        }
        return false;
    }
}
