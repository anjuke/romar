package com.anjuke.romar.mahout;

import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

public interface MahoutService extends Recommender{

    List<RecommendedItem> mostSimilarItems(long itemID, int howMany) throws TasteException;

    List<RecommendedItem> mostSimilarItems(long[] itemIDs, int howMany) throws TasteException;

    List<RecommendedItem> mostSimilarItems(long[] itemIDs,
            int howMany,
            boolean excludeItemIfNotSimilarToAll) throws TasteException;


    long[] mostSimilarUserIDs(long userID, int howMany) throws TasteException;

}
