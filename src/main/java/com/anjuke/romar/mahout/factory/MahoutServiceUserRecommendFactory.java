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
package com.anjuke.romar.mahout.factory;

import java.io.File;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CachingUserSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.GenericUserSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.GenericUserSimilarity.UserUserSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.ClassUtils;

import com.anjuke.romar.core.RomarConfig;
import com.anjuke.romar.mahout.MahoutService;
import com.anjuke.romar.mahout.RecommenderWrapper;
import com.anjuke.romar.mahout.similarity.ReadableGenericSimilarityProxy;
import com.anjuke.romar.mahout.similarity.ReadableSimilarity;
import com.anjuke.romar.mahout.similarity.RomarFileUserSimilarity;
import com.anjuke.romar.mahout.similarity.file.RomarFileSimilarityIterator;
import com.anjuke.romar.mahout.similarity.file.RomarFileSimilarityIterator.IteratorBuiler;

public class MahoutServiceUserRecommendFactory implements MahoutServiceFactory {
    @Override
    public MahoutService createService() {
        RomarConfig config = RomarConfig.getInstance();
        Recommender recommender;
        DataModel dataModel = PersistenceDataModelFactory.createDataModel(config);

        UserSimilarity similarity;
        if (config.isUseFileSimilarity()) {
            File file = new File(config.getSimilarityFile());
            if (!file.exists()) {
                throw new IllegalArgumentException("similairy file not exists");
            }

            if (!file.isFile()) {
                throw new IllegalArgumentException("similairy file is a directory");
            }

            IteratorBuiler<UserUserSimilarity> iteratorBuilder;
            if (config.isBinarySimilarityFile()) {
                iteratorBuilder = RomarFileSimilarityIterator
                        .dataFileUserIteratorBuilder();
            } else {
                iteratorBuilder = RomarFileSimilarityIterator
                        .lineFileUserIteratorBuilder();
            }
            similarity = new RomarFileUserSimilarity(file, iteratorBuilder);
        } else {
            similarity = createSimilarity(config, dataModel);
            if (config.isUseSimilariyCache()) {
                similarity = new CachingUserSimilarity(similarity,
                        config.getSimilarityCacheSize());
            }
        }

        UserNeighborhood neighborhood = ClassUtils
                .instantiateAs(
                        config.getUserNeighborhoodClass(),
                        UserNeighborhood.class,
                        new Class<?>[] {int.class, UserSimilarity.class, DataModel.class},
                        new Object[] {config.getUserNeighborhoodNearestN(), similarity,
                                dataModel});
        recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);

        return new RecommenderWrapper(recommender);
    }

    @Override
    public ReadableSimilarity createReadableSimilarity(DataModel dataModel) {
        RomarConfig config = RomarConfig.getInstance();
        UserSimilarity similarity = createSimilarity(config,
                dataModel);
        try {
            return ReadableGenericSimilarityProxy
                    .proxySimilarity(new GenericUserSimilarity(similarity, dataModel));
        } catch (TasteException e) {
            throw new RuntimeException(e);
        }
    }

    private UserSimilarity createSimilarity(RomarConfig config, DataModel dataModel) {
        UserSimilarity similarity = ClassUtils.instantiateAs(
                config.getUserSimilarityClass(), UserSimilarity.class,
                new Class<?>[] {DataModel.class}, new Object[] {dataModel});
        return similarity;
    }
}
