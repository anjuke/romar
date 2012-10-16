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
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CachingItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;

public class GenericReloadDataModel implements DataModel {
    private static final long serialVersionUID = -4393051837705770391L;

    private volatile GenericDataModel currentModel;

    // private final FastByIDMap<PreferenceArray> data;
    private final List<Preference> addData;
    private final List<Preference> removeData;

    public GenericReloadDataModel() {
        super();
        currentModel = new GenericDataModel(new FastByIDMap<PreferenceArray>());
        addData = new LinkedList<Preference>();
        removeData = new LinkedList<Preference>();
    }

    /**
     * until refresh called , these data will not be used
     */
    @Override
    public synchronized void setPreference(long userID, long itemID,
            float preferenceValue) throws TasteException {

        addData.add(new GenericPreference(userID, itemID, preferenceValue));

    }

    /** See the warning at {@link #setPreference(long, long, float)}. */
    @Override
    public synchronized void removePreference(long userID, long itemID)
            throws TasteException {
        removeData.add(new BooleanPreference(userID, itemID));
    }

    @Override
    public synchronized void refresh(Collection<Refreshable> alreadyRefreshed) {
        FastByIDMap<PreferenceArray> data = currentModel.getRawUserData()
                .clone();
        applyAddData(data);
        applyRemoveData(data);
        currentModel = new GenericDataModel(data);
    }


    private void applyRemoveData(FastByIDMap<PreferenceArray> data){
        for (Preference addPreference : removeData) {
            long userID = addPreference.getUserID();
            long itemID = addPreference.getItemID();
            PreferenceArray prefs = data.get(userID);
            if (prefs != null) {
                boolean exists = false;
                int length = prefs.length();
                for (int i = 0; i < length; i++) {
                    if (prefs.getItemID(i) == itemID) {
                        exists = true;
                        break;
                    }
                }
                if (exists) {
                    if (length == 1) {
                        data.remove(userID);
                    } else {
                        PreferenceArray newPrefs = new GenericUserPreferenceArray(
                                length - 1);
                        for (int i = 0, j = 0; i < length; i++, j++) {
                            if (prefs.getItemID(i) == itemID) {
                                j--;
                            } else {
                                newPrefs.set(j, prefs.get(i));
                            }
                        }
                        data.put(userID, newPrefs);
                    }
                }
            }
        }
        removeData.clear();
    }


    private void applyAddData(FastByIDMap<PreferenceArray> data) {
        for (Preference addPreference : addData) {
            long userID = addPreference.getUserID();
            long itemID = addPreference.getItemID();
            float preferenceValue = addPreference.getValue();
            PreferenceArray prefs = data.get(userID);
            boolean exists = false;
            if (prefs != null) {
                for (int i = 0; i < prefs.length(); i++) {
                    if (prefs.getItemID(i) == itemID) {
                        exists = true;
                        prefs.setValue(i, preferenceValue);
                        break;
                    }
                }
            }

            if (!exists) {
                if (prefs == null) {
                    prefs = new GenericUserPreferenceArray(1);
                } else {
                    PreferenceArray newPrefs = new GenericUserPreferenceArray(
                            prefs.length() + 1);
                    for (int i = 0, j = 1; i < prefs.length(); i++, j++) {
                        newPrefs.set(j, prefs.get(i));
                    }
                    prefs = newPrefs;
                }
                prefs.setUserID(0, userID);
                prefs.setItemID(0, itemID);
                prefs.setValue(0, preferenceValue);
                data.put(userID, prefs);
            }
        }
        addData.clear();
    }

    @Override
    public PreferenceArray getPreferencesFromUser(long userID)
            throws TasteException {
        return currentModel.getPreferencesFromUser(userID);
    }

    @Override
    public FastIDSet getItemIDsFromUser(long userID) throws TasteException {
        return currentModel.getItemIDsFromUser(userID);
    }

    @Override
    public LongPrimitiveIterator getItemIDs() throws TasteException {
        return currentModel.getItemIDs();
    }

    @Override
    public PreferenceArray getPreferencesForItem(long itemID)
            throws TasteException {
        return currentModel.getPreferencesForItem(itemID);
    }

    @Override
    public Float getPreferenceValue(long userID, long itemID)
            throws TasteException {
        return currentModel.getPreferenceValue(userID, itemID);
    }

    @Override
    public Long getPreferenceTime(long userID, long itemID)
            throws TasteException {
        return currentModel.getPreferenceTime(userID, itemID);
    }

    @Override
    public int getNumItems() throws TasteException {
        return currentModel.getNumItems();
    }

    @Override
    public int getNumUsers() throws TasteException {
        return currentModel.getNumUsers();
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID) throws TasteException {
        return currentModel.getNumUsersWithPreferenceFor(itemID);
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID1, long itemID2)
            throws TasteException {
        return currentModel.getNumUsersWithPreferenceFor(itemID1, itemID2);
    }

    @Override
    public boolean hasPreferenceValues() {
        return currentModel.hasPreferenceValues();
    }

    @Override
    public float getMaxPreference() {
        return currentModel.getMaxPreference();
    }

    @Override
    public float getMinPreference() {
        return currentModel.getMinPreference();
    }

    @Override
    public LongPrimitiveIterator getUserIDs() throws TasteException {
        return currentModel.getUserIDs();
    }

}
