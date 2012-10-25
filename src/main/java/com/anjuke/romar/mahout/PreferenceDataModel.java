package com.anjuke.romar.mahout;


import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

public interface PreferenceDataModel extends DataModel {
    FastByIDMap<PreferenceArray> getRawUserData();

    FastByIDMap<PreferenceArray> getRawItemData();

    void compact();

    /**
     * remove all memory data and load the given data
     * @param data
     */
    void reload(FastByIDMap<PreferenceArray> data);
}
