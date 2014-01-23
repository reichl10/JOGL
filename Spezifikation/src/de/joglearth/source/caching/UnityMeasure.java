package de.joglearth.source.caching;

/**
 * Implements the {@link ObjectMeasure} interface in such way that it can measure the size of single
 * objects. Treats every object as a object of the size 1.
 * 
 * @param T The type of the measured object
 */
public class UnityMeasure<T> implements ObjectMeasure<T> {

    @Override
    public int getSize(T t) {
        return 1;
    }
}
