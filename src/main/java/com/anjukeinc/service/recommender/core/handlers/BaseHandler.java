package com.anjukeinc.service.recommender.core.handlers;

import com.anjukeinc.service.recommender.core.RecommendRequestHandler;
import com.anjukeinc.service.recommender.mahout.MahoutService;

public abstract class BaseHandler implements RecommendRequestHandler{
    protected final MahoutService service;

    public BaseHandler(MahoutService service) {
        super();
        this.service = service;
    }


}
