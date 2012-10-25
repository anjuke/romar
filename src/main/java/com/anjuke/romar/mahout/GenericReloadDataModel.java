package com.anjuke.romar.mahout;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.BooleanPreference;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import com.anjuke.romar.mahout.util.Util;

public class GenericReloadDataModel implements PreferenceDataModel {
    private static final long serialVersionUID = -4393051837705770391L;

    private volatile GenericDataModel _currentModel;

    private final List<Preference> _addData;
    private final List<Preference> _removeData;

    public GenericReloadDataModel() {
        super();
        _currentModel = new GenericDataModel(new FastByIDMap<PreferenceArray>());
        _addData = new LinkedList<Preference>();
        _removeData = new LinkedList<Preference>();
    }

    /**
     * until refresh called , these data will not be used
     */
    @Override
    public void setPreference(long userID, long itemID,
            float preferenceValue) throws TasteException {
        _addData.add(new GenericPreference(userID, itemID, preferenceValue));
    }

    /** See the warning at {@link #setPreference(long, long, float)}. */
    @Override
    public void removePreference(long userID, long itemID)
            throws TasteException {
        _removeData.add(new BooleanPreference(userID, itemID));
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        FastByIDMap<PreferenceArray> data = _currentModel.getRawUserData()
                .clone();
        applyAddData(data);
        applyRemoveData(data);
        _currentModel = new GenericDataModel(data);
    }

    private void applyRemoveData(FastByIDMap<PreferenceArray> data){
        for (Preference removePreference : _removeData) {
            Util.applyRemove(data, removePreference);
        }
        _removeData.clear();
    }

    private void applyAddData(FastByIDMap<PreferenceArray> data) {
        for (Preference addPreference : _addData) {
            Util.applyAdd(data, addPreference);
        }
        _addData.clear();
    }

    @Override
    public FastByIDMap<PreferenceArray> getRawUserData(){
        return _currentModel.getRawUserData();
    }

    @Override
    public FastByIDMap<PreferenceArray> getRawItemData() {
        return _currentModel.getRawItemData();
    }

    @Override
    public PreferenceArray getPreferencesFromUser(long userID)
            throws TasteException {
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
    public PreferenceArray getPreferencesForItem(long itemID)
            throws TasteException {
        return _currentModel.getPreferencesForItem(itemID);
    }

    @Override
    public Float getPreferenceValue(long userID, long itemID)
            throws TasteException {
        return _currentModel.getPreferenceValue(userID, itemID);
    }

    @Override
    public Long getPreferenceTime(long userID, long itemID)
            throws TasteException {
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
