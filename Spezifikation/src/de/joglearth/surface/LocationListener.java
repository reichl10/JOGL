package de.joglearth.surface;

public interface LocationListener {
    
    /**
     * Sends a notification that the results are available simultaneously
     * to a Location array containing the results
     * @param results a array of Locations withe results of the search
     */
	void searchResultsAvailable(Location[] results);
}
