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
package com.anjuke.romar.mahout;

import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

public interface MahoutService extends Recommender{

    List<RecommendedItem> mostSimilarItems(long itemID, int howMany) throws TasteException;

    List<RecommendedItem> mostSimilarItems(long[] itemIDs, int howMany) throws TasteException;

    List<RecommendedItem> mostSimilarItems(long[] itemIDs,
            int howMany,
            boolean excludeItemIfNotSimilarToAll) throws TasteException;


    long[] mostSimilarUserIDs(long userID, int howMany) throws TasteException;

    void removeUser(long userID) throws TasteException;

    void removeItem(long userID) throws TasteException;

}
