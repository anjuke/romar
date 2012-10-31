package com.anjuke.romar.mahout;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;

public class RecommenderWrapper implements MahoutService {
    private final Recommender _recommender;

    private final ReadWriteLock _lock = new ReentrantReadWriteLock();

    private final Lock _readLock = _lock.readLock();

    private final Lock _writeLock = _lock.readLock();

    public RecommenderWrapper(Recommender recommender) {
        super();
        _recommender = recommender;
    }

    @Override
    public List<RecommendedItem> recommend(long userID, int howMany)
            throws TasteException {
        _readLock.lock();
        try {
            return _recommender.recommend(userID, howMany);
        } finally {
            _readLock.unlock();
        }
    }

    @Override
    public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer)
            throws TasteException {
        _readLock.lock();
        try {
            return _recommender.recommend(userID, howMany, rescorer);
        } finally {
            _readLock.unlock();
        }
    }

    @Override
    public float estimatePreference(long userID, long itemID) throws TasteException {
        _readLock.lock();
        try {
            return _recommender.estimatePreference(userID, itemID);
        } finally {
            _readLock.unlock();
        }
    }

    @Override
    public void setPreference(long userID, long itemID, float value)
            throws TasteException {
        _writeLock.lock();
        try {
            _recommender.setPreference(userID, itemID, value);
        } finally {
            _writeLock.unlock();
        }
    }

    @Override
    public void removePreference(long userID, long itemID) throws TasteException {
        _writeLock.lock();
        try {
            _recommender.removePreference(userID, itemID);
        } finally {
            _writeLock.unlock();
        }
    }

    @Override
    public DataModel getDataModel() {
        return _recommender.getDataModel();
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
//        _writeLock.lock();
//        try {
            _recommender.refresh(alreadyRefreshed);
//        } finally {
//            _writeLock.unlock();
//        }
    }

    @Override
    public List<RecommendedItem> mostSimilarItems(long itemID, int howMany)
            throws TasteException {
        if (_recommender instanceof ItemBasedRecommender) {
            _readLock.lock();
            try {
                return ((ItemBasedRecommender) _recommender).mostSimilarItems(itemID,
                        howMany);
            } finally {
                _readLock.unlock();
            }
        } else {
            throw new UnsupportedOperationException("ItemBasedRecommender not supported");
        }
    }

    @Override
    public List<RecommendedItem> mostSimilarItems(long[] itemIDs, int howMany)
            throws TasteException {
        if (_recommender instanceof ItemBasedRecommender) {
            _readLock.lock();
            try {
                return ((ItemBasedRecommender) _recommender).mostSimilarItems(itemIDs,
                        howMany);
            } finally {
                _readLock.unlock();
            }
        } else {
            throw new UnsupportedOperationException("ItemBasedRecommender not supported");
        }
    }

    @Override
    public List<RecommendedItem> mostSimilarItems(long[] itemIDs, int howMany,
            boolean excludeItemIfNotSimilarToAll) throws TasteException {
        if (_recommender instanceof ItemBasedRecommender) {
            _readLock.lock();
            try {
                return ((ItemBasedRecommender) _recommender).mostSimilarItems(itemIDs,
                        howMany, excludeItemIfNotSimilarToAll);
            } finally {
                _readLock.unlock();
            }
        } else {
            throw new UnsupportedOperationException("ItemBasedRecommender not supported");
        }
    }

    @Override
    public long[] mostSimilarUserIDs(long userID, int howMany) throws TasteException {
        if (_recommender instanceof UserBasedRecommender) {
            _readLock.lock();
            try {
                return ((UserBasedRecommender) _recommender).mostSimilarUserIDs(userID,
                        howMany);
            } finally {
                _readLock.unlock();
            }
        } else {
            throw new UnsupportedOperationException("UserBasedRecommender not supported");
        }
    }

    @Override
    public void removeUser(long userID) throws TasteException {
        DataModel dataModel = _recommender.getDataModel();
        _writeLock.lock();
        try {
            for (long itemID : dataModel.getItemIDsFromUser(userID)) {
                removePreference(userID, itemID);
            }
            this.refresh(null);
        } finally {
            _writeLock.unlock();
        }
    }

    @Override
    public void removeItem(long itemID) throws TasteException {
        DataModel dataModel = _recommender.getDataModel();
        _writeLock.lock();
        try {
            for (Preference p : dataModel.getPreferencesForItem(itemID)) {
                removePreference(p.getUserID(), itemID);
            }
            this.refresh(null);
        } finally {
            _writeLock.unlock();
        }
    }
}
