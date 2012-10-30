package com.anjuke.romar.mahout.model;

import java.io.File;
import java.io.IOException;

/**
 * @see com.google.common.io.Files
 */
class BDBTestUtils {

    /** Maximum loop count when creating temp directories. */
    private static final int TEMP_DIR_ATTEMPTS = 10000;

    static File createTempDir() throws IOException {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        baseDir = new File(baseDir.getCanonicalPath());
        String baseName = System.currentTimeMillis() + "-";

        for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
            File tempDir = new File(baseDir, baseName + counter);
            if (tempDir.mkdir()) {
                return tempDir;
            }
        }
        throw new IllegalStateException("Failed to create directory within "
                + TEMP_DIR_ATTEMPTS + " attempts (tried "
                + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
    }

    static void deleteDirectoryContents(File directory)
            throws IOException {
        if (!directory.getCanonicalPath().equals(directory.getAbsolutePath())) {
            // return;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            throw new IOException("Error listing files for " + directory);
        }
        for (File file : files) {
            deleteRecursively(file);
        }
    }

    static void deleteRecursively(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectoryContents(file);
        }
        if (!file.delete()) {
            throw new IOException("Failed to delete " + file);
        }
    }
}
