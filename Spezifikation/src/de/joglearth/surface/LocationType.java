package de.joglearth.surface;

/**
 * Represents the type of overlays like POIs, city names and points marked by the user.
 */
public enum LocationType {
    
    /**
     * Returns cafes, restaurants and beer gardens.
     */
    RESTAURANT,
    
    /**
     * Returns bars, clubs, cinemas and solaria (tanning booths).
     */
    NIGHTLIFE,
    
    /**
     * Returns banks.
     */
    BANK,
    
    /**
     * Returns public toilets.
     */
    TOILETS,
    
    /**
     * Returns supermarkets, bakeries, butchers and drugstores.
     */
    FOOD,
    
    /**
     * Returns a variety of shops.
     */
    SHOPS,
    
    /**
     * Returns sparetime activities like amusement parks or museums, places for picnics and
     * lookouts.
     */
    ACTIVITY,
    
    /**
     * Returns outdoor pursuits for bikers and hikers like picnic places and lookouts, benches,
     * rubbish bins and bike shops.
     */
    OUTDOORACTIVITY,
    
    /**
     * Returns educational institutions.
     */
    EDUCATION,
    
    /**
     * Returns doctors, hospitals, chemists and drug stores.
     */
    HEALTH,
    
    /**
     * Returns post offices, mailboxes and freight stations.
     */
    POST,
    
    /**
     * Returns hotels and bed and breakfast places.
     */
    HOTELS
}