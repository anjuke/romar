package com.anjuke.romar.mahout.persistence;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.model.PreferenceArray;


public interface PreferenceSource {

    public void commit();

    public void compact();

    public void setPreference(long userID, long itemID, float value);

    public void removePreference(long userID, long itemID);

    public void removePreferenceByUserId(long userID);

    public void removePreferenceByItemId(long itemID);

    public void close();

    public FastByIDMap<PreferenceArray> getPreferenceUserData();

}
