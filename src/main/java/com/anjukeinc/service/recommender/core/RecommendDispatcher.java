package com.anjukeinc.service.recommender.core;

public interface RecommendDispatcher {

    public RecommendRequestHandler getHandler(RecommendRequest request);
}
