package de.joglearth.surface;

/**
 * Classes implementing this interface receive asynchronous answers from the
 * <code>LocationMananger</code> such as search results.
 *
 */
public interface LocationListener {

    /**
     * Is called as a notification that the results are available simultaneously to a Location array
     * containing the results
     * 
     * @param results a array of Locations with results of the search
     */
    void searchResultsAvailable(Location[] results);
}