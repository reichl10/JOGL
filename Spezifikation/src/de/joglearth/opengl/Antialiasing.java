package de.joglearth.opengl;

/**
 * Enumerates anti-aliasing modes used in <code>GLCanvas</code> construction.
 */
public enum Antialiasing {
    
    /**
     * No Anti-Aliasing
     */
    NONE,
    
    /**
     * 2x Multi-Sampling Anti-Aliasing
     */
    MSAA_2X,
    
    /**
     * 4x Multi-Sampling Anti-Aliasing
     */
    MSAA_4X,
    
    /**
     * 8x Multi-Sampling Anti-Aliasing
     */
    MSAA_8X,
    
    /**
     * 16x Multi-Sampling Anti-Aliasing
     */
    MSAA_16X
}
