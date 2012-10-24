package com.anjuke.romar.mahout.persistence;

import java.util.Collection;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import com.anjuke.romar.mahout.ForwardingDataModel;
import com.anjuke.romar.mahout.PreferenceDataModel;

public class PersistenceDataModelProxy extends ForwardingDataModel implements PreferenceDataModel{
    private static final long serialVersionUID = 1L;

    private final PreferenceSource source ;

    public PersistenceDataModelProxy(PreferenceDataModel dataModel,PreferenceSource source) {
        super(dataModel);
        this.source=source;
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        super.refresh(alreadyRefreshed);
        source.commit();
    }

    @Override
    public void setPreference(long userID, long itemID, float value)
            throws TasteException {
        super.setPreference(userID, itemID, value);
        source.setPreference(userID, itemID, value);
    }

    @Override
    public void removePreference(long userID, long itemID)
            throws TasteException {
        super.removePreference(userID, itemID);
        source.removePreference(userID,itemID);
    }


    /**
     * data could be none
     */
    public void reload(FastByIDMap<PreferenceArray> data) {
        super.reload(source.getPreferenceUserData());
    }

    @Override
    public void compact() {
        source.compact();
    }





}
