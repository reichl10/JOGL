package de.joglearth.source;

/**
 * Type of the response of the source. <code>SYNCHRONOUS</code>: Shows if the response includes the
 * requested value. <code>ASYNCHRONOUS</code>: The requested value is temporarily not available. It
 * must be loaded via HTTP. <code>MISSING</code>: The requested value is not available. It could not
 * be loaded via HTTP.
 * 
 */
public enum SourceResponseType {
    SYNCHRONOUS, ASYNCHRONOUS, MISSING
}
