package com.anjuke.romar.mahout.persistence;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.model.PreferenceArray;


public interface PreferenceSource {

    void commit();

    void compact();

    void setPreference(long userID, long itemID, float value);

    void removePreference(long userID, long itemID);

    void removePreferenceByUserId(long userID);

    void removePreferenceByItemId(long itemID);

    void close();

    FastByIDMap<PreferenceArray> getPreferenceUserData();

}
