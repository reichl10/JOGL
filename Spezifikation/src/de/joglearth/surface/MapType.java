package de.joglearth.surface;

/**
 * <code>Type</code> of an OpenStreetMap tile. OSM tiles has a maximum Zoomlevel of '18'.
 * 
 */
public enum MapType {
    
    SATELLITE,
    
    CHILDREN,

    /**
     * OpenStreetMap maps.
     */
    OSM_MAP,

    /**
     * Especially for walkers and riders.
     */
    TREKKING_MAP,

    /**
     * Especially for seafaring.
     */
    SEA_MAP,

    /**
     * Especially for bicyclists.
     */
    CYCLE_MAP,

    /**
     * Especially for winter sports enthusiasts.
     */
    SKI_RUN_MAP
}