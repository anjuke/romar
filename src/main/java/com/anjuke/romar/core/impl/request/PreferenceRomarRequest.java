package com.anjuke.romar.core.impl.request;

import com.anjuke.romar.core.RequestPath;
import com.anjuke.romar.core.RomarRequest;

public class PreferenceRomarRequest  extends BaseRequest implements RomarRequest {
    private long userId;
    private long itemId;
    private float value;

    public PreferenceRomarRequest(RequestPath path) {
        super(path);
    }

    public long getUserId() {
        return userId;
    }

    public long getItemId() {
        return itemId;
    }

    public float getValue() {
        return value;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public void setValue(float preference) {
        this.value = preference;
    }


}
