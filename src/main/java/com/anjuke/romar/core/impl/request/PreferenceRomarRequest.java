package com.anjuke.romar.core.impl.request;

import com.anjuke.romar.core.RequestPath;
import com.anjuke.romar.core.RomarRequest;

public class PreferenceRomarRequest  extends BaseRequest implements RomarRequest {
    private long _userId;
    private long _itemId;
    private float _value;

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
}
