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
package com.anjuke.romar.mahout.persistence;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.fs.FileUtil;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FilePreferenceSourceTest {
    private final File path = new File("/tmp/romar/test");
    FilePreferenceSource source;

    @Before
    public void setUp() throws Exception {
        source = new FilePreferenceSource(path);
    }

    @After
    public void tearDown() throws Exception {
        source.close();
        for(File f:path.listFiles()){
            f.delete();
        }
    }

    @Test
    public void testFilePreferenceSourceFileString() throws Exception {
        tearDown();
        FilePreferenceSource file = new FilePreferenceSource(path);
        String[] list = path.list();
        String name = list[0];
        assertTrue(name
                .startsWith(AbstractFilePreferenceSource.LOG_FILE_PREFIX));
        assertEquals("0", name.substring(
                AbstractFilePreferenceSource.LOG_FILE_PREFIX.length()));
    }

    @Test
    public void testAddPreference() throws IOException {
        source.setPreference(1, 1, 1.0f);
        File file = source.getLatestLogFile();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file)));
        String line = reader.readLine();
        assertNotNull(line);
        assertEquals("1,1,1.0", line);
        reader.close();
    }

    @Test
    public void testRemovePreference() throws IOException {
        source.removePreference(1, 1);
        File file = source.getLatestLogFile();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file)));
        String line = reader.readLine();
        assertNotNull(line);
        assertEquals("1,1,", line);
        reader.close();
    }

    @Test
    public void testGetPreferenceData() {
        source.setPreference(2, 3, 2);
        source.removePreference(2, 3);
        source.setPreference(1, 1, 2);
        source.setPreference(3, 4, 2);
        source.setPreference(3, 5, 3);
        source.commit();
        source.setPreference(5, 4, 1.0f);
        source.compact();
        source.close();
        source=new FilePreferenceSource(path);
        FastByIDMap<PreferenceArray> data=source.getPreferenceUserData();
        PreferenceArray id2=data.get(2);
        assertNull(id2);
        PreferenceArray id1=data.get(1);
        assertNotNull(id1);
        assertEquals(1,id1.getItemID(0));
        assertEquals(2.0f,id1.getValue(0),0f);

        PreferenceArray id3=data.get(3);
        assertNotNull(id3);
        assertEquals(4,id3.getItemID(0));
        assertEquals(2.0f,id3.getValue(0),0f);
        assertEquals(5,id3.getItemID(1));
        assertEquals(3.0f,id3.getValue(1),0f);

        PreferenceArray id5=data.get(5);
        assertNotNull(id5);
        assertEquals(4,id5.getItemID(0));
        assertEquals(1.0f,id5.getValue(0),0f);

    }

    @Test
    public void testCompact() throws IOException {
        source.setPreference(2, 3, 2);
        source.removePreference(2, 3);
        source.setPreference(1, 1, 2);
        source.setPreference(3, 4, 2);
        source.commit();
        source.setPreference(5, 4, 2);
        source.compact();
        File file = source.getSnapshotFile(0);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file)));
        String line = reader.readLine();
        assertNotNull(line);
        assertEquals("1,1,2.0", line);
        line = reader.readLine();
        assertNotNull(line);
        assertEquals("3,4,2.0", line);
        line = reader.readLine();
        assertNull(line);
        reader.close();
    }

    @Test
    public void testCommit() throws IOException {
        source.setPreference(1, 1, 1);
        source.commit();
        File file = source.getLatestLogFile();
        String name = file.getName();
        assertEquals("1", name.substring(
                AbstractFilePreferenceSource.LOG_FILE_PREFIX.length()));
        assertEquals(2, source.listLogFileNamesAndSorted().size());
        source.setPreference(1, 1, 1);
        source.commit();
        file = source.getLatestLogFile();
        name = file.getName();
        assertEquals("2", name.substring(
                AbstractFilePreferenceSource.LOG_FILE_PREFIX.length()));
        assertEquals(3, source.listLogFileNamesAndSorted().size());
        source.setPreference(1, 1, 1.0f);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file)));
        String line = reader.readLine();
        assertNotNull(line);
        assertEquals("1,1,1.0", line);
        reader.close();
    }


    @Test
    public void testContinuousCompactAndCommit(){
        source.setPreference(1, 1, 1);
        source.commit();
        source.compact();
        assertEquals(2, source.listLogFileNamesAndSorted().size());
        assertEquals(1, source.listSnapshotFileNamesAndSorted().size());
        assertEquals(1,source.getLogFileVersion(source.getLatestLogFile()));
        assertEquals(0,source.getSnapshotFileVersion(source.getLatestSnapshotFile()));
        source.commit();
        source.compact();
        assertEquals(2, source.listLogFileNamesAndSorted().size());
        assertEquals(1, source.listSnapshotFileNamesAndSorted().size());
        source.commit();
        source.compact();
        assertEquals(2, source.listLogFileNamesAndSorted().size());
        assertEquals(1, source.listSnapshotFileNamesAndSorted().size());
        source.commit();
        source.compact();
        assertEquals(2, source.listLogFileNamesAndSorted().size());
        assertEquals(1, source.listSnapshotFileNamesAndSorted().size());
        assertEquals(1,source.getLogFileVersion(source.getLatestLogFile()));
        assertEquals(0,source.getSnapshotFileVersion(source.getLatestSnapshotFile()));
    }

}
