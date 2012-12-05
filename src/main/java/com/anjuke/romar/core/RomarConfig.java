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

    private static final Logger LOG = LoggerFactory.getLogger(RomarConfig.class);

    private static final RomarConfig INSTANCE;

    private static final String CONF_PATH_KEY = "romar.config";

    public static class RomarConfigHolder {
        private Boolean _allowUserStringID;
        private Boolean _allowItemStringID;
        private Integer _serverPort;
        private RecommendType _recommendType;
        private Boolean _useSimilarityCache;
        private Integer _similarityCacheSize;
        private ItemSimilarityClass _itemSimilarityClass;
        private UserSimilarityClass _userSimilarityClass;
        private UserNeighborhoodClass _userNeighborhoodClass;
        private Integer _userNeighborhoodNearestN;
        private CommonRecommenderClass _commonRecommenderClass;
        private String _persistencePath;

        public Boolean getAllowUserStringID() {
            return _allowUserStringID;
        }

        public void setAllowUserStringID(Boolean allowUserStringID) {
            _allowUserStringID = allowUserStringID;
        }

        public Boolean getAllowItemStringID() {
            return _allowItemStringID;
        }

        public void setAllowItemStringID(Boolean allowItemStringID) {
            _allowItemStringID = allowItemStringID;
        }

        public Integer getServerPort() {
            return _serverPort;
        }

        public void setServerPort(Integer serverPort) {
            _serverPort = serverPort;
        }

        public RecommendType getRecommendType() {
            return _recommendType;
        }

        public void setRecommendType(RecommendType recommendType) {
            _recommendType = recommendType;
        }

        public Boolean getUseSimilarityCache() {
            return _useSimilarityCache;
        }

        public void setUseSimilarityCache(Boolean useSimilarityCache) {
            _useSimilarityCache = useSimilarityCache;
        }

        public Integer getSimilarityCacheSize() {
            return _similarityCacheSize;
        }

        public void setSimilarityCacheSize(Integer similarityCacheSize) {
            _similarityCacheSize = similarityCacheSize;
        }

        public ItemSimilarityClass getItemSimilarityClass() {
            return _itemSimilarityClass;
        }

        public void setItemSimilarityClass(ItemSimilarityClass itemSimilarityClass) {
            _itemSimilarityClass = itemSimilarityClass;
        }

        public UserSimilarityClass getUserSimilarityClass() {
            return _userSimilarityClass;
        }

        public void setUserSimilarityClass(UserSimilarityClass userSimilarityClass) {
            _userSimilarityClass = userSimilarityClass;
        }

        public UserNeighborhoodClass getUserNeighborhoodClass() {
            return _userNeighborhoodClass;
        }

        public void setUserNeighborhoodClass(UserNeighborhoodClass userNeighborhoodClass) {
            _userNeighborhoodClass = userNeighborhoodClass;
        }

        public Integer getUserNeighborhoodNearestN() {
            return _userNeighborhoodNearestN;
        }

        public void setUserNeighborhoodNearestN(Integer userNeighborhoodNearestN) {
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

        InputStream isDefault = null;
        InputStream isCustom = null;
        try {
            isDefault = RomarConfig.class.getResourceAsStream("/romar.default.yaml");
            RomarConfigHolder defaultHolder = yaml.loadAs(isDefault,
                    RomarConfigHolder.class);
            RomarConfigHolder customHolder;
            LOG.debug("custom conf path is " + path);
            if (path != null && !path.isEmpty()) {
                LOG.info("loading config from " + path);
                isCustom = new FileInputStream(path);
                customHolder = yaml.loadAs(isCustom, RomarConfigHolder.class);
            } else {
                customHolder = new RomarConfigHolder();
            }

            INSTANCE = new RomarConfig(defaultHolder, customHolder);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        } finally {
            if (isDefault != null) {
                try {
                    isDefault.close();
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            if (isCustom != null) {
                try {
                    isCustom.close();
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

    private final RomarConfigHolder _defaultHolder;

    private final RomarConfigHolder _customerHolder;

    public RomarConfig(RomarConfigHolder defaultHolder, RomarConfigHolder customerHolder) {
        super();
        _defaultHolder = defaultHolder;
        _customerHolder = customerHolder;
    }

    public int getServerPort() {
        if (_customerHolder._serverPort != null) {
            return _customerHolder._serverPort;
        }
        return _defaultHolder._serverPort;
    }

    public MahoutServiceFactory getMahoutServiceFactory() {
        if (_customerHolder._recommendType != null) {
            return _customerHolder._recommendType._factory;
        }

        return _defaultHolder._recommendType._factory;
    }

    public boolean isUseSimilariyCache() {
        if (_customerHolder._useSimilarityCache != null) {
            return _customerHolder._useSimilarityCache;
        }

        return _defaultHolder._useSimilarityCache;
    }

    public int getSimilarityCacheSize() {
        if (_customerHolder._similarityCacheSize != null) {
            return _customerHolder._similarityCacheSize;
        }

        return _defaultHolder._similarityCacheSize;
    }

    public Class<? extends ItemSimilarity> getItemSimilarityClass() {
        if (_customerHolder._itemSimilarityClass != null) {
            return _customerHolder._itemSimilarityClass.getClazz();
        }

        return _defaultHolder._itemSimilarityClass.getClazz();
    }

    public Class<? extends UserSimilarity> getUserSimilarityClass() {
        if (_customerHolder._userSimilarityClass != null) {
            return _customerHolder._userSimilarityClass.getClazz();
        }

        return _defaultHolder._userSimilarityClass.getClazz();
    }

    public Class<? extends UserNeighborhood> getUserNeighborhoodClass() {
        if (_customerHolder._userNeighborhoodClass != null) {
            return _customerHolder._userNeighborhoodClass.getClazz();
        }

        return _defaultHolder._userNeighborhoodClass.getClazz();
    }

    public int getUserNeighborhoodNearestN() {
        if (_customerHolder._userNeighborhoodNearestN != null) {
            return _customerHolder._userNeighborhoodNearestN;
        }

        return _defaultHolder._userNeighborhoodNearestN;
    }

    public Class<? extends Recommender> getCommonRecommenderClass() {
        if (_customerHolder._commonRecommenderClass != null) {
            return _customerHolder._commonRecommenderClass.getClazz();
        }

        return _defaultHolder._commonRecommenderClass.getClazz();
    }

    public String getPersistencePath() {
        String path;
        if (_customerHolder._persistencePath != null) {
            path = _customerHolder._persistencePath;
        } else {
            path = _defaultHolder._persistencePath;
        }
        if (path == null) {
            return null;
        }
        if (path.startsWith("/")) {
            return path;
        } else {
            return System.getProperty("romar.home", "/tmp/romar") + "/" + path;
        }
    }

    public boolean isAllowUserStringID() {
        Boolean allowStringID = _customerHolder.getAllowUserStringID();
        if (allowStringID != null) {
            return allowStringID;
        }
        return _defaultHolder._allowUserStringID;
    }

    public boolean isAllowItemStringID() {
        Boolean allowStringID = _customerHolder.getAllowItemStringID();
        if (allowStringID != null) {
            return allowStringID;
        }
        return _defaultHolder._allowItemStringID;
    }

}
