package com.anjukeinc.service.recommender.core.handlers;

import com.anjukeinc.service.recommender.core.RecommendRequest;
import com.anjukeinc.service.recommender.core.RecommendRequestHandler;
import com.anjukeinc.service.recommender.core.RecommendResponse;
import com.anjukeinc.service.recommender.core.impl.SuccessReplyNoneResponse;
import com.anjukeinc.service.recommender.mahout.MahoutService;

public class ReloadHandler extends BaseHandler implements
        RecommendRequestHandler {

    public ReloadHandler(MahoutService service) {
        super(service);
    }

    @Override
    public RecommendResponse process(RecommendRequest request) throws Exception {
        service.refresh(null);
        return SuccessReplyNoneResponse.instance;
    }

}
