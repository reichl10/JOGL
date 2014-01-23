package de.joglearth.util;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Helper class for accessing the OS-specific storage of application data.
 */
public final class ApplicationData {
    
    private static String dataRootDirectory = buildRootPath();

    private static String buildRootPath() {
        String folderName = "joglearth";
        String os = System.getProperty("os.name");
        if (os.contains("Windows")) {
            String localAppdata = System.getenv("LOCALAPPDATA");
            return localAppdata + File.separator + folderName + File.separator;
        } else if (os.contains("Linux")) {
            String userHome = System.getProperty("user.home");
            return userHome + File.separator + "." + folderName + File.separator;
        } else {
            throw new RuntimeException("Unable to determine application data directory: " 
                    + "Operating system not supported");
        }
    }
    
    /**
     * Returns the application data root directory
     * 
     * @return The directory
     */
    public static String getDirectory() {
        return getDirectory("");
    }
    
    /**
     * Returns a sub-directory of the application data root
     * 
     * @param subPath The relative sub-directory
     * @return The sub-directory
     */
    public static String getDirectory(String subPath) {
        String[] hierarchy = subPath.split(Pattern.quote(File.separator));
        String absolutePath = dataRootDirectory;
        for (String folder : hierarchy) {
            if (folder.length() > 0) {
                absolutePath = absolutePath + folder + File.separator;
            }
        }
        File directory = new File(absolutePath);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("Creating application data directory " + absolutePath
                    + " failed");
        }
        return absolutePath;
    }
}
