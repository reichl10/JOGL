package de.joglearth.map.osm;

/**
 * Enumerates different OpenStreetMap map types.
 */
public enum OSMMapType {

    /**
     * OpenStreetMap maps.
     */
    MAPNIK,

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
    OSM2WORLD, 
    
    /**
     * Standard OpenStreetMap without labels.
     */
    OSM_NOLABELS,
    
    /**
     * Satellite Tiles without labels.
     */
    SATELLITE
}
