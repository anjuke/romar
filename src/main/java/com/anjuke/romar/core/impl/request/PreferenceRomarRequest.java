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
package com.anjuke.romar.core.impl.request;

import com.anjuke.romar.core.RequestPath;
import com.anjuke.romar.core.RomarRequest;

public class PreferenceRomarRequest  extends BaseRequest implements RomarRequest {
    private long _userId;
    private long _itemId;
    private float _value;
    private int _limit;

    public PreferenceRomarRequest(RequestPath path) {
        super(path);
    }

    public long getUserId() {
        return _userId;
    }

    public long getItemId() {
        return _itemId;
    }

    public float getValue() {
        return _value;
    }

    public void setUserId(long userId) {
        _userId = userId;
    }

    public void setItemId(long itemId) {
        _itemId = itemId;
    }

    public void setValue(float preference) {
        _value = preference;
    }

    public int getLimit() {
        return _limit;
    }

    public void setLimit(int limit) {
        _limit = limit;
    }

}
