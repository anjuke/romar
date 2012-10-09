package com.anjukeinc.service.recommend.core;

public interface RecommendRequestHandler {
    public RecommendResponse process(RecommendRequest request) throws Exception;
}
