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
package com.anjuke.romar.mahout.util;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;

public final class Util {
    private Util() {
    }

    public static void applyAdd(FastByIDMap<PreferenceArray> data,
            Preference addPreference) {
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

    public static void applyRemove(FastByIDMap<PreferenceArray> data,
            Preference removePreference) {
        long userID = removePreference.getUserID();
        long itemID = removePreference.getItemID();
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
}
