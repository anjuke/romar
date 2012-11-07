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
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class ForwardingRecommender implements Recommender {

    private Recommender _recommender;

    public ForwardingRecommender(Recommender recommender) {
        _recommender = recommender;
    }

    public List<RecommendedItem> recommend(long userID, int howMany)
            throws TasteException {
        return _recommender.recommend(userID, howMany);
    }

    public List<RecommendedItem> recommend(long userID, int howMany,
            IDRescorer rescorer) throws TasteException {
        return _recommender.recommend(userID, howMany, rescorer);
    }

    public float estimatePreference(long userID, long itemID)
            throws TasteException {
        return _recommender.estimatePreference(userID, itemID);
    }

    public void setPreference(long userID, long itemID, float value)
            throws TasteException {
        _recommender.setPreference(userID, itemID, value);
    }

    public void removePreference(long userID, long itemID)
            throws TasteException {
        _recommender.removePreference(userID, itemID);
    }

    public DataModel getDataModel() {
        return _recommender.getDataModel();
    }

    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        _recommender.refresh(alreadyRefreshed);
    }
}
