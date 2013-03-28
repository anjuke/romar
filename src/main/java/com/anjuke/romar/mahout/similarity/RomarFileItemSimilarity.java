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
package com.anjuke.romar.mahout.similarity;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity.ItemItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.file.FileItemSimilarity;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.romar.mahout.similarity.file.RomarFileSimilarityIterator.IteratorBuiler;
import com.anjuke.romar.mahout.util.Util;
import com.google.common.base.Preconditions;

public class RomarFileItemSimilarity implements ItemSimilarity {
    private static final Logger LOG = LoggerFactory
            .getLogger(RomarFileItemSimilarity.class);

    private final IteratorBuiler<ItemItemSimilarity> _iteratorBuilder;
    private final File _dataFile;
    private ItemSimilarity _delegate;
    private final ReentrantLock _reloadLock;
    private long _lastModified;
    private final long _minReloadIntervalMS;

    /**
     * @param dataFile
     *            file containing the similarity data
     */
    public RomarFileItemSimilarity(File dataFile,
            IteratorBuiler<ItemItemSimilarity> iteratorBuilder) {
        this(dataFile, iteratorBuilder, FileItemSimilarity.DEFAULT_MIN_RELOAD_INTERVAL_MS);
    }

    /**
     * @param minReloadIntervalMS
     *            the minimum interval in milliseconds after which a full reload
     *            of the original datafile is done when refresh() is called
     * @see #FileItemSimilarity(File)
     */
    public RomarFileItemSimilarity(File dataFile,
            IteratorBuiler<ItemItemSimilarity> iteratorBuilder, long minReloadIntervalMS) {
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
    public double[] itemSimilarities(long itemID1, long[] itemID2s) throws TasteException {
        return _delegate.itemSimilarities(itemID1, itemID2s);
    }

    @Override
    public long[] allSimilarItemIDs(long itemID) throws TasteException {
        return _delegate.allSimilarItemIDs(itemID);
    }

    @Override
    public double itemSimilarity(long itemID1, long itemID2) throws TasteException {
        return _delegate.itemSimilarity(itemID1, itemID2);
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
                _delegate = new GenericItemSimilarity(Util.iterable(_iteratorBuilder
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
