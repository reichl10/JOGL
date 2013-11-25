package de.joglearth.source.caching;


public interface FileSystemTranslator<Key> {
    String toFileSystemPath(Key k);
    Key fromFileSystemPath(String s);
}
