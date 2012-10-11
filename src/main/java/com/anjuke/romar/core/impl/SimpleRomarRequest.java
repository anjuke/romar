package com.anjuke.romar.core.impl;

import com.anjuke.romar.core.RomarRequest;

public class SimpleRomarRequest implements RomarRequest {
    private final String path;
    private long userId;
    private long itemId;
    private float preference;

    public SimpleRomarRequest(String path) {
        super();
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public long getItemId() {
        return itemId;
    }

    @Override
    public float getPreference() {
        return preference;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public void setPreference(float preference) {
        this.preference = preference;
    }


}
