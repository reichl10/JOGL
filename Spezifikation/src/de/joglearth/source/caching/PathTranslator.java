package de.joglearth.source.caching;


public interface PathTranslator<Key> {
    String toFileSystemPath(Key k);
    Key fromFileSystemPath(String s);
}
