package de.joglearth.source.caching;

/**
 * Interface for classes assigning sizes to objects.
 * 
 * @param <T> The type to measure
 */
public interface ObjectMeasure<T> {

    /**
     * Determines the size of an object in a unit specific to the implementing class.
     * 
     * @param object The object to measure
     * @return The size, in units. Must be greater or equal 1
     */
    int getSize(T object);
}
