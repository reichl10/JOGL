package de.joglearth.ui;


/**
 * 
 * @author trion
 *
 * @param E
 */
public class NamedItem<E> {
    private String caption;
    private E value;
    
    public NamedItem(String caption, E value) {
        this.caption = caption;
        this.value = value;
    }
    
    @Override
    public String toString() {
        return caption;
    }
    
    public E getValue() {
        return value;
    }
}
