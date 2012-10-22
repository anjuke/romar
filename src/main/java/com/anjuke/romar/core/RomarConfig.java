package com.anjuke.romar.core;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.ItemAverageRecommender;
import org.apache.mahout.cf.taste.impl.recommender.ItemUserAverageRecommender;
import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.SpearmanCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.yaml.snakeyaml.Yaml;

import com.anjuke.romar.mahout.factory.MahoutServiceFactory;
import com.anjuke.romar.mahout.factory.MahoutServiceItemRecommendFactory;
import com.anjuke.romar.mahout.factory.MahoutServiceCommonRecommendFactory;
import com.anjuke.romar.mahout.factory.MahoutServiceUserRecommendFactory;

public class RomarConfig {

    private static final RomarConfig instance;

    private static final String CONF_PATH_KEY = "romar.config";

    private static class RomarConfigHolder {
        public RecommendType recommendType;
        public boolean useSimilarityCache;
        public int similarityCacheSize;
        public ItemSimilarityClass itemSimilarityClass;
        public UserSimilarityClass userSimilarityClass;
        public UserNeighborhoodClass userNeighborhoodClass;
        public int userNeighborhoodNearestN;
        public CommonRecommenderClass commonRecommenderClass;
    }

    static {
        Yaml yaml = new Yaml();
        String path = System.getProperty(CONF_PATH_KEY);

        InputStream is = null;
        try {
            if (path == null || path.isEmpty()) {
                is = RomarConfig.class.getResourceAsStream("/romar.yaml");
            } else {
                is = new FileInputStream(path);
            }

            RomarConfigHolder holder = yaml.loadAs(is, RomarConfigHolder.class);
            instance = new RomarConfig(holder);

        } catch (Exception e) {
            throw new Error(e);
        } finally {
            if (is != null) {
                try {
                    // is.close();
                } catch (Exception e) {

                }
            }
        }
    }

    public static RomarConfig getInstance() {
        return instance;
    }

    private enum RecommendType {
        item(new MahoutServiceItemRecommendFactory()), user(
                new MahoutServiceUserRecommendFactory()),common(new MahoutServiceCommonRecommendFactory());

        private MahoutServiceFactory factory;

        private RecommendType(MahoutServiceFactory factory) {
            this.factory = factory;
        }

    }

    private enum ItemSimilarityClass {
        EuclideanDistanceSimilarity(EuclideanDistanceSimilarity.class), PearsonCorrelationSimilarity(
                PearsonCorrelationSimilarity.class), UncenteredCosineSimilarity(
                UncenteredCosineSimilarity.class), CityBlockSimilarity(
                CityBlockSimilarity.class), LogLikelihoodSimilarity(
                LogLikelihoodSimilarity.class), TanimotoCoefficientSimilarity(
                TanimotoCoefficientSimilarity.class);

        private Class<? extends ItemSimilarity> clazz;

        private ItemSimilarityClass(Class<? extends ItemSimilarity> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends ItemSimilarity> getClazz() {
            return clazz;
        }

    }

    private enum UserSimilarityClass {
        EuclideanDistanceSimilarity(EuclideanDistanceSimilarity.class), PearsonCorrelationSimilarity(
                PearsonCorrelationSimilarity.class), UncenteredCosineSimilarity(
                UncenteredCosineSimilarity.class), CityBlockSimilarity(
                CityBlockSimilarity.class), LogLikelihoodSimilarity(
                LogLikelihoodSimilarity.class), TanimotoCoefficientSimilarity(
                TanimotoCoefficientSimilarity.class), SpearmanCorrelationSimilarity(
                SpearmanCorrelationSimilarity.class);

        private Class<? extends UserSimilarity> clazz;

        private UserSimilarityClass(Class<? extends UserSimilarity> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends UserSimilarity> getClazz() {
            return clazz;
        }

    }

    private enum UserNeighborhoodClass {
        NearestNUserNeighborhood(NearestNUserNeighborhood.class), ThresholdUserNeighborhood(
                ThresholdUserNeighborhood.class);

        private Class<? extends UserNeighborhood> clazz;

        private UserNeighborhoodClass(Class<? extends UserNeighborhood> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends UserNeighborhood> getClazz() {
            return clazz;
        }
    }

    private enum CommonRecommenderClass {
        ItemAverageRecommender(ItemAverageRecommender.class), ItemUserAverageRecommender(
                ItemUserAverageRecommender.class), SlopeOneRecommender(
                SlopeOneRecommender.class);
        private Class<? extends Recommender> clazz;

        private CommonRecommenderClass(Class<? extends Recommender> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends Recommender> getClazz() {
            return clazz;
        }
    }

    private final RomarConfigHolder holder;

    private RomarConfig(RomarConfigHolder holder) {
        this.holder = holder;
    }

    public MahoutServiceFactory getMahoutServiceFactory() {
        return holder.recommendType.factory;
    }

    public boolean isUseSimilariyCache() {
        return holder.useSimilarityCache;
    }

    public int getSimilarityCacheSize() {
        return holder.similarityCacheSize;
    }

    public Class<? extends ItemSimilarity> getItemSimilarityClass() {
        return holder.itemSimilarityClass.getClazz();
    }

    public Class<? extends UserSimilarity> getUserSimilarityClass() {
        return holder.userSimilarityClass.getClazz();
    }

    public Class<? extends UserNeighborhood> getUserNeighborhoodClass() {
        return holder.userNeighborhoodClass.getClazz();
    }

    public int getUserNeighborhoodNearestN() {
        return holder.userNeighborhoodNearestN;
    }

    public Class<? extends Recommender> getCommonRecommenderClass(){
        return holder.commonRecommenderClass.getClazz();
    }

    public static void main(String[] args) {
        System.out.println(RomarConfig.getInstance());
    }

}
