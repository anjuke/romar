package com.anjuke.romar.mahout;

import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CachingItemSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.common.ClassUtils;

public class MahoutServiceFactory {

    private final String RECOMMENDER_TYPE = "recommender-type";
    private final String SIMILARITY_CLASSNAME = "similarity-classname";
    private final String SIMILARITY_USE_CACHE = "similarity-use-cache";
    private final String DEFAULT_SIMILARITY_CLASSNAME = "org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity";
    private final String USER_CF = "UserCF";
    private final String ITEM_CF = "ItemCF";

    public MahoutService getService(){
        String name = System.getProperty(RECOMMENDER_TYPE, ITEM_CF);
        String similarityClassName = System.getProperty(SIMILARITY_CLASSNAME,
                DEFAULT_SIMILARITY_CLASSNAME);
        boolean useSimilarityCaching=Boolean.parseBoolean(System.getProperty(SIMILARITY_USE_CACHE,"true"));


        Recommender recommender;
        DataModel dataModel =new GenericReloadDataModel();
        if (USER_CF.endsWith(name)) {
            // FIXME;
            throw new UnsupportedOperationException();
        } else {
            ItemSimilarity similarity = ClassUtils.instantiateAs(
                    similarityClassName, ItemSimilarity.class,
                    new Class<?>[] { DataModel.class },
                    new Object[] { dataModel });
            if(useSimilarityCaching)
                similarity=new CachingItemSimilarity(similarity, 1024*10);
            recommender = new GenericItemBasedRecommender(dataModel, similarity);
        }

        return new RecommenderWrapper(recommender);
    }
}
