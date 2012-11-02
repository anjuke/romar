package com.anjuke.romar.mahout;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import com.anjuke.romar.mahout.util.Util;

public class GenericReloadDataModel implements PreferenceDataModel {
    private static final long serialVersionUID = -4393051837705770391L;

    private volatile GenericDataModel _currentModel;

    private final List<PreferenceValue> _data;

    private static class PreferenceValue implements Preference {

        public PreferenceValue(long userID, long itemID) {
            super();
            _add = false;
            _userID = userID;
            _itemID = itemID;
        }

        public PreferenceValue(long userID, long itemID, float value) {
            super();
            _add = true;
            _userID = userID;
            _itemID = itemID;
            _value = value;
        }

        private boolean _add;
        private long _userID;
        private long _itemID;
        private float _value;

        public boolean isAdd() {
            return _add;
        }

        public void setAdd(boolean add) {
            _add = add;
        }

        public long getUserID() {
            return _userID;
        }

        public void setUserID(long userID) {
            _userID = userID;
        }

        public long getItemID() {
            return _itemID;
        }

        public void setItemID(long itemID) {
            _itemID = itemID;
        }

        public float getValue() {
            return _value;
        }

        public void setValue(float value) {
            _value = value;
        }

    }

    public GenericReloadDataModel() {
        super();
        _currentModel = new GenericDataModel(new FastByIDMap<PreferenceArray>());
        _data = new LinkedList<PreferenceValue>();
    }

    /**
     * until refresh called , these data will not be used
     */
    @Override
    public synchronized void setPreference(long userID, long itemID, float preferenceValue)
            throws TasteException {
        _data.add(new PreferenceValue(userID, itemID, preferenceValue));
    }

    /** See the warning at {@link #setPreference(long, long, float)}. */
    @Override
    public synchronized void removePreference(long userID, long itemID) throws TasteException {
        _data.add(new PreferenceValue(userID, itemID));
    }

    @Override
    public synchronized void refresh(Collection<Refreshable> alreadyRefreshed) {
        FastByIDMap<PreferenceArray> data = _currentModel.getRawUserData().clone();
        for (PreferenceValue value : _data) {
            if (value.isAdd()) {
                Util.applyAdd(data, value);
            } else {
                Util.applyRemove(data, value);
            }
        }

        _currentModel = new GenericDataModel(data);
    }

    @Override
    public FastByIDMap<PreferenceArray> getRawUserData() {
        return _currentModel.getRawUserData();
    }

    @Override
    public FastByIDMap<PreferenceArray> getRawItemData() {
        return _currentModel.getRawItemData();
    }

    @Override
    public PreferenceArray getPreferencesFromUser(long userID) throws TasteException {
        return _currentModel.getPreferencesFromUser(userID);
    }

    @Override
    public FastIDSet getItemIDsFromUser(long userID) throws TasteException {
        return _currentModel.getItemIDsFromUser(userID);
    }

    @Override
    public LongPrimitiveIterator getItemIDs() throws TasteException {
        return _currentModel.getItemIDs();
    }

    @Override
    public PreferenceArray getPreferencesForItem(long itemID) throws TasteException {
        return _currentModel.getPreferencesForItem(itemID);
    }

    @Override
    public Float getPreferenceValue(long userID, long itemID) throws TasteException {
        return _currentModel.getPreferenceValue(userID, itemID);
    }

    @Override
    public Long getPreferenceTime(long userID, long itemID) throws TasteException {
        return _currentModel.getPreferenceTime(userID, itemID);
    }

    @Override
    public int getNumItems() throws TasteException {
        return _currentModel.getNumItems();
    }

    @Override
    public int getNumUsers() throws TasteException {
        return _currentModel.getNumUsers();
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID) throws TasteException {
        return _currentModel.getNumUsersWithPreferenceFor(itemID);
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID1, long itemID2)
            throws TasteException {
        return _currentModel.getNumUsersWithPreferenceFor(itemID1, itemID2);
    }

    @Override
    public boolean hasPreferenceValues() {
        return _currentModel.hasPreferenceValues();
    }

    @Override
    public float getMaxPreference() {
        return _currentModel.getMaxPreference();
    }

    @Override
    public float getMinPreference() {
        return _currentModel.getMinPreference();
    }

    @Override
    public LongPrimitiveIterator getUserIDs() throws TasteException {
        return _currentModel.getUserIDs();
    }

    @Override
    public void reload(FastByIDMap<PreferenceArray> data) {
        _currentModel = new GenericDataModel(data);
    }

    @Override
    public void compact() {
        throw new UnsupportedOperationException();
    }
}
