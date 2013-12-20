package de.joglearth.source.caching;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;


/**
 * Implements the {@link de.joglEarth.source.caching.Cache} interface in such way that it stores
 * objects in the file system.
 * 
 */
public class FileSystemCache<Key> implements Cache<Key, byte[]> {

    private Path basePath;
    private PathTranslator<Key> pathTranslator;
    private Set<Key> keySet;
    private Set<Key> lockedFileSet;
    private ExecutorService executorService;
    private Set<Key> filesForDroping;
    private Map<Key, Set<SourceListener<Key, byte[]>>> registredListeners;


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

        this.basePath = Paths.get(folder);
        this.pathTranslator = p;
        this.keySet = new HashSet<Key>();
        this.lockedFileSet = new HashSet<Key>();
        this.filesForDroping = new HashSet<Key>();
        this.registredListeners = new HashMap<Key, Set<SourceListener<Key, byte[]>>>();
        this.executorService = Executors.newFixedThreadPool(5);
    }

    @Override
    public synchronized SourceResponse<byte[]> requestObject(Key key,
            SourceListener<Key, byte[]> sender) {
        SourceResponseType responseType;
        if (keySet.contains(key)) {
            registerListener(key, sender);
            lockedFileSet.add(key);
            responseType = SourceResponseType.ASYNCHRONOUS;
            executorService.execute(new FileLoaderRunnable(key));
        } else {
            responseType = SourceResponseType.MISSING;
        }

        System.err.println("FileSystemCache: requesting " + key + ": " + responseType.toString());
        return new SourceResponse<byte[]>(responseType, null);
    }

    @Override
    public synchronized void putObject(Key k, byte[] v) {
        System.err.println("FileSystemCache: adding key " + k);
        Path filePath = pathFromKey(k);
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            Files.write(filePath, v, StandardOpenOption.CREATE);
            keySet.add(k);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void dropObject(Key k) {
        keySet.remove(k);
        if (lockedFileSet.contains(k)) {
            filesForDroping.add(k);
            return;
        }
        System.err.println("FileSystemCache: dropping key " + k);
        try {
            Files.deleteIfExists(pathFromKey(k));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized Iterable<Key> getExistingObjects() {
        try {
            Files.walkFileTree(basePath, new FileIndexerWalker(keySet, basePath, pathTranslator));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new HashSet<Key>(keySet);
    }

    @Override
    public synchronized void dropAll() {
        keySet.clear();
        if (lockedFileSet.size() > 0) {
            for (Key key : keySet) {
                filesForDroping.add(key);
            }
            return;
        }
        System.err.println("FileSystemCache: dropping all objects");
        try {
            Files.walkFileTree(basePath, new DirectoryCleaner());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path pathFromKey(Key k) {
        String relativFilePathString = pathTranslator.toFileSystemPath(k);
        Path filePath = basePath.resolve(relativFilePathString);
        return filePath;
    }

    private void registerListener(Key k, SourceListener<Key, byte[]> sender) {
        Set<SourceListener<Key, byte[]>> listenerSet = registredListeners.get(k);
        if (listenerSet == null) {
            listenerSet = new HashSet<SourceListener<Key, byte[]>>();
            registredListeners.put(k, listenerSet);
        }
        listenerSet.add(sender);
    }

    private void callListener(Key k, byte[] data) {
        Set<SourceListener<Key, byte[]>> listenerSet = registredListeners.get(k);
        if (listenerSet == null)
            return;
        for (SourceListener<Key, byte[]> sourceListener : listenerSet) {
            sourceListener.requestCompleted(k, data);
        }
        listenerSet.clear();
    }

    /**
     * Deletes all files inside a Path and its subpaths.
     */
    private class DirectoryCleaner extends SimpleFileVisitor<Path> {

        public DirectoryCleaner() {}

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
            return FileVisitResult.CONTINUE;
        }

        public FileVisitResult postVisitDirectory(Path dir, IOException e)
                throws IOException {
            /*
             * Uncomment to remove directors if (e == null) { Files.delete(dir);
             * 
             * } else { throw e; }
             */
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc)
                throws IOException {
            return FileVisitResult.TERMINATE;
        }
    }

    private class FileIndexerWalker extends SimpleFileVisitor<Path> {

        private Set<Key> fileKeySet;
        private Path basePath;
        private PathTranslator<Key> pathTranslator;


        /**
         * Takes a Set to which it adds the keys of the found files and a basePath to remove from
         * the filesPath.
         * 
         * @param s the set to use
         */
        public FileIndexerWalker(Set<Key> s, Path base, PathTranslator<Key> pt) {
            fileKeySet = s;
            basePath = base;
            pathTranslator = pt;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
            return FileVisitResult.CONTINUE;
        }

        public FileVisitResult postVisitDirectory(Path dir, IOException e)
                throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
            Path rPath = basePath.relativize(file);
            Key key = pathTranslator.fromFileSystemPath(rPath.toString());
            fileKeySet.add(key);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc)
                throws IOException {
            return FileVisitResult.TERMINATE;
        }
    }

    private class FileLoaderRunnable implements Runnable {

        private Key key;


        public FileLoaderRunnable(Key k) {
            this.key = k;
        }

        @Override
        public void run() {
            try {
                byte[] content = Files.readAllBytes(pathFromKey(key));
                callListener(key, content);
            } catch (IOException e) {
                callListener(key, null);
                e.printStackTrace();
            }
            lockedFileSet.remove(key);
            synchronized (FileSystemCache.this) {
                for (Key k : filesForDroping) {
                    System.err.println("FileSystemCache: dropping key " + k);
                    try {
                        Files.deleteIfExists(pathFromKey(k));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
