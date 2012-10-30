package com.anjuke.romar.mahout.model;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BDBDataModelTest {
    @Before
    public void setUp() throws TasteException, IOException {
        _bdbDir = BDBTestUtils.createTempDir();
        _dataModel = new BDBDataModel(_bdbDir.getCanonicalPath(), CACHE_SIZE);

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
        BDBTestUtils.deleteRecursively(_bdbDir);
    }

    @Test
    public void testGetPreferenceValue() throws TasteException {
        Assert.assertEquals(Float.valueOf(10), _dataModel.getPreferenceValue(104, 14));
        Assert.assertEquals(Float.valueOf(5), _dataModel.getPreferenceValue(103, 12));
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

    @Test
    public void testRemoveUser() throws TasteException {
        _dataModel.removeUser(104);
        Assert.assertEquals(3, _dataModel.getNumUsers());
        Assert.assertEquals(3, _dataModel.getNumItems());

        _dataModel.removeUser(101);
        Assert.assertEquals(2, _dataModel.getNumUsers());
        Assert.assertEquals(3, _dataModel.getNumItems());
    }

    @Test
    public void testRemoveItem() throws TasteException {
        _dataModel.removeItem(11);
        Assert.assertEquals(3, _dataModel.getNumUsers());
        Assert.assertEquals(3, _dataModel.getNumItems());

        _dataModel.removeItem(14);
        Assert.assertEquals(3, _dataModel.getNumUsers());
        Assert.assertEquals(2, _dataModel.getNumItems());
    }

    @Test
    public void testRecommender() throws TasteException, IOException {
        File dir = BDBTestUtils.createTempDir();
        try {
            BDBDataModel model = new BDBDataModel(dir.getAbsolutePath(), CACHE_SIZE);

            // following data from http://manning.com/owen/ intro.csv
            model.setMinPreference(0);
            model.setMaxPreference(5);

            model.setPreference(1, 101, 5.0F);
            model.setPreference(1, 102, 3.0F);
            model.setPreference(1, 103, 2.5F);

            model.setPreference(2, 101, 2.0F);
            model.setPreference(2, 102, 2.5F);
            model.setPreference(2, 103, 5.0F);
            model.setPreference(2, 104, 2.0F);

            model.setPreference(3, 101, 2.5F);
            model.setPreference(3, 104, 4.0F);
            model.setPreference(3, 105, 4.5F);
            model.setPreference(3, 107, 5.0F);

            model.setPreference(4, 101, 5.5F);
            model.setPreference(4, 103, 3.0F);
            model.setPreference(4, 104, 4.5F);
            model.setPreference(4, 106, 4.0F);

            model.setPreference(5, 101, 4.0F);
            model.setPreference(5, 102, 3.0F);
            model.setPreference(5, 103, 2.0F);
            model.setPreference(5, 104, 4.0F);
            model.setPreference(5, 105, 3.5F);
            model.setPreference(5, 106, 4.0F);

            RecommendedItem ri1 = recommenderIntro(model);
            _logger.info("Recommender: with new data model: " + ri1);
            Assert.assertEquals(104, ri1.getItemID());
            Assert.assertEquals(4.257081, ri1.getValue(), 0.001);
            model.close();

            BDBDataModel newModel = new BDBDataModel(dir.getAbsolutePath(), CACHE_SIZE);
            _logger.info("Recommender: with exist data model: " + ri1);
            RecommendedItem ri2 = recommenderIntro(newModel);
            Assert.assertEquals(ri1, ri2);
            newModel.close();
        } finally {
            BDBTestUtils.deleteRecursively(dir);
        }
    }

    private RecommendedItem recommenderIntro(DataModel model) throws TasteException {
        // following code from http://manning.com/owen/ RecommenderIntro.java

        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood =
                new NearestNUserNeighborhood(2, similarity, model);

        Recommender recommender = new GenericUserBasedRecommender(
                model, neighborhood, similarity);

        List<RecommendedItem> recommendations =
                recommender.recommend(1, 1);

//        for (RecommendedItem recommendation : recommendations) {
//                System.out.println(recommendation);
//        }

        return recommendations.get(0);

        //[item:104, value:4.257081]
    }

    //
    //
    //

    private File _bdbDir;
    private BDBDataModel _dataModel;
    protected static final long CACHE_SIZE = 1024 * 1024 * 32;

    private final static Logger _logger = LoggerFactory.getLogger(
            BDBDataModelTest.class);
}
