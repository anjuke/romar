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
    protected final PreferenceDataModel dataModel;

    protected ForwardingDataModel(PreferenceDataModel dataModel) {
        super();
        this.dataModel = dataModel;
    }

    @Override
    public PreferenceArray getPreferencesFromUser(long userID)
            throws TasteException {
        return dataModel.getPreferencesFromUser(userID);
    }

    @Override
    public FastIDSet getItemIDsFromUser(long userID) throws TasteException {
        return dataModel.getItemIDsFromUser(userID);
    }

    @Override
    public LongPrimitiveIterator getItemIDs() throws TasteException {
        return dataModel.getItemIDs();
    }

    @Override
    public PreferenceArray getPreferencesForItem(long itemID)
            throws TasteException {
        return dataModel.getPreferencesForItem(itemID);
    }

    @Override
    public Float getPreferenceValue(long userID, long itemID)
            throws TasteException {
        return dataModel.getPreferenceValue(userID, itemID);
    }

    @Override
    public Long getPreferenceTime(long userID, long itemID)
            throws TasteException {
        return dataModel.getPreferenceTime(userID, itemID);
    }

    @Override
    public int getNumItems() throws TasteException {
        return dataModel.getNumItems();
    }

    @Override
    public int getNumUsers() throws TasteException {
        return dataModel.getNumUsers();
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID) throws TasteException {
        return dataModel.getNumUsersWithPreferenceFor(itemID);
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID1, long itemID2)
            throws TasteException {
        return dataModel.getNumUsersWithPreferenceFor(itemID1, itemID2);
    }

    @Override
    public boolean hasPreferenceValues() {
        return dataModel.hasPreferenceValues();
    }

    @Override
    public float getMaxPreference() {
        return dataModel.getMaxPreference();
    }

    @Override
    public float getMinPreference() {
        return dataModel.getMinPreference();
    }

    @Override
    public LongPrimitiveIterator getUserIDs() throws TasteException {
        return dataModel.getUserIDs();
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        dataModel.refresh(alreadyRefreshed);

    }

    @Override
    public void setPreference(long userID, long itemID, float value)
            throws TasteException {
        dataModel.setPreference(userID, itemID, value);
    }

    @Override
    public void removePreference(long userID, long itemID)
            throws TasteException {
        dataModel.removePreference(userID, itemID);
    }

    @Override
    public FastByIDMap<PreferenceArray> getRawUserData(){
        return dataModel.getRawUserData();
    }

    @Override
    public FastByIDMap<PreferenceArray> getRawItemData() {
        return dataModel.getRawItemData();
    }

    @Override
    public void reload(FastByIDMap<PreferenceArray> data) {
        dataModel.reload(data);
    }

    @Override
    public void compact() {
        dataModel.compact();
    }

}
