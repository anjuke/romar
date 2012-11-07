/**
 * Copyright 2012 Anjuke Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.anjuke.romar.mahout.persistence;

import java.util.Collection;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import com.anjuke.romar.mahout.ForwardingDataModel;
import com.anjuke.romar.mahout.PreferenceDataModel;

public class PersistenceDataModelProxy extends ForwardingDataModel implements
        PreferenceDataModel {
    private static final long serialVersionUID = 1L;

    private final PreferenceSource _source;

    public PersistenceDataModelProxy(PreferenceDataModel dataModel,
            PreferenceSource source) {
        super(dataModel);
        this._source = source;
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        super.refresh(alreadyRefreshed);
        _source.commit();
    }

    @Override
    public void setPreference(long userID, long itemID, float value)
            throws TasteException {
        super.setPreference(userID, itemID, value);
        _source.setPreference(userID, itemID, value);
    }

    @Override
    public void removePreference(long userID, long itemID)
            throws TasteException {
        super.removePreference(userID, itemID);
        _source.removePreference(userID, itemID);
    }

    /**
     * data could be none
     */
    public void reload(FastByIDMap<PreferenceArray> data) {
        super.reload(_source.getPreferenceUserData());
    }

    @Override
    public void compact() {
        _source.compact();
    }

}
