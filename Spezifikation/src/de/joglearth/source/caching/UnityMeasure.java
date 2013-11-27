package de.joglearth.source.caching;

/**
<<<<<<< HEAD
 * Implements the {@link ObjectMeasure} interface in such way that it can measure the size of single
 * objects. Treats every object as a object of the size 1.
 * @param <T> The type of the measured object
=======
 * Implements the ObjectMeasure interface for generic types, treating all objects as equally sized.
 * 
 * @param <T> The type to measure.
>>>>>>> Übersehene Javadocs committed
 */
public class UnityMeasure<T> implements ObjectMeasure<T> {

    @Override
    public int getSize(T t) {
        return 1;
    }

}
