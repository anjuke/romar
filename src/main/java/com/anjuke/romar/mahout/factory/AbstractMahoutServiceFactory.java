package com.anjuke.romar.mahout.factory;

import java.io.File;

import com.anjuke.romar.core.RomarConfig;
import com.anjuke.romar.mahout.PreferenceDataModel;
import com.anjuke.romar.mahout.persistence.FilePreferenceSource;
import com.anjuke.romar.mahout.persistence.PersistenceDataModelProxy;
import com.anjuke.romar.mahout.persistence.PreferenceSource;

public abstract class AbstractMahoutServiceFactory implements
        MahoutServiceFactory {
    protected PreferenceDataModel wrapDataModel(PreferenceDataModel dataModel) {
        final PreferenceSource source = new FilePreferenceSource(new File(
                RomarConfig.getInstance().getPersistencePath()));
        PreferenceDataModel model = new PersistenceDataModelProxy(dataModel,
                source);
        model.reload(null);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                source.close();
            }
        });
        return model;
    }
}
