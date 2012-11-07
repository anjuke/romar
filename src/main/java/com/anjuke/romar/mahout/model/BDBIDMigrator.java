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

import java.io.File;

import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import org.apache.mahout.cf.taste.impl.model.AbstractIDMigrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BDBIDMigrator extends AbstractIDMigrator {
    /**
     *
     * @param directory the directory where the data will be persist to
     * @param cacheSize The size of cache for the environment
     *
     * @see BDBSequenceIDMigrator
     */
    public BDBIDMigrator(String directory, long cacheSize) {
        this(directory, cacheSize, false);
    }

    /**
     *
     * @param directory the directory where the data will be persist to
     * @param cacheSize The size of cache for the environment
     * @param transactional enable transaction or not
     */
    public BDBIDMigrator(String directory, long cacheSize,
                                 boolean transactional) {
        _environment = createEnvironment(directory, cacheSize, transactional);
        _primary = createPrimaryDatabase(_environment);
    }

    /**
     *
     */
    public void close() {
        if (_primary != null) {
            _primary.close();
            _primary = null;
        }
        if (_environment != null) {
            _environment.cleanLog();
            _environment.close();
            _environment = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    @Override
    public long toLongID(String stringID) {
        long longID = super.toLongID(stringID); // & 0x7FFFFFFFFFFFFFFFL;

        DatabaseEntry stringEntry = new DatabaseEntry();
        StringBinding.stringToEntry(stringID, stringEntry);

        DatabaseEntry longEntry = new DatabaseEntry();
        LongBinding.longToEntry(longID, longEntry);

        OperationStatus status = _primary.put(null, longEntry, stringEntry);
        if (status == OperationStatus.SUCCESS) {
            return longID;
        }

        // TODO: what to do while storing LongID -> StringID mapping failed
        if (_logger.isErrorEnabled()) {
            _logger.error(String.format(
                    "Unable to save StringID:%s as LongID:%d",
                    stringID, longID));
        }
        return longID;
    }

    @Override
    public String toStringID(long longID)  {
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();

        LongBinding.longToEntry(longID, key);
        OperationStatus status = _primary.get(null, key, data, LOCK_MODE);
        if (status == OperationStatus.SUCCESS) {
            return StringBinding.entryToString(data);
        }
        return null;
    }

    //
    //
    //

    protected Environment createEnvironment(String path, long cacheSize,
                                            boolean transactional) {
        EnvironmentConfig config = EnvironmentConfig.DEFAULT;
        config.setAllowCreate(true);
        config.setTransactional(transactional);
        config.setCacheSize(cacheSize);
        return new Environment(new File(path), config);
    }

    protected Database createPrimaryDatabase(Environment environment) {
        DatabaseConfig config = DatabaseConfig.DEFAULT;
        config.setAllowCreate(true);
        config.setTransactional(environment.getConfig().getTransactional());
        return environment.openDatabase(null, "id_string_mapping", config);
    }

    /**
     * Returns the BDB environment.
     * @return the environment
     */
    protected Environment getEnvironment() {
        return _environment;
    }

    /**
     * Returns LongID -> StringID mapping database
     * @return the database
     */
    protected Database getPrimaryDatabase() {
        return _primary;
    }

    //
    //
    //

    private Environment _environment;

    private Database _primary;

    protected final static LockMode LOCK_MODE = LockMode.READ_UNCOMMITTED;

    private final static Logger _logger =
            LoggerFactory.getLogger(BDBIDMigrator.class);
}
