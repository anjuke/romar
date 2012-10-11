package com.anjukeinc.service.recommender.core;

public interface RecommendRequest {
    public String getPath();
    public long getUserId();
    public long getItemId();
    public float getPreference();
}
