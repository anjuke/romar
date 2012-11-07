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
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BDBIDMigratorTest {
    public BDBIDMigratorTest() {
        _logger = LoggerFactory.getLogger(this.getClass());
    }

    protected BDBIDMigrator createIDMigrator(String path) {
        return new BDBIDMigrator(path, CACHE_SIZE);
    }

    @Before
    public void setUp() throws IOException {
        _bdbDir = BDBTestUtils.createTempDir();
        _idMigrator = createIDMigrator(_bdbDir.getAbsolutePath());
    }

    @After
    public void TearDown() throws IOException {
        _idMigrator.close();
        BDBTestUtils.deleteRecursively(_bdbDir);
    }

    @Test
    public void differentStringShouldTransformedToDifferentId() {
        final int COUNT = 10;
        Set<Long> idSet = new HashSet<Long>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            long id = _idMigrator.toLongID("Anjuke - " + i);
            idSet.add(id);
        }
        Assert.assertEquals(COUNT, idSet.size());
    }

    @Test
    public void sameStringShouldTransformedToSameId() {
        final int COUNT = 10;

        Set<Long> idSet = new HashSet<Long>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            long id = _idMigrator.toLongID("Anjuke - *");
            idSet.add(id);
        }
        Assert.assertEquals(1, idSet.size());
    }

    @Test
    public void transformNotExistIdShouldReturnNull() {
        Assert.assertNull(_idMigrator.toStringID(37));
        Random random = new Random(System.currentTimeMillis());
        Assert.assertNull(_idMigrator.toStringID(random.nextLong()));
    }

    @Test
    public void transformExistIdShouldReturnCorrectString() {
        final int COUNT = 10;
        Map<Long, String> map = new HashMap<Long, String>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            String stringId = "Anjuke - " + i;
            long id = _idMigrator.toLongID(stringId);
            map.put(id, stringId);
        }
        // we assume the above transform from string to id is correct

        for (Map.Entry<Long, String> entry : map.entrySet()) {
            String stringId = _idMigrator.toStringID(entry.getKey());
            Assert.assertEquals(entry.getValue(), stringId);
        }
    }

    @Test
    public void benchmark() {
        final int COUNT = 1000;
        Set<Long> idSet = new HashSet<Long>(COUNT);

        // not exist stirng to long
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            long id = _idMigrator.toLongID("Anjuke - " + i);
            idSet.add(id);
        }

        // exist long to string
        long t2 = System.currentTimeMillis();
        for (long id : idSet) {
            _idMigrator.toStringID(id);
        }

        // exist string to long
        long t3 = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            _idMigrator.toLongID("Anjuke - " + i);
        }

        // not exist long to string
        long t4 = System.currentTimeMillis();
        for (long id : idSet) {
            Assert.assertNull(_idMigrator.toStringID(-id));
        }
        long t5 = System.currentTimeMillis();

        _logger.info("COUNT:" + COUNT + ", total time: " + (t5 - t1) / 1000.0);
        _logger.info(String.format("not exist string to long: around %d transform per second", Math.round((double) COUNT / (t2 - t1) * 1000)));
        _logger.info(String.format("    exist long to string: around %d transform per second", Math.round((double) COUNT / (t3 - t2) * 1000)));
        _logger.info(String.format("    exist string to long: around %d transform per second", Math.round((double) COUNT / (t4 - t3) * 1000)));
        _logger.info(String.format("not exist long to string: around %d transform per second", Math.round((double) COUNT / (t5 - t4) * 1000)));
    }

    //
    //
    //

    private File _bdbDir;
    private BDBIDMigrator _idMigrator;
    protected static final long CACHE_SIZE = 1024 * 1024 * 32;

    private final Logger _logger;
}
