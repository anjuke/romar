package com.anjuke.romar.mahout.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DiskOrderedCursorConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.ForwardCursor;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryCursor;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;
import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericItemPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BDBDataModel implements DataModel {

    public BDBDataModel(String directory, long cacheSize) {
        setMinPreference(Float.MIN_VALUE);
        setMaxPreference(Float.MAX_VALUE);
        _environment = createEnvironment(directory, cacheSize, false);
        _preferencesDB = createPreferencesDB(_environment);
        _preferencesFromUserDB = createPreferencesFromUserDB(_preferencesDB);
        _preferencesForItemDB = createPreferencesForItemDB(_preferencesDB);
        _usersDB = createUsersDB(_environment);
        _itemsDB = createItemsDB(_environment);
    }

    public void close() {
        if (_itemsDB != null) {
            _itemsDB.close();
            _itemsDB = null;
        }
        if (_usersDB != null) {
            _usersDB.close();
            _usersDB = null;
        }
        if (_preferencesForItemDB != null) {
            _preferencesForItemDB.close();
            _preferencesForItemDB = null;
        }
        if (_preferencesFromUserDB != null) {
            _preferencesFromUserDB.close();
            _preferencesFromUserDB = null;
        }
        if (_preferencesDB != null) {
            _preferencesDB.close();
            _preferencesDB = null;
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
    public LongPrimitiveIterator getUserIDs() throws TasteException {
        return new BDBLongPrimitiveIterator(_usersDB);
    }

    @Override
    public PreferenceArray getPreferencesFromUser(long userID)
            throws TasteException {
        List<Preference> prefs = new ArrayList<Preference>();

        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry pKey = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();

        LongBinding.longToEntry(userID, key);

        long itemID;
        float value;

        SecondaryCursor cursor = _preferencesFromUserDB.openCursor(null, null);
        try {
            OperationStatus status = cursor.getSearchKey(key, pKey, data,
                    LockMode.READ_UNCOMMITTED);
            if (status != OperationStatus.SUCCESS) {
                throw new NoSuchUserException(userID);
            }
            do {
                itemID = new TupleInput(pKey.getData(), PK_ITEM_ID_OFFSET,
                        LONG_BYTES).readLong();
                value = new TupleInput(data.getData()).readFloat();
                prefs.add(new GenericPreference(userID, itemID, value));
            } while (cursor.getNextDup(key, pKey, data,
                    LockMode.READ_UNCOMMITTED) == OperationStatus.SUCCESS);
        } catch (DatabaseException e) {
            throw new TasteException(e);
        } finally {
            cursor.close();
        }

        return new GenericUserPreferenceArray(prefs);
    }

    @Override
    public FastIDSet getItemIDsFromUser(long userID) throws TasteException {
        FastIDSet result = new FastIDSet();

        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry pKey = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();

        LongBinding.longToEntry(userID, key);

        long itemID;

        SecondaryCursor cursor = _preferencesFromUserDB.openCursor(null, null);
        try {
            OperationStatus status = cursor.getSearchKey(key, pKey, data,
                    LockMode.READ_UNCOMMITTED);
            if (status != OperationStatus.SUCCESS) {
                throw new NoSuchUserException(userID);
            }
            do {
                itemID = new TupleInput(pKey.getData(), PK_ITEM_ID_OFFSET,
                        LONG_BYTES).readLong();
                result.add(itemID);
            } while (cursor.getNextDup(key, pKey, data,
                    LockMode.READ_UNCOMMITTED) == OperationStatus.SUCCESS);
        } catch (DatabaseException e) {
            throw new TasteException(e);
        } finally {
            cursor.close();
        }

        return result;
    }

    @Override
    public LongPrimitiveIterator getItemIDs() throws TasteException {
        return new BDBLongPrimitiveIterator(_itemsDB);
    }

    @Override
    public PreferenceArray getPreferencesForItem(long itemID)
            throws TasteException {
        List<Preference> prefs = new ArrayList<Preference>();

        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry pKey = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();

        LongBinding.longToEntry(itemID, key);

        long userID;
        float value;

        SecondaryCursor cursor = _preferencesForItemDB.openCursor(null, null);
        try {
            OperationStatus status = cursor.getSearchKey(key, pKey, data,
                    LockMode.READ_UNCOMMITTED);
            if (status != OperationStatus.SUCCESS) {
                throw new NoSuchItemException(itemID);
            }
            do {
                userID = new TupleInput(pKey.getData(), PK_USER_ID_OFFSET,
                        LONG_BYTES).readLong();
                value = new TupleInput(data.getData()).readFloat();
                prefs.add(new GenericPreference(userID, itemID, value));
            } while (cursor.getNextDup(key, pKey, data,
                    LockMode.READ_UNCOMMITTED) == OperationStatus.SUCCESS);
        } catch (DatabaseException e) {
            throw new TasteException(e);
        } finally {
            cursor.close();
        }

        return new GenericItemPreferenceArray(prefs);
    }

    @Override
    public Float getPreferenceValue(long userID, long itemID)
            throws TasteException {
        DatabaseEntry key = preferencePK(userID, itemID);
        DatabaseEntry data = new DatabaseEntry();
        OperationStatus status = _preferencesDB.get(null, key, data,
                LockMode.READ_UNCOMMITTED);
        if (status == OperationStatus.SUCCESS) {
            return new TupleInput(data.getData(), DATA_VALUE_OFFSET,
                    FLOAT_BYTES).readFloat();
        }
        return null;
    }

    @Override
    public Long getPreferenceTime(long userID, long itemID)
            throws TasteException {
        DatabaseEntry key = preferencePK(userID, itemID);
        DatabaseEntry data = new DatabaseEntry();
        OperationStatus status = _preferencesDB.get(null, key, data,
                LockMode.READ_UNCOMMITTED);
        if (status == OperationStatus.SUCCESS) {
            return new TupleInput(data.getData(), DATA_TIMESTAMP_OFFSET,
                    TIMESTAMP_BYTES).readUnsignedInt();
        }
        return null;
    }

    @Override
    public int getNumItems() throws TasteException {
        return (int)_itemsDB.count();
    }

    @Override
    public int getNumUsers() throws TasteException {
        return (int)_usersDB.count();
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID)
            throws TasteException {
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();

        LongBinding.longToEntry(itemID, key);

        Cursor cursor = _preferencesForItemDB.openCursor(null, null);
        try {
            OperationStatus status = cursor.getSearchKey(key, data,
                    LockMode.READ_UNCOMMITTED);
            if (status == OperationStatus.SUCCESS) {
                return cursor.count();
            } else {
                return 0;
            }
        } finally {
            cursor.close();
        }
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID1, long itemID2)
            throws TasteException {
        PreferenceArray prefs1 = getPreferencesForItem(itemID1);
        if (prefs1 == null) {
            return 0;
        }
        PreferenceArray prefs2 = getPreferencesForItem(itemID2);
        if (prefs2 == null) {
            return 0;
        }

        int size1 = prefs1.length();
        int size2 = prefs2.length();
        int count = 0;
        int i = 0;
        int j = 0;
        long userID1 = prefs1.getUserID(0);
        long userID2 = prefs2.getUserID(0);
        while (true) {
            if (userID1 < userID2) {
                if (++i == size1) {
                    break;
                }
                userID1 = prefs1.getUserID(i);
            } else if (userID1 > userID2) {
                if (++j == size2) {
                    break;
                }
                userID2 = prefs2.getUserID(j);
            } else {
                count++;
                if (++i == size1 || ++j == size2) {
                    break;
                }
                userID1 = prefs1.getUserID(i);
                userID2 = prefs2.getUserID(j);
            }
        }
        return count;
    }

    @Override
    public void setPreference(long userID, long itemID, float value)
            throws TasteException {
        _preferencesDB.put(null, preferencePK(userID, itemID),
                preferenceData(value));

        DatabaseEntry userIDEntry = new DatabaseEntry();
        LongBinding.longToEntry(userID, userIDEntry);
        _usersDB.put(null, userIDEntry, EMPTY_ENTRY);

        DatabaseEntry itemIDEntry = new DatabaseEntry();
        LongBinding.longToEntry(itemID, itemIDEntry);
        _itemsDB.put(null, itemIDEntry, EMPTY_ENTRY);
    }

    @Override
    public void removePreference(long userID, long itemID)
            throws TasteException {
        OperationStatus status = _preferencesDB.delete(null,
                preferencePK(userID, itemID));
        if (status != OperationStatus.SUCCESS) {
            // TODO: NOT_FOUND?
            return;
        }

        if (getNumUsersWithPreferenceFor(itemID) <= 0) {
            DatabaseEntry entry = new DatabaseEntry();
            LongBinding.longToEntry(itemID, entry);
            _itemsDB.delete(null, entry);
        }


        if (getNumItemsWithPreferenceFrom(userID) <= 0) {
            DatabaseEntry entry = new DatabaseEntry();
            LongBinding.longToEntry(userID, entry);
            _usersDB.delete(null, entry);
        }
    }

    @Override
    public boolean hasPreferenceValues() {
        return true;
    }

    @Override
    public float getMaxPreference() {
        return _maxPreference;
    }

    @Override
    public float getMinPreference() {
        return _minPreference;
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        _logger.debug("refreshing");
    }

    //
    //
    //

    public void setMaxPreference(float value) {
        _maxPreference = value;
    }

    public void setMinPreference(float value) {
        _minPreference = value;
    }

    public void removeUser(long userID) throws TasteException {
        // TODO: it's ugly and slow, should be removed from database directly
        for (long itemID : getItemIDsFromUser(userID)) {
            removePreference(userID, itemID);
        }
    }

    public void removeItem(long itemID) throws TasteException {
        // TODO: it's ugly and slow, should be removed from database directly
        for (Preference p : getPreferencesForItem(itemID)) {
            removePreference(p.getUserID(), itemID);
        }
    }

    //
    //
    //

    protected int getNumItemsWithPreferenceFrom(long userID) {
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();

        LongBinding.longToEntry(userID, key);

        Cursor cursor = _preferencesFromUserDB.openCursor(null, null);
        try {
            OperationStatus status = cursor.getSearchKey(key, data,
                    LockMode.READ_UNCOMMITTED);
            if (status == OperationStatus.SUCCESS) {
                return cursor.count();
            } else {
                return 0;
            }
        } finally {
            cursor.close();
        }
    }

    protected Environment createEnvironment(String path, long cacheSize,
                                            boolean transactional) {
        EnvironmentConfig config = EnvironmentConfig.DEFAULT;
        config.setAllowCreate(true);
        config.setTransactional(transactional);
        config.setCacheSize(cacheSize);
        return new Environment(new File(path), config);
    }

    // Primary db
    protected Database createPreferencesDB(Environment environment) {
        DatabaseConfig config = DatabaseConfig.DEFAULT;
        config.setAllowCreate(true);
        config.setTransactional(environment.getConfig().getTransactional());
        return environment.openDatabase(null, "preferences", config);
    }

    // Many-to-one
    // UserID:ItemID -> PreferenceValue to UserID -> ItemID:PreferenceValue
    protected SecondaryDatabase createPreferencesFromUserDB(
            Database preferencesDB) {
        Environment environment = preferencesDB.getEnvironment();
        SecondaryConfig config = SecondaryConfig.DEFAULT;
        config.setAllowCreate(true);
        config.setTransactional(environment.getConfig().getTransactional());
        config.setSortedDuplicates(true);
        config.setKeyCreator(new SecondaryKeyCreator() {
            @Override
            public boolean createSecondaryKey(SecondaryDatabase secondary,
                                              DatabaseEntry key,
                                              DatabaseEntry data,
                                              DatabaseEntry result) {
                try {
                    result.setData(key.getData(), PK_USER_ID_OFFSET,
                            LONG_BYTES);
                } catch (Throwable e) {
                    _logger.error(e.getMessage());
                    return false;
                }
                return true;
            }
        });
        return environment.openSecondaryDatabase(null, "user_preferences",
                preferencesDB, config);
    }

    // Many-to-one
    // UserID:ItemID -> PreferenceValue to ItemID -> UserID:PreferenceValue
    protected SecondaryDatabase createPreferencesForItemDB(
            Database preferencesDB) {
        Environment environment = preferencesDB.getEnvironment();
        SecondaryConfig config = SecondaryConfig.DEFAULT;
        config.setAllowCreate(true);
        config.setTransactional(environment.getConfig().getTransactional());
        config.setSortedDuplicates(true);
        config.setKeyCreator(new SecondaryKeyCreator() {
            @Override
            public boolean createSecondaryKey(SecondaryDatabase secondary,
                                              DatabaseEntry key,
                                              DatabaseEntry data,
                                              DatabaseEntry result) {
                try {
                    result.setData(key.getData(), PK_ITEM_ID_OFFSET,
                            LONG_BYTES);
                } catch (Throwable e) {
                    _logger.error(e.getMessage());
                    return false;
                }
                return true;
            }
        });
        return environment.openSecondaryDatabase(null, "item_preferences",
                preferencesDB, config);
    }

    protected Database createUsersDB(Environment environment) {
        DatabaseConfig config = DatabaseConfig.DEFAULT;
        config.setAllowCreate(true);
        config.setTransactional(environment.getConfig().getTransactional());
        return environment.openDatabase(null, "users", config);
    }

    protected Database createItemsDB(Environment environment) {
        DatabaseConfig config = DatabaseConfig.DEFAULT;
        config.setAllowCreate(true);
        config.setTransactional(environment.getConfig().getTransactional());
        return environment.openDatabase(null, "items", config);
    }

    protected static DatabaseEntry preferencePK(long userID, long itemID) {
        byte[] bytes = new byte[LONG_BYTES * 2];
        TupleOutput output = new TupleOutput(bytes);
        output.writeLong(userID).writeLong(itemID);
        return new DatabaseEntry(bytes);
    }

    protected static DatabaseEntry preferenceData(float value) {
        byte[] bytes = new byte[FLOAT_BYTES + TIMESTAMP_BYTES];
        TupleOutput output = new TupleOutput(bytes);
        output.writeFloat(value).writeUnsignedInt(System.currentTimeMillis());
        return new DatabaseEntry(bytes);
    }

    protected Environment getEnvironment() {
        return _environment;
    }

    protected Database getPreferencesDB() {
        return _preferencesDB;
    }

    protected SecondaryDatabase getPreferencesFromUserDB() {
        return _preferencesFromUserDB;
    }

    protected SecondaryDatabase getPreferencesForItemDB() {
        return _preferencesForItemDB;
    }

    protected Database getUsersDB() {
        return _usersDB;
    }

    protected Database getItemsDB() {
        return _itemsDB;
    }

    //
    //
    //

    private float _maxPreference;

    private float _minPreference;

    private Environment _environment;

    // UserID:ItemID -> PreferenceValue (primary)
    private Database _preferencesDB;

    // UserID -> NaN
    private Database _usersDB;
    // ItemID -> NaN
    private Database _itemsDB;

    // UserID -> ItemID:PreferenceValue (secondary)
    private SecondaryDatabase _preferencesFromUserDB;

    // ItemID -> UserID:PreferenceValue (secondary)
    private SecondaryDatabase _preferencesForItemDB;

    private final static DatabaseEntry EMPTY_ENTRY =
            new DatabaseEntry(new byte[0]);

    private final static int PK_USER_ID_OFFSET = 0;
    private final static int PK_ITEM_ID_OFFSET = 8;
    private final static int LONG_BYTES = 8;
    private final static int FLOAT_BYTES = 4;
    private final static int TIMESTAMP_BYTES = 4;
    private final static int DATA_VALUE_OFFSET = 0;
    private final static int DATA_TIMESTAMP_OFFSET = 4;

    private final static Logger _logger = LoggerFactory.getLogger(
            BDBDataModel.class);

    //
    //
    //

    static class BDBLongPrimitiveIterator  implements LongPrimitiveIterator {

        BDBLongPrimitiveIterator(Database database) {
            DiskOrderedCursorConfig config = DiskOrderedCursorConfig.DEFAULT
                    .setKeysOnly(true);
            _cursor = database.openCursor(config);
            doFetchNext();
        }

        public void close() {
            if (_cursor != null) {
                _cursor.close();
                _cursor = null;
            }
        }

        @Override
        protected void finalize() throws Throwable {
            close();
            super.finalize();
        }

        protected boolean doFetchNext() {
            if (_cursor == null) {
                throw new IllegalStateException(
                        "cursor for the iterator has been closed");
            }
            DatabaseEntry key = new DatabaseEntry();
            DatabaseEntry data = new DatabaseEntry();
            OperationStatus status = _cursor.getNext(key, data,
                    LockMode.READ_UNCOMMITTED);
            if (status == OperationStatus.SUCCESS) {
                _current = LongBinding.entryToLong(key);
                return true;
            } else {
                close();
                return false;
            }
        }

        @Override
        public long nextLong() {
            long result = _current;
            doFetchNext();
            return result;
        }

        @Override
        public long peek() {
            return _current;
        }

        @Override
        public void skip(int n) {
            for (int i = 0; i < n; i++) {
                if (!doFetchNext()) {
                    break;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return _cursor != null;
        }

        @Override
        public Long next() {
            return nextLong();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private ForwardCursor _cursor;
        private long _current;
    }
}
