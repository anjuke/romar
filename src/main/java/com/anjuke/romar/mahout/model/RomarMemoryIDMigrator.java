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
