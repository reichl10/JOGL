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
     * The requested value is temporarily not available. An <code>asynchronous</code> response
     * follows.
     */
    ASYNCHRONOUS,

    /**
     * The requested value is not available. It could not be acquired.
     */
    MISSING
}
