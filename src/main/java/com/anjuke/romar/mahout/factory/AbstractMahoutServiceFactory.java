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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.romar.core.RomarConfig;
import com.anjuke.romar.core.RomarCore;
import com.anjuke.romar.mahout.PreferenceDataModel;
import com.anjuke.romar.mahout.persistence.FilePreferenceSource;
import com.anjuke.romar.mahout.persistence.PersistenceDataModelProxy;
import com.anjuke.romar.mahout.persistence.PreferenceSource;

public abstract class AbstractMahoutServiceFactory implements MahoutServiceFactory {
    private static final Logger log = LoggerFactory.getLogger(RomarCore.class);

    protected PreferenceDataModel wrapDataModel(PreferenceDataModel dataModel) {
        final String persistencePath = RomarConfig.getInstance().getPersistencePath();
        if (persistencePath == null || persistencePath.isEmpty()) {
            return dataModel;
        }
        final PreferenceSource source = new FilePreferenceSource(
                new File(persistencePath));
        PreferenceDataModel model = new PersistenceDataModelProxy(dataModel, source);
        model.reload(null);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("FilePreferenceSource on file "+persistencePath +" close");
                source.close();
            }
        });
        return model;
    }
}
