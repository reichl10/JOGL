package de.joglearth.source.caching;

/**
 * Is used to convert a path to a <code>Key</code> and vice versa.
 */
public interface PathTranslator<Key> {
    
    /**
     * Converts a <code>Key</code> into a path.
     * @param k The <code>Key</code> that be resolved to a string
     * @return The string representation of the <code>Key</code>
     */
    String toFileSystemPath(Key k);
    
    /**
     * Converts a path to a <code>Key</code>.
     * @param s The path that should be resolved to a <code>Key</code>
     * @return The <code>Key</code> described by the path
     */
    Key fromFileSystemPath(String s);
}
