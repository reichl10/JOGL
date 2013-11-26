package de.joglearth.surface;

/**
 * Represents overlay types like POIs, city names and points marked by the user.
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
     * Returns supermarkets, bakeries, butchers and drug stores.
     */
    GROCERY_SHOPS,

    /**
     * Returns a variety of shops.
     */
    SHOPS,

    /**
     * Returns spare-time activities like amusement parks or museums, places for picnics and
     * lookouts.
     */
    ACTIVITY,

    /**
     * Returns outdoor pursuits for bikers and hikers like picnic places and lookouts, benches,
     * rubbish bins and bike shops.
     */
    HIKING_AND_CYCLING,

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
    HOTELS,
    
    /**
     * Returns a city.
     */
    CITY,
    
    /**
     * Returns a town.
     */
    TOWN,
    
    /**
     * Returns a village.
     */
    VILLAGE,
    
    /**
     * Returns a point marked by the user.
     */
    USER_TAG
}