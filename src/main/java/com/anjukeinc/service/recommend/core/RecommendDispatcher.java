package com.anjukeinc.service.recommend.core;

public interface RecommendDispatcher {

    public RecommendRequestHandler getHandler(RecommendRequest request);
}
