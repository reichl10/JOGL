package de.joglearth.surface;

import java.util.Collection;

/**
 * Classes implementing this interface receive asynchronous answers from the
 * {@link LocationMananger} such as search results.
 * 
 */
public interface LocationListener {

    /**
     * Is called as a notification that the results are available simultaneously to a
     * {@link Location} collection containing the results.
     * 
     * @param results A collection of <code>Locations</code> with results of the search
     */
    void searchResultsAvailable(Collection<Location> results);
}
