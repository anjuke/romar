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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.anjuke.romar.mahout.factory.MahoutServiceCommonRecommendFactory;
import com.anjuke.romar.mahout.factory.MahoutServiceFactory;
import com.anjuke.romar.mahout.factory.MahoutServiceItemRecommendFactory;
import com.anjuke.romar.mahout.factory.MahoutServiceUserRecommendFactory;

public final class RomarConfig {

    private static final Logger LOG = LoggerFactory
            .getLogger(RomarConfig.class);

    private static final RomarConfig INSTANCE;

    private static final String CONF_PATH_KEY = "romar.config";

    public static class RomarConfigHolder {
        private RecommendType _recommendType;
        private boolean _useSimilarityCache;
        private int _similarityCacheSize;
        private ItemSimilarityClass _itemSimilarityClass;
        private UserSimilarityClass _userSimilarityClass;
        private UserNeighborhoodClass _userNeighborhoodClass;
        private int _userNeighborhoodNearestN;
        private CommonRecommenderClass _commonRecommenderClass;
        private String _persistencePath;

        public RecommendType getRecommendType() {
            return _recommendType;
        }

        public void setRecommendType(RecommendType recommendType) {
            _recommendType = recommendType;
        }

        public boolean isUseSimilarityCache() {
            return _useSimilarityCache;
        }

        public void setUseSimilarityCache(boolean useSimilarityCache) {
            _useSimilarityCache = useSimilarityCache;
        }

        public int getSimilarityCacheSize() {
            return _similarityCacheSize;
        }

        public void setSimilarityCacheSize(int similarityCacheSize) {
            _similarityCacheSize = similarityCacheSize;
        }

        public ItemSimilarityClass getItemSimilarityClass() {
            return _itemSimilarityClass;
        }

        public void setItemSimilarityClass(
                ItemSimilarityClass itemSimilarityClass) {
            _itemSimilarityClass = itemSimilarityClass;
        }

        public UserSimilarityClass getUserSimilarityClass() {
            return _userSimilarityClass;
        }

        public void setUserSimilarityClass(
                UserSimilarityClass userSimilarityClass) {
            _userSimilarityClass = userSimilarityClass;
        }

        public UserNeighborhoodClass getUserNeighborhoodClass() {
            return _userNeighborhoodClass;
        }

        public void setUserNeighborhoodClass(
                UserNeighborhoodClass userNeighborhoodClass) {
            _userNeighborhoodClass = userNeighborhoodClass;
        }

        public int getUserNeighborhoodNearestN() {
            return _userNeighborhoodNearestN;
        }

        public void setUserNeighborhoodNearestN(int userNeighborhoodNearestN) {
            _userNeighborhoodNearestN = userNeighborhoodNearestN;
        }

        public CommonRecommenderClass getCommonRecommenderClass() {
            return _commonRecommenderClass;
        }

        public void setCommonRecommenderClass(
                CommonRecommenderClass commonRecommenderClass) {
            _commonRecommenderClass = commonRecommenderClass;
        }

        public String getPersistencePath() {
            return _persistencePath;
        }

        public void setPersistencePath(String persistencePath) {
            _persistencePath = persistencePath;
        }
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
            INSTANCE = new RomarConfig(holder);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    public static RomarConfig getInstance() {
        return INSTANCE;
    }

    private enum RecommendType {
        item(new MahoutServiceItemRecommendFactory()),
        user(new MahoutServiceUserRecommendFactory()),
        common(new MahoutServiceCommonRecommendFactory());

        private MahoutServiceFactory _factory;

        private RecommendType(MahoutServiceFactory factory) {
            _factory = factory;
        }

    }

    private enum ItemSimilarityClass {
        EuclideanDistanceSimilarity(EuclideanDistanceSimilarity.class),
        PearsonCorrelationSimilarity(PearsonCorrelationSimilarity.class),
        UncenteredCosineSimilarity(UncenteredCosineSimilarity.class),
        CityBlockSimilarity(CityBlockSimilarity.class),
        LogLikelihoodSimilarity(LogLikelihoodSimilarity.class),
        TanimotoCoefficientSimilarity(TanimotoCoefficientSimilarity.class);

        private Class<? extends ItemSimilarity> _clazz;

        private ItemSimilarityClass(Class<? extends ItemSimilarity> clazz) {
            _clazz = clazz;
        }

        public Class<? extends ItemSimilarity> getClazz() {
            return _clazz;
        }

    }

    private enum UserSimilarityClass {
        EuclideanDistanceSimilarity(EuclideanDistanceSimilarity.class),
        PearsonCorrelationSimilarity(PearsonCorrelationSimilarity.class),
        UncenteredCosineSimilarity(UncenteredCosineSimilarity.class),
        CityBlockSimilarity(CityBlockSimilarity.class),
        LogLikelihoodSimilarity(LogLikelihoodSimilarity.class),
        TanimotoCoefficientSimilarity(TanimotoCoefficientSimilarity.class),
        SpearmanCorrelationSimilarity(SpearmanCorrelationSimilarity.class);

        private Class<? extends UserSimilarity> _clazz;

        private UserSimilarityClass(Class<? extends UserSimilarity> clazz) {
            _clazz = clazz;
        }

        public Class<? extends UserSimilarity> getClazz() {
            return _clazz;
        }

    }

    private enum UserNeighborhoodClass {
        NearestNUserNeighborhood(NearestNUserNeighborhood.class),
        ThresholdUserNeighborhood(ThresholdUserNeighborhood.class);

        private Class<? extends UserNeighborhood> _clazz;

        private UserNeighborhoodClass(Class<? extends UserNeighborhood> clazz) {
            _clazz = clazz;
        }

        public Class<? extends UserNeighborhood> getClazz() {
            return _clazz;
        }
    }

    private enum CommonRecommenderClass {
        ItemAverageRecommender(ItemAverageRecommender.class),
        ItemUserAverageRecommender(ItemUserAverageRecommender.class),
        SlopeOneRecommender(SlopeOneRecommender.class);

        private Class<? extends Recommender> _clazz;

        private CommonRecommenderClass(Class<? extends Recommender> clazz) {
            _clazz = clazz;
        }

        public Class<? extends Recommender> getClazz() {
            return _clazz;
        }
    }

    private final RomarConfigHolder _holder;

    private RomarConfig(RomarConfigHolder holder) {
        _holder = holder;
    }

    public MahoutServiceFactory getMahoutServiceFactory() {
        return _holder._recommendType._factory;
    }

    public boolean isUseSimilariyCache() {
        return _holder._useSimilarityCache;
    }

    public int getSimilarityCacheSize() {
        return _holder._similarityCacheSize;
    }

    public Class<? extends ItemSimilarity> getItemSimilarityClass() {
        return _holder._itemSimilarityClass.getClazz();
    }

    public Class<? extends UserSimilarity> getUserSimilarityClass() {
        return _holder._userSimilarityClass.getClazz();
    }

    public Class<? extends UserNeighborhood> getUserNeighborhoodClass() {
        return _holder._userNeighborhoodClass.getClazz();
    }

    public int getUserNeighborhoodNearestN() {
        return _holder._userNeighborhoodNearestN;
    }

    public Class<? extends Recommender> getCommonRecommenderClass() {
        return _holder._commonRecommenderClass.getClazz();
    }

    public String getPersistencePath() {
        return _holder._persistencePath;
    }

    public static void main(String[] args) {
        System.out.println(RomarConfig.getInstance());
    }

}
