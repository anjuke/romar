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
package com.anjuke.romar.core.impl.response;

import java.util.Collections;
import java.util.List;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import com.anjuke.romar.core.RomarResponse;

public class RecommendResultResponse implements RomarResponse{
    private final List<RecommendedItem> _list;

    public RecommendResultResponse(List<RecommendedItem> list) {
        super();
        _list = Collections.unmodifiableList(list);
    }

    public List<RecommendedItem> getList() {
        return _list;
    }

}
