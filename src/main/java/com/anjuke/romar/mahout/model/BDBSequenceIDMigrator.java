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

import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.bind.tuple.TupleTupleKeyCreator;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.Sequence;
import com.sleepycat.je.SequenceConfig;

/**
 * Implementation which stores the reverse long-to-string mapping in BerkeleyDB.
 */
public class BDBSequenceIDMigrator extends BDBIDMigrator {

    /**
     * @see BDBSequenceIDMigrator
     */
    public BDBSequenceIDMigrator(String directory, long cacheSize) {
        this(directory, cacheSize, false);
    }

    /**
     * @see BDBIDMigrator
     */
    public BDBSequenceIDMigrator(String directory, long cacheSize,
                                 boolean transactional) {
        super(directory, cacheSize, transactional);
        _secondary = createSecondaryDatabase(getEnvironment(),
                getPrimaryDatabase());
        _sequence = createIdSequence(getPrimaryDatabase());
    }

    /**
     *
     */
    public void close() {
        if (_sequence != null) {
            _sequence.close();
            _sequence = null;
        }
        if (_secondary != null) {
            _secondary.close();
            _secondary = null;
        }
        super.close();
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    @Override
    public long toLongID(String stringID) {
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();
        DatabaseEntry primaryKey = new DatabaseEntry();

        StringBinding.stringToEntry(stringID, key);
        OperationStatus status = _secondary.get(null, key, primaryKey, data,
                LOCK_MODE);
        if (OperationStatus.SUCCESS == status) {
            return LongBinding.entryToLong(primaryKey);
        }

        long next = _sequence.get(null, 1);
        LongBinding.longToEntry(next, primaryKey);
        status = getPrimaryDatabase().put(null, primaryKey, key);
        if (status == OperationStatus.SUCCESS) {
            return next;
        }
        return -next;
    }

    //
    //
    //

    protected SecondaryDatabase createSecondaryDatabase(Environment environment,
                                                        Database primaryDb) {
        SecondaryConfig config = SecondaryConfig.DEFAULT;
        config.setAllowCreate(true);
        config.setTransactional(environment.getConfig().getTransactional());
        config.setSortedDuplicates(false);
        config.setKeyCreator(new TupleTupleKeyCreator<Long>() {
            @Override
            public boolean createSecondaryKey(TupleInput primaryKeyInput,
                                              TupleInput dataInput,
                                              TupleOutput indexKeyOutput) {
                try {
                    indexKeyOutput.writeString(dataInput.readString());
                } catch (Exception e) {
                    return false;
                }
                return true;
            }
        });
        return environment.openSecondaryDatabase(null, "string_id_mapping",
                primaryDb, config);
    }

    protected Sequence createIdSequence(Database db) {
        SequenceConfig config = SequenceConfig.DEFAULT;
        config.setAllowCreate(true);
        config.setInitialValue(1);

        DatabaseEntry sequenceName = new DatabaseEntry();
        StringBinding.stringToEntry("id_sequence", sequenceName);
        return db.openSequence(null, sequenceName, config);
    }

    /**
     * Returns StringID -> LongID mapping database
     * @return the database
     */
    protected Database getSecondaryDatabase() {
        return _secondary;
    }

    /**
     * Return sequence for LongID
     * @return the sequence
     */
    protected Sequence getSequence() {
        return _sequence;
    }

    //
    //
    //

    /**
     * DB: StringID -> LongID
     */
    private SecondaryDatabase _secondary;

    /**
     * Numeric ID sequence
     */
    private Sequence _sequence;

}
