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
package com.anjuke.romar.mahout.similarity.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity.ItemItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.GenericUserSimilarity.UserUserSimilarity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;

public class RomarFileSimilarityIteratorTest {

    private File file;

    @Before
    public void before() throws IOException {
        file = File.createTempFile(
                this.getClass().getName() + String.valueOf(System.nanoTime()), null);
    }

    private void initLineFile() throws IOException {
        Files.write("1,3,0.8\n1	3	0.8".getBytes(), file);
    }

    private void initDataFile() throws IOException {
        byte[] data = new byte[RomarFileSimilarityIterator.DATA_SIZE];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.putLong(1l);
        buffer.putLong(3l);
        buffer.putDouble(0.8);

        Files.write(data, file);
    }

    @After
    public void after() {
        file.delete();
        file = null;
    }

    @Test
    public void testLineFileItemIterator() throws IOException {
        initLineFile();
        Iterator<ItemItemSimilarity> it = RomarFileSimilarityIterator
                .lineFileItemIterator(file);
        assertTrue(it.hasNext());
        ItemItemSimilarity s = it.next();
        assertEquals(1, s.getItemID1());
        assertEquals(3, s.getItemID2());
        assertEquals(0.8, s.getValue(), 0.00001);
        s = it.next();
        assertEquals(1, s.getItemID1());
        assertEquals(3, s.getItemID2());
        assertEquals(0.8, s.getValue(), 0.00001);
        assertFalse(it.hasNext());
    }

    @Test
    public void testDataFileItemIterator() throws IOException {
        initDataFile();
        Iterator<ItemItemSimilarity> it = RomarFileSimilarityIterator
                .dataFileItemIterator(file);
        assertTrue(it.hasNext());
        ItemItemSimilarity s = it.next();
        assertEquals(1, s.getItemID1());
        assertEquals(3, s.getItemID2());
        assertEquals(0.8, s.getValue(), 0.00001);
        assertFalse(it.hasNext());
    }

    @Test
    public void testLineFileUserIterator() throws IOException {
        initLineFile();
        Iterator<UserUserSimilarity> it = RomarFileSimilarityIterator
                .lineFileUserIterator(file);
        assertTrue(it.hasNext());
        UserUserSimilarity s = it.next();
        assertEquals(1, s.getUserID1());
        assertEquals(3, s.getUserID2());
        assertEquals(0.8, s.getValue(), 0.00001);
        s = it.next();
        assertEquals(1, s.getUserID1());
        assertEquals(3, s.getUserID2());
        assertEquals(0.8, s.getValue(), 0.00001);
        assertFalse(it.hasNext());
    }

    @Test
    public void testDataFileUserIterator() throws IOException {
         initDataFile();
         Iterator<UserUserSimilarity> it = RomarFileSimilarityIterator
                 .dataFileUserIterator(file);
         assertTrue(it.hasNext());
         UserUserSimilarity s = it.next();
         assertEquals(1, s.getUserID1());
         assertEquals(3, s.getUserID2());
         assertEquals(0.8, s.getValue(), 0.00001);
         assertFalse(it.hasNext());
    }

}
