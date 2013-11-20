package de.joglearth.surface;

/**
 * Classes implementing this interface receive asynchronous answers from the
 * {@link LocationMananger} such as search results.
 * 
 */
public interface LocationListener {

    /**
     * Is called as a notification that the results are available simultaneously to a
     * {@link Location} array containing the results.
     * 
     * @param results a array of <code>Locations</code> with results of the search
     */
    void searchResultsAvailable(Location[] results);
}
