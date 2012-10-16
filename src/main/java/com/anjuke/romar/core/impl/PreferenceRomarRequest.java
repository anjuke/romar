package com.anjuke.romar.core.impl;

import com.anjuke.romar.core.RomarRequest;

public class PreferenceRomarRequest implements RomarRequest {
    private final String path;
    private long userId;
    private long itemId;
    private float value;

    public PreferenceRomarRequest(String path) {
        super();
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
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
