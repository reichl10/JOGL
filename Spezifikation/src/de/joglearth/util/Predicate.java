package de.joglearth.util;

/**
 * Interface for classes assigning boolean values, i.e. predicates, to objects.
 * 
 * @param T The type to test
 */
public interface Predicate<T> {

    /**
     * Tests an object if it satisfies the predicate condition.
     * 
     * @param t The object
     * @return Whether the predicate is satisfied
     */
    public boolean test(T t);
}
