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
package com.anjuke.romar.core.handlers;

import java.util.List;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import com.anjuke.romar.core.RomarRequest;
import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.request.MultiItemIdRequest;
import com.anjuke.romar.core.impl.response.RecommendResultResponse;
import com.anjuke.romar.mahout.MahoutService;

public class ItemRecommendHandler extends BaseHandler {

    public ItemRecommendHandler(MahoutService service) {
        super(service);
    }

    @Override
    public RomarResponse process(RomarRequest request) throws Exception {
        MultiItemIdRequest mr = (MultiItemIdRequest) request;
        int howMany = mr.getLimit();
        if (howMany <= 0) {
            howMany = PreferenceBaseHandler.DEFAULT_HOW_MANY;
        }
        List<RecommendedItem> list = _service.mostSimilarItems(mr.getItemId(), howMany);
        return new RecommendResultResponse(list);
    }

}
