package de.joglearth.source.caching;

import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;


/**
 * Implements the {@link de.joglEarth.source.caching.Cache} interface in such way that it stores
 * objects in the file system.
 * 
 */
public class FileSystemCache<Key> implements Cache<Key, byte[]> {

    private String              folder;
    private PathTranslator<Key> paths;


    /**
     * Constructor.Initializes a {@link de.joglearth.source.caching.FileSystemCache} as it assigns
     * values to the <code>folder</code> value and the
     * {@link de.joglEarth.source.caching.PathTranslator}.
     * 
     * @param folder The name of the folder where the objects should be stored
     * @param p The <code>PathTranslator</code> that is used by the <code>FileSystemCache</code> to
     *        convert a path to a string and vice versa
     */
    public FileSystemCache(String folder, PathTranslator<Key> p) {
        this.folder = folder;
        paths = p;
    }

    @Override
    public SourceResponse<byte[]> requestObject(Key key,
            SourceListener<Key, byte[]> sender) {
        // Datei wenn mögl. asynchron laden
        return null;
    }

    @Override
    public void putObject(Key k, byte[] v) {
        // Datei schreiben
    }

    @Override
    public void dropObject(Key k) {
        // Datei löschen
    }

    @Override
    public Iterable<Key> getExistingObjects() {
        // Ordner durchsuchen, Dateien auflisten, Keys generieren, zurückgeben
        return null;
    }

    @Override
    public void dropAll() {
        // TODO Automatisch generierter Methodenstub

    }
}
