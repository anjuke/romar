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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity.ItemItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.GenericUserSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.GenericUserSimilarity.UserUserSimilarity;
import org.junit.Test;

import com.anjuke.romar.mahout.util.Util;
import com.google.common.collect.Iterators;

public class ReadableGenericSimilarityProxyTest {

    @Test
    public void testProxySimilarityGenericItemSimilarity() {
        Iterator<ItemItemSimilarity> it = Iterators
                .singletonIterator(new ItemItemSimilarity(1, 2, 0.8));
        GenericItemSimilarity similarity = new GenericItemSimilarity(Util.iterable(it));
        ReadableGenericItemSimilarity readable = ReadableGenericSimilarityProxy
                .proxySimilarity(similarity);

        FastByIDMap<FastByIDMap<Double>> idIdValueMap = readable.getSimilarityMaps();
        FastByIDMap<Double> id2ValueMap = idIdValueMap.get(1);

        assertNotNull(id2ValueMap);
        assertEquals(0.8, id2ValueMap.get(2).doubleValue(), 0.00001);
    }

    @Test
    public void testProxySimilarityGenericUserSimilarity() {
        Iterator<UserUserSimilarity> it = Iterators
                .singletonIterator(new UserUserSimilarity(1, 2, 0.8));
        GenericUserSimilarity similarity = new GenericUserSimilarity(Util.iterable(it));
        ReadableGenericUserSimilarity readable = ReadableGenericSimilarityProxy
                .proxySimilarity(similarity);

        FastByIDMap<FastByIDMap<Double>> idIdValueMap = readable.getSimilarityMaps();
        FastByIDMap<Double> id2ValueMap = idIdValueMap.get(1);

        assertNotNull(id2ValueMap);
        assertEquals(0.8, id2ValueMap.get(2).doubleValue(), 0.00001);
    }

}
