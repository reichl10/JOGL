package de.joglearth.source;

/**
 * Type of the response of the source. 
 */
public enum SourceResponseType {
    /**
     * Shows if the response includes the requested value.
     */
    SYNCHRONOUS, 
    
    /**
     * The requested value is temporarily not available. It must be loaded via internet.
     */
    ASYNCHRONOUS, 
    
    /**
     * The requested value is not available. It could not be loaded via internet.
     */
    MISSING
}
