package com.anjuke.romar.mahout.factory;

import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CachingUserSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.ClassUtils;

import com.anjuke.romar.core.RomarConfig;
import com.anjuke.romar.mahout.GenericReloadDataModel;
import com.anjuke.romar.mahout.MahoutService;
import com.anjuke.romar.mahout.RecommenderWrapper;

public class MahoutServiceUserRecommendFactory
        extends AbstractMahoutServiceFactory  implements MahoutServiceFactory {
    @Override
    public MahoutService createService() {
        RomarConfig config = RomarConfig.getInstance();
        Recommender recommender;
        DataModel dataModel = wrapDataModel(new GenericReloadDataModel());
        UserSimilarity similarity = ClassUtils.instantiateAs(
                config.getUserSimilarityClass(), UserSimilarity.class,
                new Class<?>[] {DataModel.class}, new Object[] {dataModel});
        if (config.isUseSimilariyCache()){
            similarity = new CachingUserSimilarity(similarity,
                    config.getSimilarityCacheSize());
        }
        UserNeighborhood neighborhood = ClassUtils.instantiateAs(
                config.getUserNeighborhoodClass(), UserNeighborhood.class,
                new Class<?>[] {int.class, UserSimilarity.class,
                        DataModel.class},
                new Object[] {config.getUserNeighborhoodNearestN(),
                        similarity, dataModel});
        recommender = new GenericUserBasedRecommender(dataModel, neighborhood,
                similarity);

        return new RecommenderWrapper(recommender);
    }
}
