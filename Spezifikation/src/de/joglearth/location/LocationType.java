package de.joglearth.location;

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
     * Returns spare-time activities like theme parks, museums, picnic sites and
     * viewpoints.
     */
    ACTIVITY,

    /**
     * Returns outdoor pursuits for bikers and hikers like picnic sites viewpoints, benches,
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