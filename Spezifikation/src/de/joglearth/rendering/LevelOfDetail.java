package de.joglearth.rendering;


/**
 * Enumerates levels of detail for mesh tiles.
 */
public enum LevelOfDetail {
    
    /**
     * Lowest detail level. Provides the best performance.
     */
    LOW,
    
    /**
     * Medium detail level. A trade-off between <code>LOW</code> and <code>HIGH</code>.
     */
    MEDIUM,
    
    /**
     * Highest detail level. Provides the best possible visual experience at the expense of performance.
     */
    HIGH
}
