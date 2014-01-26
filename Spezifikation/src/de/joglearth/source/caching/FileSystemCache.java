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

import de.joglearth.source.ProgressManager;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;


/**
 * Implements the {@link Cache} interface in such way that it stores objects in the file system.
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
     * Constructor.Initializes a {@link FileSystemCache} as it assigns values to the
     * <code>folder</code> value and the {@link PathTranslator}.
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
        try {
            Files.walkFileTree(basePath, new FileIndexerWalker(keySet, basePath, pathTranslator));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SourceResponse<byte[]> requestObject(Key key,
            SourceListener<Key, byte[]> sender) {
        SourceResponseType responseType;
        synchronized (keySet) {

            if (keySet.contains(key)) {
                synchronized (lockedFileSet) {
                    lockedFileSet.add(key);
                }
                registerListener(key, sender);
                responseType = SourceResponseType.ASYNCHRONOUS;
                ProgressManager.getInstance().requestArrived();
                executorService.execute(new FileLoaderRunnable(key));
            } else {
                responseType = SourceResponseType.MISSING;
            }
        }
        return new SourceResponse<byte[]>(responseType, null);
    }

    @Override
    public void putObject(Key k, byte[] v) {
        ProgressManager.getInstance().requestArrived();
        executorService.execute(new FileWriterRunnable(k, v));
    }

    @Override
    public void dropObject(Key k) {
        synchronized (keySet) {
            keySet.remove(k);
        }

        if (lockedFileSet.contains(k)) {
            synchronized (lockedFileSet) {
                filesForDroping.add(k);
            }
            return;
        }

        executorService.execute(new DeleteIfExistsRunnable(k));
    }

    @Override
    public Iterable<Key> getExistingObjects() {
        synchronized (keySet) {

            return new HashSet<Key>(keySet);
        }
    }

    @Override
    public void dropAll() {
        synchronized (keySet) {
            keySet.clear();
        }

        if (lockedFileSet.size() > 0) {
            synchronized (lockedFileSet) {
                for (Key key : keySet) {
                    filesForDroping.add(key);
                }
            }
            return;
        } else {
            try {
                Files.walkFileTree(basePath, new DirectoryCleaner());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private Path pathFromKey(Key k) {
        String relativFilePathString = pathTranslator.toFileSystemPath(k);
        Path filePath = basePath.resolve(relativFilePathString);
        return filePath;
    }

    private void registerListener(Key k, SourceListener<Key, byte[]> sender) {
        synchronized (registredListeners) {

            Set<SourceListener<Key, byte[]>> listenerSet = registredListeners.get(k);
            if (listenerSet == null) {
                listenerSet = new HashSet<SourceListener<Key, byte[]>>();
                registredListeners.put(k, listenerSet);
            }
            listenerSet.add(sender);
        }
    }

    private void callListener(Key k, byte[] data) {
        Set<SourceListener<Key, byte[]>> listenerSet;
        synchronized (registredListeners) {
            listenerSet = registredListeners.remove(k);
        }
        if (listenerSet == null)
            return;
        for (SourceListener<Key, byte[]> sourceListener : listenerSet) {
            sourceListener.requestCompleted(k, data);
        }
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
         * @param s The set to use
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
            synchronized (lockedFileSet) {
                lockedFileSet.remove(key);
                for (Key k : filesForDroping) {
                    keySet.remove(k);
                    if (!lockedFileSet.contains(k)) {
                        try {
                            Files.deleteIfExists(pathFromKey(k));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            ProgressManager.getInstance().requestCompleted();
        }
    }

    private class FileWriterRunnable implements Runnable {

        private Key key;
        private byte[] data;


        public FileWriterRunnable(Key key, byte[] data) {
            this.key = key;
            this.data = data;
        }

        @Override
        public void run() {
            try {
                Path filePath = pathFromKey(key);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, data, StandardOpenOption.CREATE);
                synchronized (keySet) {
                    keySet.add(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressManager.getInstance().requestCompleted();
        }
    }

    private class DeleteIfExistsRunnable implements Runnable {

        private Key key;


        public DeleteIfExistsRunnable(Key k) {
            key = k;
        }

        @Override
        public void run() {
            try {
                Files.deleteIfExists(pathFromKey(key));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void dispose() {
        executorService.shutdown();
    }

    /**
     * Determines the size of a certain Key.
     * 
     * @param k The Key object
     * @return The size of the object
     */
    public int sizeOf(Key k) {
        Path filePath = pathFromKey(k);
        int size = 0;
        try {
            size = (int) Files.size(filePath);
        } catch (IOException e) {}
        return size;
    }
}
