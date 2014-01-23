package de.joglearth.opengl;


/**
 * Enumerates texture filtering modes supported by the {@link GLContext}.
 */
public enum TextureFilter {
    
    /**
     * Nearest-neighbour interpolation. Chooses the texel closest to the pixel's center. 
     * Fast, creates a sharp look for scaled-down, a blocky look for scaled-up textures.
     */
    NEAREST,
    
    /**
     * Linear interpolation between the four texels closest to the pixel's center.
     * Works well for scaling up, but causes noise and distortion when scaling down. 
     * This mode does not use mipmaps.
     */
    BILINEAR,
    
    /**
     * Linear interpolation between the four texels closest to the pixel's center on the two 
     * mipmaps closest to the effective resolution. Corrects the noise incurred by bilinear
     * filtering, but causes blurry textures when viewing surfaces at an oblique angle.
     * This mode uses mipmaps.
     */
    TRILINEAR,
    
    /**
     * Like trilinear filtering, but uses additional, non-square mipmaps for reducing blurriness
     * of surfaces viewed at an oblique angle. "2x" means the horizontal image frequency
     * can be twice as high as the vertical one (and vice versa) without losing sharpness.
     */
    ANISOTROPIC_2X,
    
    /**
     * Like ANISOTROPIC_2X, but for up to a 4:1 frequency ratio.
     */
    ANISOTROPIC_4X,

    /**
     * Like ANISOTROPIC_2X, but for up to a 8:1 frequency ratio.
     */
    ANISOTROPIC_8X,

    /**
     * Like ANISOTROPIC_2X, but for up to a 16:1 frequency ratio.
     */
    ANISOTROPIC_16X
}
