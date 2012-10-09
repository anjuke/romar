package com.anjukeinc.service.recommend.core;

public interface RecommendRequest {
    public String getPath();
    public long getUserId();
    public long getItemId();
    public float getPreference();
}
