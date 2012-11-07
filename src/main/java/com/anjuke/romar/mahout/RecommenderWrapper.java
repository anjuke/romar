/**
 * Copyright 2012 Anjuke Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.anjuke.romar.mahout;

import java.util.Collection;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;

public class RecommenderWrapper implements MahoutService {
    private final Recommender _recommender;

    public RecommenderWrapper(Recommender recommender) {
        super();
        _recommender = recommender;
    }

    @Override
    public List<RecommendedItem> recommend(long userID, int howMany)
            throws TasteException {
        return _recommender.recommend(userID, howMany);
    }

    @Override
    public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer)
            throws TasteException {
        return _recommender.recommend(userID, howMany, rescorer);
    }

    @Override
    public float estimatePreference(long userID, long itemID) throws TasteException {
        return _recommender.estimatePreference(userID, itemID);
    }

    @Override
    public void setPreference(long userID, long itemID, float value)
            throws TasteException {
        _recommender.setPreference(userID, itemID, value);
    }

    @Override
    public void removePreference(long userID, long itemID) throws TasteException {
        _recommender.removePreference(userID, itemID);
    }

    @Override
    public DataModel getDataModel() {
        return _recommender.getDataModel();
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        _recommender.refresh(alreadyRefreshed);
    }

    @Override
    public List<RecommendedItem> mostSimilarItems(long itemID, int howMany)
            throws TasteException {
        if (_recommender instanceof ItemBasedRecommender) {
            return ((ItemBasedRecommender) _recommender)
                    .mostSimilarItems(itemID, howMany);
        } else {
            throw new UnsupportedOperationException("ItemBasedRecommender not supported");
        }
    }

    @Override
    public List<RecommendedItem> mostSimilarItems(long[] itemIDs, int howMany)
            throws TasteException {
        if (_recommender instanceof ItemBasedRecommender) {
            return ((ItemBasedRecommender) _recommender).mostSimilarItems(itemIDs,
                    howMany);
        } else {
            throw new UnsupportedOperationException("ItemBasedRecommender not supported");
        }
    }

    @Override
    public List<RecommendedItem> mostSimilarItems(long[] itemIDs, int howMany,
            boolean excludeItemIfNotSimilarToAll) throws TasteException {
        if (_recommender instanceof ItemBasedRecommender) {
            return ((ItemBasedRecommender) _recommender).mostSimilarItems(itemIDs,
                    howMany, excludeItemIfNotSimilarToAll);
        } else {
            throw new UnsupportedOperationException("ItemBasedRecommender not supported");
        }
    }

    @Override
    public long[] mostSimilarUserIDs(long userID, int howMany) throws TasteException {
        if (_recommender instanceof UserBasedRecommender) {
            return ((UserBasedRecommender) _recommender).mostSimilarUserIDs(userID,
                    howMany);
        } else {
            throw new UnsupportedOperationException("UserBasedRecommender not supported");
        }
    }

    @Override
    public void removeUser(long userID) throws TasteException {
        DataModel dataModel = _recommender.getDataModel();
        for (long itemID : dataModel.getItemIDsFromUser(userID)) {
            removePreference(userID, itemID);
        }
    }

    @Override
    public void removeItem(long itemID) throws TasteException {
        DataModel dataModel = _recommender.getDataModel();
        for (Preference p : dataModel.getPreferencesForItem(itemID)) {
            removePreference(p.getUserID(), itemID);
        }
    }
}
