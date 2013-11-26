package de.joglearth.source.caching;

/**
 * Classes implementing this interface can determine the size of an object.
 * @param <T> The type of the measured object
 */
public interface ObjectMeasure<T> {
	
    /**
     * Returns the size of an object
     * @param t The object to measure
     * @return The size of the object that should be measured
     */
    int getSize(T t);
}
