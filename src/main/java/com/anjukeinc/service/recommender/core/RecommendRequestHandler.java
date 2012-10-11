package com.anjukeinc.service.recommender.core;

public interface RecommendRequestHandler {
    public RecommendResponse process(RecommendRequest request) throws Exception;
}
