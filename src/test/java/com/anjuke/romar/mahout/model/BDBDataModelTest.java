package com.anjuke.romar.mahout.model;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BDBDataModelTest {
    @Before
    public void setUp() throws TasteException {
        _bdbDir = createTempDir();
        _dataModel = new BDBDataModel(_bdbDir.getAbsolutePath(), CACHE_SIZE);

        _dataModel.setPreference(101, 11, 1);
        _dataModel.setPreference(102, 11, 2);
        _dataModel.setPreference(102, 12, 3);
        _dataModel.setPreference(103, 11, 4);
        _dataModel.setPreference(103, 12, 5);
        _dataModel.setPreference(103, 13, 6);
        _dataModel.setPreference(104, 11, 7);
        _dataModel.setPreference(104, 12, 8);
        _dataModel.setPreference(104, 13, 9);
        _dataModel.setPreference(104, 14, 10);
    }

    @After
    public void TearDown() throws IOException {
        _dataModel.close();
        deleteRecursively(_bdbDir);
    }

    @Test
    public void testGetPreferenceValue() throws TasteException {
        Assert.assertEquals(Float.valueOf(10),  _dataModel.getPreferenceValue(104, 14));
        Assert.assertEquals(Float.valueOf(5),  _dataModel.getPreferenceValue(103, 12));
        Assert.assertEquals(Float.valueOf(2), _dataModel.getPreferenceValue(102, 11));
    }

    @Test
    public void getNonExistPreferenceValueShouldReturnNull() throws TasteException {
        Assert.assertNull(_dataModel.getPreferenceValue(1, 2));
        Assert.assertNull(_dataModel.getPreferenceValue(2, 0));
    }

    @Test
    public void testGetPreferencesFromUser() throws TasteException {
        PreferenceArray array = _dataModel.getPreferencesFromUser(101);
        Assert.assertEquals(1, array.length());

        array = _dataModel.getPreferencesFromUser(104);
        Assert.assertEquals(4, array.length());
    }

    @Test
    public void testGetPreferencesForItem() throws TasteException {
        PreferenceArray array = _dataModel.getPreferencesForItem(11);
        Assert.assertEquals(4, array.length());

        array = _dataModel.getPreferencesForItem(13);
        Assert.assertEquals(2, array.length());
    }

    @Test
    public void testGetNumUsersWithPreferenceFor() throws TasteException {
        Assert.assertEquals(4, _dataModel.getNumUsersWithPreferenceFor(11));
        Assert.assertEquals(2, _dataModel.getNumUsersWithPreferenceFor(13));

        Assert.assertEquals(3, _dataModel.getNumUsersWithPreferenceFor(11, 12));
        Assert.assertEquals(2, _dataModel.getNumUsersWithPreferenceFor(12, 13));
    }

    @Test
    public void testGetItemIDsFromUser() throws TasteException {
        Assert.assertEquals(1, _dataModel.getItemIDsFromUser(101).size());
        Assert.assertEquals(4, _dataModel.getItemIDsFromUser(104).size());
    }

    @Test
    public void testGetUserIDs() throws TasteException {
        int count = 0;
        LongPrimitiveIterator iterator = _dataModel.getUserIDs();
        while (iterator.hasNext()) {
            long userID = iterator.nextLong();
            if (userID >= 101 && userID <=104) {
                count++;
            }
        }
        Assert.assertEquals(4, count);
    }

    @Test
    public void testGetItemIDs() throws TasteException {
        int count = 0;
        LongPrimitiveIterator iterator = _dataModel.getItemIDs();
        while (iterator.hasNext()) {
            long itemID = iterator.nextLong();
            if (itemID >= 11 && itemID <=14) {
                count++;
            }
        }
        Assert.assertEquals(4, count);
    }

    @Test
    public void testGetNumUsers() throws TasteException {
        Assert.assertEquals(4, _dataModel.getNumUsers());

        _dataModel.setPreference(104, 14, 0);
        Assert.assertEquals(4, _dataModel.getNumUsers());

        _dataModel.setPreference(104, 15, 0);
        Assert.assertEquals(4, _dataModel.getNumUsers());

        _dataModel.setPreference(105, 15, 0);
        Assert.assertEquals(5, _dataModel.getNumUsers());

        _dataModel.removePreference(101, 11);
        Assert.assertEquals(4, _dataModel.getNumUsers());
    }

    @Test
    public void testGetNumItems() throws TasteException {
        Assert.assertEquals(4, _dataModel.getNumItems());

        _dataModel.setPreference(104, 14, 0);
        Assert.assertEquals(4, _dataModel.getNumItems());

        _dataModel.setPreference(104, 15, 0);
        Assert.assertEquals(5, _dataModel.getNumItems());

        _dataModel.setPreference(105, 15, 0);
        Assert.assertEquals(5, _dataModel.getNumItems());

        _dataModel.removePreference(104, 14);
        Assert.assertEquals(4, _dataModel.getNumItems());
    }

    //
    //
    //

    private File _bdbDir;
    private BDBDataModel _dataModel;
    protected static final long CACHE_SIZE = 1024 * 1024 * 32;

    private final static Logger _logger = LoggerFactory.getLogger(
            BDBDataModelTest.class);

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
