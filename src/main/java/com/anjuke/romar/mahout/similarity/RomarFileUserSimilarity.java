package com.anjuke.romar.mahout.similarity;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.similarity.GenericUserSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.GenericUserSimilarity.UserUserSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.file.FileItemSimilarity;
import org.apache.mahout.cf.taste.similarity.PreferenceInferrer;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.romar.mahout.similarity.file.RomarFileSimilarityIterator.IteratorBuiler;
import com.anjuke.romar.mahout.util.Util;
import com.google.common.base.Preconditions;

public class RomarFileUserSimilarity implements UserSimilarity {
    private static final Logger LOG = LoggerFactory
            .getLogger(RomarFileUserSimilarity.class);

    private final IteratorBuiler<UserUserSimilarity> _iteratorBuilder;
    private final File _dataFile;
    private UserSimilarity _delegate;
    private final ReentrantLock _reloadLock;
    private long _lastModified;
    private final long _minReloadIntervalMS;

    /**
     * @param dataFile
     *            file containing the similarity data
     */
    public RomarFileUserSimilarity(File dataFile,
            IteratorBuiler<UserUserSimilarity> iteratorBuilder) {
        this(dataFile, iteratorBuilder, FileItemSimilarity.DEFAULT_MIN_RELOAD_INTERVAL_MS);
    }

    /**
     * @param minReloadIntervalMS
     *            the minimum interval in milliseconds after which a full reload
     *            of the original datafile is done when refresh() is called
     * @see #FileItemSimilarity(File)
     */
    public RomarFileUserSimilarity(File dataFile,
            IteratorBuiler<UserUserSimilarity> iteratorBuilder, long minReloadIntervalMS) {
        Preconditions.checkArgument(dataFile != null, "dataFile is null");
        Preconditions.checkArgument(dataFile.exists() && !dataFile.isDirectory(),
                "dataFile is missing or a directory: %s", dataFile);
        LOG.info("Creating FileItemSimilarity for file {}", dataFile);
        this._lastModified = dataFile.lastModified();
        this._dataFile = dataFile;
        this._iteratorBuilder = iteratorBuilder;
        this._minReloadIntervalMS = minReloadIntervalMS;
        this._reloadLock = new ReentrantLock();
        reload();
    }

    @Override
    public void setPreferenceInferrer(PreferenceInferrer inferrer) {
        _delegate.setPreferenceInferrer(inferrer);
    }

    @Override
    public double userSimilarity(long userID1, long userID2) throws TasteException {
        return _delegate.userSimilarity(userID1, userID2);
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        if (_dataFile.lastModified() > _lastModified + _minReloadIntervalMS) {
            LOG.debug("File has changed; reloading...");
            reload();
        }
    }

    protected void reload() {
        if (_reloadLock.tryLock()) {
            try {
                long newLastModified = _dataFile.lastModified();
                LOG.info("reading similarity from " + _dataFile.getAbsolutePath());
                _delegate = new GenericUserSimilarity(Util.iterable(_iteratorBuilder
                        .build(_dataFile)));
                LOG.info("read similarity finish");
                _lastModified = newLastModified;
            } finally {
                _reloadLock.unlock();
            }
        }
    }

    @Override
    public String toString() {
        return "FileItemSimilarity[dataFile:" + _dataFile + ']';
    }

}
