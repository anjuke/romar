package com.anjuke.romar.mahout;

import static org.junit.Assert.*;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CachingItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.junit.Test;


public class GenericReloadDataModelTest {
    @Test
    public void test() throws TasteException{
        GenericReloadDataModel model = new GenericReloadDataModel();
        ItemBasedRecommender recommender = new GenericItemBasedRecommender(
                model, new CachingItemSimilarity(new TanimotoCoefficientSimilarity(model),100));
        recommender.setPreference(1, 1, 5f);
        recommender.setPreference(1, 2, 1f);
        recommender.setPreference(2, 1, 1f);
        recommender.setPreference(2, 2, 5f);
        recommender.refresh(null);
        assertEquals(0,recommender.recommend(1, 5).size());
        recommender.setPreference(2, 3, 1f);
        recommender.refresh(null);
        assertEquals(1,recommender.recommend(1, 5).size());
        recommender.setPreference(2, 4, 1f);
        recommender.refresh(null);
        assertEquals(2,recommender.recommend(1, 5).size());
        assertEquals(2,recommender.recommend(1, 5).size());
    }
}
