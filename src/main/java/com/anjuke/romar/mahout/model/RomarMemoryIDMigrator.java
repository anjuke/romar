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
package com.anjuke.romar.mahout.model;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.AbstractIDMigrator;

public class RomarMemoryIDMigrator extends AbstractIDMigrator {

    private final FastByIDMap<String> _longToString;

    private static final int INIT_SIZE = 100;

    public RomarMemoryIDMigrator() {
        this._longToString = new FastByIDMap<String>(INIT_SIZE);
    }

    @Override
    public long toLongID(String stringID) {
        long longID = super.toLongID(stringID);
        synchronized (_longToString) {
            _longToString.put(longID, stringID);

        }
        return longID;
    }

    @Override
    public String toStringID(long longID) {
        synchronized (_longToString) {
            return _longToString.get(longID);
        }
    }
}
