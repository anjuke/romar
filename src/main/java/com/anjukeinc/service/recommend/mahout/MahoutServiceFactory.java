package com.anjukeinc.service.recommend.mahout;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.common.ClassUtils;

public class MahoutServiceFactory {

    private final String RECOMMENDER_TYPE = "recommender-type";
    private final String SIMILARITY_CLASSNAME = "similarity-classname";
    private final String DEFAULT_SIMILARITY_CLASSNAME = "org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity";
    private final String USER_CF = "UserCF";
    private final String ITEM_CF = "ItemCF";

    public MahoutService getService() {
        String name = System.getProperty(RECOMMENDER_TYPE, ITEM_CF);
        String similarityClassName = System.getProperty(SIMILARITY_CLASSNAME,
                DEFAULT_SIMILARITY_CLASSNAME);
        Recommender recommender;
        DataModel dataModel = new GenericDataModel(
                new FastByIDMap<PreferenceArray>());
        if (USER_CF.endsWith(name)) {
            // FIXME;
            throw new UnsupportedOperationException();
        } else {
            ItemSimilarity similarity = ClassUtils.instantiateAs(
                    similarityClassName, ItemSimilarity.class,
                    new Class<?>[] { DataModel.class },
                    new Object[] { dataModel });

            recommender = new GenericItemBasedRecommender(dataModel, similarity);
        }

        return new RecommenderWrapper(new UpdatableRecommender(recommender));
    }

    static class UpdatableRecommender extends ForwardingRecommender {

        public UpdatableRecommender(Recommender recommender) {
            super(recommender);
        }

        @Override
        public void setPreference(long userID, long itemID, float value)
                throws TasteException {
            PreferenceArray prefsForUser;
            if ((prefsForUser = ((GenericDataModel) recommender.getDataModel())
                    .getPreferencesFromUser(userID)) != null) {
                GenericUserPreferenceArray g = new GenericUserPreferenceArray(
                        prefsForUser.length() + 1);
                ((GenericDataModel) recommender.getDataModel())
                        .getRawUserData().remove(userID);
                g.setUserID(0, userID);
                g.setItemID(0, itemID);
                g.setValue(0, value);
                for (int i = 0; i < prefsForUser.length(); i++) {
                    g.set(i + 1, prefsForUser.get(i));
                }
                prefsForUser = g;
            } else {
                prefsForUser = new GenericUserPreferenceArray(1);
                prefsForUser.setUserID(0, userID);
                prefsForUser.setItemID(0, itemID);
                prefsForUser.setValue(0, value);
            }
            ((GenericDataModel) recommender.getDataModel()).getRawUserData()
                    .put(userID, prefsForUser);
        }

    }
}
