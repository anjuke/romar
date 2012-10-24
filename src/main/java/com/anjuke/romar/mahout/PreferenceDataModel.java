package com.anjuke.romar.mahout;


import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

public interface PreferenceDataModel extends DataModel {
    public FastByIDMap<PreferenceArray> getRawUserData();

    public FastByIDMap<PreferenceArray> getRawItemData();

    public void compact();
    /**
     * remove all memory data and load the given data
     * @param data
     */
    public void reload(FastByIDMap<PreferenceArray> data);
}
