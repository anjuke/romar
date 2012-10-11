package com.anjukeinc.service.recommender.core.handlers;

import com.anjukeinc.service.recommender.core.RecommendRequest;
import com.anjukeinc.service.recommender.core.RecommendRequestHandler;
import com.anjukeinc.service.recommender.core.RecommendResponse;
import com.anjukeinc.service.recommender.core.impl.SuccessReplyNoneResponse;
import com.anjukeinc.service.recommender.mahout.MahoutService;

public class RemoveHandler extends BaseHandler implements RecommendRequestHandler {

    public RemoveHandler(MahoutService service) {
        super(service);
    }

    @Override
    public RecommendResponse process(RecommendRequest request) throws Exception {
        service.removePreference(request.getUserId(), request.getItemId());
        return SuccessReplyNoneResponse.instance;
    }

}
