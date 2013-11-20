package de.joglearth.source;

/**
 * Type of the response of the source. SYNCHRONOUS: Shows if the response includes the requested
 * value. ASYNCHRONOUS: The requested value is temporarily not available. It must be loaded via
 * internet. MISSING: The requested value is not available. It could not be loaded via internet.
 * 
 */
public enum SourceResponseType {
    SYNCHRONOUS, ASYNCHRONOUS, MISSING
}
