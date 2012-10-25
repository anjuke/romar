package com.anjuke.romar.mahout;

import java.util.Collection;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.PreferenceArray;

public class ForwardingDataModel implements PreferenceDataModel {
    private static final long serialVersionUID = -3702872370562680832L;

    private final PreferenceDataModel _dataModel;

    protected ForwardingDataModel(PreferenceDataModel dataModel) {
        super();
        _dataModel = dataModel;
    }

    @Override
    public PreferenceArray getPreferencesFromUser(long userID)
            throws TasteException {
        return _dataModel.getPreferencesFromUser(userID);
    }

    @Override
    public FastIDSet getItemIDsFromUser(long userID) throws TasteException {
        return _dataModel.getItemIDsFromUser(userID);
    }

    @Override
    public LongPrimitiveIterator getItemIDs() throws TasteException {
        return _dataModel.getItemIDs();
    }

    @Override
    public PreferenceArray getPreferencesForItem(long itemID)
            throws TasteException {
        return _dataModel.getPreferencesForItem(itemID);
    }

    @Override
    public Float getPreferenceValue(long userID, long itemID)
            throws TasteException {
        return _dataModel.getPreferenceValue(userID, itemID);
    }

    @Override
    public Long getPreferenceTime(long userID, long itemID)
            throws TasteException {
        return _dataModel.getPreferenceTime(userID, itemID);
    }

    @Override
    public int getNumItems() throws TasteException {
        return _dataModel.getNumItems();
    }

    @Override
    public int getNumUsers() throws TasteException {
        return _dataModel.getNumUsers();
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID) throws TasteException {
        return _dataModel.getNumUsersWithPreferenceFor(itemID);
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID1, long itemID2)
            throws TasteException {
        return _dataModel.getNumUsersWithPreferenceFor(itemID1, itemID2);
    }

    @Override
    public boolean hasPreferenceValues() {
        return _dataModel.hasPreferenceValues();
    }

    @Override
    public float getMaxPreference() {
        return _dataModel.getMaxPreference();
    }

    @Override
    public float getMinPreference() {
        return _dataModel.getMinPreference();
    }

    @Override
    public LongPrimitiveIterator getUserIDs() throws TasteException {
        return _dataModel.getUserIDs();
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        _dataModel.refresh(alreadyRefreshed);

    }

    @Override
    public void setPreference(long userID, long itemID, float value)
            throws TasteException {
        _dataModel.setPreference(userID, itemID, value);
    }

    @Override
    public void removePreference(long userID, long itemID)
            throws TasteException {
        _dataModel.removePreference(userID, itemID);
    }

    @Override
    public FastByIDMap<PreferenceArray> getRawUserData(){
        return _dataModel.getRawUserData();
    }

    @Override
    public FastByIDMap<PreferenceArray> getRawItemData() {
        return _dataModel.getRawItemData();
    }

    @Override
    public void reload(FastByIDMap<PreferenceArray> data) {
        _dataModel.reload(data);
    }

    @Override
    public void compact() {
        _dataModel.compact();
    }
}
