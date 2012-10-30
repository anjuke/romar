package com.anjuke.romar.mahout.model;

import java.util.Collection;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

public class CachedDataModel implements DataModel {
    public CachedDataModel(DataModel delegate) throws TasteException {
        _delegate = delegate;

        _preferencesFromUserMap = new FastByIDMap<PreferenceArray>(
                _delegate.getNumUsers());
        _preferencesForItemMap = new FastByIDMap<PreferenceArray>(
                _delegate.getNumItems());
    }

    @Override
    public LongPrimitiveIterator getUserIDs() throws TasteException {
        return _delegate.getUserIDs();
    }

    @Override
    public PreferenceArray getPreferencesFromUser(long userID) throws TasteException {
        if (_preferencesFromUserMap.containsKey(userID)) {
            return _preferencesFromUserMap.get(userID);
        }
        PreferenceArray preferences = _delegate.getPreferencesFromUser(userID);
        _preferencesFromUserMap.put(userID, preferences);
        return preferences;
    }

    @Override
    public FastIDSet getItemIDsFromUser(long userID) throws TasteException {
        if (_preferencesFromUserMap.containsKey(userID)) {
            PreferenceArray preferences = getPreferencesFromUser(userID);
            int size = preferences.length();
            FastIDSet result = new FastIDSet(size);
            for (int i = 0; i < size; i++) {
                result.add(preferences.getItemID(i));
            }
            return result;
        }

        return _delegate.getItemIDsFromUser(userID);
    }

    @Override
    public LongPrimitiveIterator getItemIDs() throws TasteException {
        return _delegate.getItemIDs();
    }

    @Override
    public PreferenceArray getPreferencesForItem(long itemID) throws TasteException {
        if (_preferencesForItemMap.containsKey(itemID)) {
            return _preferencesForItemMap.get(itemID);
        }
        PreferenceArray preferences = _delegate.getPreferencesForItem(itemID);
        _preferencesForItemMap.put(itemID, preferences);
        return preferences;
    }

    @Override
    public Float getPreferenceValue(long userID, long itemID) throws TasteException {
        return _delegate.getPreferenceValue(userID, itemID);
    }

    @Override
    public Long getPreferenceTime(long userID, long itemID) throws TasteException {
        return _delegate.getPreferenceTime(userID, itemID);
    }

    @Override
    public int getNumItems() throws TasteException {
        return _delegate.getNumItems();
    }

    @Override
    public int getNumUsers() throws TasteException {
        return _delegate.getNumUsers();
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID) throws TasteException {
        if (_preferencesForItemMap.containsKey(itemID)) {
            return _preferencesFromUserMap.size();
        }

        return _delegate.getNumUsersWithPreferenceFor(itemID);
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID1, long itemID2) throws TasteException {
        return _delegate.getNumUsersWithPreferenceFor(itemID1, itemID2);
    }

    @Override
    public void setPreference(long userID, long itemID, float value) throws TasteException {
        _delegate.setPreference(userID, itemID, value);
        _preferencesFromUserMap.remove(userID);
        _preferencesForItemMap.remove(itemID);

    }

    @Override
    public void removePreference(long userID, long itemID) throws TasteException {
        _delegate.removePreference(userID, itemID);
        _preferencesFromUserMap.clear();
        _preferencesForItemMap.clear();
    }

    @Override
    public boolean hasPreferenceValues() {
        return _delegate.hasPreferenceValues();
    }

    @Override
    public float getMaxPreference() {
        return _delegate.getMaxPreference();
    }

    @Override
    public float getMinPreference() {
        return _delegate.getMinPreference();
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        _delegate.refresh(alreadyRefreshed);
        _preferencesFromUserMap.clear();
        _preferencesForItemMap.clear();
    }

    private DataModel _delegate;

    private final FastByIDMap<PreferenceArray> _preferencesFromUserMap;
    private final FastByIDMap<PreferenceArray> _preferencesForItemMap;
}
