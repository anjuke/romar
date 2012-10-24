package com.anjuke.romar.mahout.factory;

import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.common.ClassUtils;

import com.anjuke.romar.core.RomarConfig;
import com.anjuke.romar.mahout.GenericReloadDataModel;
import com.anjuke.romar.mahout.MahoutService;
import com.anjuke.romar.mahout.RecommenderWrapper;

public class MahoutServiceCommonRecommendFactory extends AbstractMahoutServiceFactory implements MahoutServiceFactory {

    @Override
    public MahoutService createService() {
        RomarConfig config = RomarConfig.getInstance();
        Recommender recommender;
        DataModel dataModel =wrapDataModel( new GenericReloadDataModel());

        recommender = ClassUtils.instantiateAs(
                config.getCommonRecommenderClass(), Recommender.class,
                new Class<?>[] { DataModel.class }, new Object[] { dataModel });
        return new RecommenderWrapper(recommender);
    }

}
