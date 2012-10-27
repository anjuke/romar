package com.anjuke.romar.stringid;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BdbTransformerTest {

    @Before
    public void setUp() {
        _bdbDir = createTempDir();
        _transformer = new BdbStringIdTransformer(_bdbDir.getAbsolutePath(), CACHE_SIZE);
    }

    @After
    public void TearDown() throws IOException {
        _transformer.Close();
        deleteRecursively(_bdbDir);
    }

    @Test
    public void differentStringShouldTransformedToDifferentId() {
        final int COUNT = 10;
        Set<Long> idSet = new HashSet<Long>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            long id = _transformer.transform("Anjuke - " + i);
            idSet.add(id);
        }
        Assert.assertEquals(COUNT, idSet.size());
    }

    @Test
    public void sameStringShouldTransformedToSameId() {
        final int COUNT = 10;

        Set<Long> idSet = new HashSet<Long>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            long id = _transformer.transform("Anjuke - *");
            idSet.add(id);
        }
        Assert.assertEquals(1, idSet.size());
    }

    @Test
    public void transformNotExistIdShouldReturnNull() {
        Assert.assertNull(_transformer.transform(37));
        Random random = new Random(System.currentTimeMillis());
        Assert.assertNull(_transformer.transform(random.nextLong()));
    }

    @Test
    public void transformExistIdShouldReturnCorrectString() {
        final int COUNT = 10;
        Map<Long, String> map = new HashMap<Long, String>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            String stringId = "Anjuke - " + i;
            long id = _transformer.transform(stringId);
            map.put(id, stringId);
        }
        // we assume the above transform from string to id is correct

        for (Map.Entry<Long, String> entry : map.entrySet()) {
            String stringId = _transformer.transform(entry.getKey());
            Assert.assertEquals(entry.getValue(), stringId);
        }
    }

    @Test
    public void benchmark() {
        final int COUNT = 10000;

        long t1 = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            _transformer.transform("Anjuke - " + i);
        }
        long t2 = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            _transformer.transform(i + 1);
        }
        long t3 = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            _transformer.transform("Anjuke - " + i);
        }
        long t4 = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            _transformer.transform(COUNT + i + 1);
        }
        long t5 = System.currentTimeMillis();

        System.out.println("COUNT:" + COUNT + ", total time: " + (t5 - t1) / 1000.0);
        System.out.println(String.format("not exist string: around %d transform per second", Math.round((double)COUNT / (t2 - t1) * 1000)));
        System.out.println(String.format("    exist id:     around %d transform per second", Math.round((double)COUNT / (t3 - t2) * 1000)));
        System.out.println(String.format("    exist string: around %d transform per second", Math.round((double)COUNT / (t4 - t3) * 1000)));
        System.out.println(String.format("not exist id:     around %d transform per second", Math.round((double)COUNT / (t5 - t4) * 1000)));
    }

    private File _bdbDir;
    private BdbStringIdTransformer _transformer;
    private static final long CACHE_SIZE = 1024 * 1024 * 32;

    //
    //
    //

    /** Maximum loop count when creating temp directories. */
    private static final int TEMP_DIR_ATTEMPTS = 10000;

    private static File createTempDir() {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
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

    public static void deleteDirectoryContents(File directory)
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

    public static void deleteRecursively(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectoryContents(file);
        }
        if (!file.delete()) {
            throw new IOException("Failed to delete " + file);
        }
    }
}
