package de.joglearth.surface;

/**
 * Type of an OpenStreetMap tile. OSM tiles have a maximum zoom level of '18'.
 * 
 */
public enum TiledMapType {

    /**
     * OpenStreetMap maps.
     */
    OSM_MAPNIK,

    /**
     * Especially for walkers and riders.
     */
    HIKING,

    /**
     * Especially for bicyclists.
     */
    CYCLING,

    /**
     * Especially for winter sports enthusiasts.
     */
    SKIING,
    
    /**
     * A map with 3D models of buildings.
     */
    OSM2WORLD
}
