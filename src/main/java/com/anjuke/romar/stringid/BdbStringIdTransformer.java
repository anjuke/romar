package com.anjuke.romar.stringid;

import java.io.File;

import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.bind.tuple.TupleTupleKeyCreator;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.Sequence;
import com.sleepycat.je.SequenceConfig;

public class BdbStringIdTransformer implements StringIdTransformer {

    /**
     *
     * @param directory the directory where the data will be persist to
     * @param cacheSize The size of cache for the environment
     *
     * @see BdbStringIdTransformer
     */
    public BdbStringIdTransformer(String directory, long cacheSize) {
        this(directory, cacheSize, false);
    }

    /**
     *
     * @param directory the directory where the data will be persist to
     * @param cacheSize The size of cache for the environment
     * @param transactional enable transaction or not
     */
    public BdbStringIdTransformer(String directory, long cacheSize,
            boolean transactional) {
        _environment = createEnvironment(directory, cacheSize, transactional);
        _primary = createPrimaryDatabase(_environment);
        _secondary = createSecondaryDatabase(_environment, _primary);
        _sequence = createIdSequence(_primary);
    }

    public void Close() {
        if (null == _environment) {
            return;
        }
        _sequence.close();
        _secondary.close();
        _primary.close();
        _environment.close();
        _sequence = null;
        _secondary = null;
        _primary = null;
        _environment = null;
    }

    @Override
    protected void finalize() throws Throwable {
        Close();
        super.finalize();
    }

    @Override
    public long transform(String value) {
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();
        DatabaseEntry primaryKey = new DatabaseEntry();

        StringBinding.stringToEntry(value, key);
        OperationStatus status = _secondary.get(null, key, primaryKey, data,
                LOCK_MODE);
        if (OperationStatus.SUCCESS == status) {
            return LongBinding.entryToLong(primaryKey);
        }

        long next = _sequence.get(null, 1);
        LongBinding.longToEntry(next, primaryKey);
        status = _primary.put(null, primaryKey, key);
        return next;
    }

    @Override
    public String transform(long value) {
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();

        LongBinding.longToEntry(value, key);
        OperationStatus status = _primary.get(null, key, data, LOCK_MODE);
        if (OperationStatus.SUCCESS == status) {
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

    //
    //
    //

    private Environment _environment;
    private Database _primary;
    private SecondaryDatabase _secondary;
    private Sequence _sequence;

    private final static LockMode LOCK_MODE = LockMode.DEFAULT;
}
