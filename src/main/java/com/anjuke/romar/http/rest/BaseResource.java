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
package com.anjuke.romar.http.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.romar.core.CoreContainer;
import com.anjuke.romar.core.RomarConfig;
import com.anjuke.romar.core.RomarCore;
import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.response.ErrorResponse;
import com.anjuke.romar.core.impl.response.MultiValueResponse;
import com.anjuke.romar.core.impl.response.RecommendResultResponse;
import com.anjuke.romar.http.rest.bean.RecommendBean;
import com.anjuke.romar.http.rest.bean.RecommendStringBean;
import com.anjuke.romar.http.rest.exception.InternalException;

abstract class BaseResource {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final RomarCore _romarCore = CoreContainer.getCore();

    private final boolean _allowStringID = RomarConfig.getInstance().isAllowStringID();

    long[] getItemIds(List<String> itemStrings) {
        try {
            int length = itemStrings.size();
            long[] items = new long[length];
            for (int i = 0; i < length; i++) {
                String value = itemStrings.get(i);
                if (_allowStringID) {
                    items[i] = _romarCore.getItemIdMigrator().toLongID(value);
                } else {
                    items[i] = Long.parseLong(value);
                }
            }
            return items;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new InternalException(new ErrorResponse(ErrorResponse.INTERNAL_ERROR,
                    "internal error: " + e.getMessage()));
        }

    }

    List<Object> wrapRecommendItem(RecommendResultResponse recommendResponse) {
        List<RecommendedItem> list = recommendResponse.getList();
        List<Object> result = new ArrayList<Object>();
        for (RecommendedItem item : list) {
            if (_allowStringID) {
                RecommendStringBean bean = new RecommendStringBean();
                bean.setItem(getItemString(item.getItemID()));
                bean.setValue(item.getValue());
                result.add(bean);
            } else {
                RecommendBean bean = new RecommendBean();
                bean.setItem(item.getItemID());
                bean.setValue(item.getValue());
                result.add(bean);
            }
        }
        return result;
    }

    String getItemString(long item) {
        try {
            return _romarCore.getItemIdMigrator().toStringID(item);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new InternalException(new ErrorResponse(ErrorResponse.INTERNAL_ERROR,
                    "internal error: " + e.getMessage()));
        }
    }

    String getUserString(long user) {
        try {
            return _romarCore.getUserIdMigrator().toStringID(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new InternalException(new ErrorResponse(ErrorResponse.INTERNAL_ERROR,
                    "internal error: " + e.getMessage()));
        }
    }

    long getItem(String itemString) {
        long item;
        try {
            if (_allowStringID) {
                item = _romarCore.getItemIdMigrator().toLongID(itemString);
            } else {
                item = Long.parseLong(itemString);
            }
            return item;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new InternalException(new ErrorResponse(ErrorResponse.INTERNAL_ERROR,
                    "internal error: " + e.getMessage()));
        }
    }

    long getUser(String userString) {
        long user;
        try {
            if (_allowStringID) {
                user = _romarCore.getUserIdMigrator().toLongID(userString);
            } else {
                user = Long.parseLong(userString);
            }
            return user;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new InternalException(new ErrorResponse(ErrorResponse.INTERNAL_ERROR,
                    "internal error: " + e.getMessage()));
        }
    }

    long[] getUserAndItem(String userString, String itemString) {
        long user;
        long item;
        try {
            if (_allowStringID) {
                user = _romarCore.getUserIdMigrator().toLongID(userString);
                item = _romarCore.getItemIdMigrator().toLongID(itemString);
            } else {
                user = Long.parseLong(userString);
                item = Long.parseLong(itemString);
            }
            return new long[] {user, item};
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new InternalException(new ErrorResponse(ErrorResponse.INTERNAL_ERROR,
                    "internal error: " + e.getMessage()));
        }
    }

    MultiValueResponse wrapMultiUserValues(MultiValueResponse response) {
        if (_allowStringID) {
            List<Long> values = (List<Long>) response.getValues();
            List<String> result = new ArrayList<String>(values.size());
            try {
                for (Long userId : values) {
                    result.add(getUserString(userId));
                }
                return new MultiValueResponse(result);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new InternalException(
                        new ErrorResponse(ErrorResponse.INTERNAL_ERROR,
                                "internal error: " + e.getMessage()));
            }

        } else {
            return response;
        }
    }

    void checkErrors(RomarResponse response) {
        if (response instanceof ErrorResponse) {
            throw new InternalException((ErrorResponse) response);
        }
    }
}
