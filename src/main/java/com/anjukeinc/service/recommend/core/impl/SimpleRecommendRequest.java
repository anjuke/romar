package com.anjukeinc.service.recommend.core.impl;

import com.anjukeinc.service.recommend.core.RecommendRequest;

public class SimpleRecommendRequest implements RecommendRequest {
    private final String path;
    private long userId;
    private long itemId;
    private float preference;

    public SimpleRecommendRequest(String path) {
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
