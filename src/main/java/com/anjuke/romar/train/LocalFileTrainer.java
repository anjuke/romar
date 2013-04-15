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
package com.anjuke.romar.train;

import java.io.IOException;
import java.util.Map;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.model.DataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.romar.core.RomarConfig;
import com.anjuke.romar.mahout.factory.MahoutServiceFactory;
import com.anjuke.romar.mahout.factory.PersistenceDataModelFactory;
import com.anjuke.romar.mahout.similarity.ReadableSimilarity;
import com.anjuke.romar.mahout.similarity.file.SimilarityFileWriter;
import com.anjuke.romar.mahout.similarity.file.SimilarityFileWriterFactory;
import com.google.common.io.Closeables;

public final class LocalFileTrainer {
    private static final Logger LOG = LoggerFactory.getLogger(LocalFileTrainer.class);
    private LocalFileTrainer(){

    }
    public static void main(String[] args) {
        RomarConfig config = RomarConfig.getInstance();

        LOG.info("init dataModel");
        DataModel dataModel = PersistenceDataModelFactory.createDataModel(config);

        MahoutServiceFactory serviceFactory = config.getMahoutServiceFactory();
        LOG.info("init in memory similarity");
        ReadableSimilarity similarity = serviceFactory
                .createReadableSimilarity(dataModel);
        FastByIDMap<FastByIDMap<Double>> similarityDataMap = similarity
                .getSimilarityMaps();

        SimilarityFileWriter writer = null;
        LOG.info("start write to " + config.getSimilarityFile());
        try {
            writer = SimilarityFileWriterFactory.createFileWriter(config);
            for (Map.Entry<Long, FastByIDMap<Double>> idValueEntry : similarityDataMap
                    .entrySet()) {
                long id1 = idValueEntry.getKey();

                for (Map.Entry<Long, Double> entry : idValueEntry.getValue().entrySet()) {
                    long id2 = entry.getKey();
                    double value = entry.getValue();
                    writer.write(id1, id2, value);
                }
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            Closeables.closeQuietly(writer);
        }
        LOG.info("train finish");
    }
}
