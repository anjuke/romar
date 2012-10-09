package com.anjukeinc.service.recommend.mahout;

import java.util.Collection;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class ForwardingRecommender implements Recommender {

    protected Recommender recommender;

    public ForwardingRecommender(Recommender recommender) {
        this.recommender = recommender;
    }

    public List<RecommendedItem> recommend(long userID, int howMany)
            throws TasteException {
        return recommender.recommend(userID, howMany);
    }

    public List<RecommendedItem> recommend(long userID, int howMany,
            IDRescorer rescorer) throws TasteException {
        return recommender.recommend(userID, howMany, rescorer);
    }

    public float estimatePreference(long userID, long itemID)
            throws TasteException {
        return recommender.estimatePreference(userID, itemID);
    }

    public void setPreference(long userID, long itemID, float value)
            throws TasteException {
        recommender.setPreference(userID, itemID, value);
    }

    public void removePreference(long userID, long itemID)
            throws TasteException {
        recommender.removePreference(userID, itemID);
    }

    public DataModel getDataModel() {
        return recommender.getDataModel();
    }

    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        recommender.refresh(alreadyRefreshed);
    }
}
