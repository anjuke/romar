package com.anjukeinc.service.recommender.core.handlers;

import com.anjukeinc.service.recommender.core.RecommendRequest;
import com.anjukeinc.service.recommender.core.RecommendResponse;
import com.anjukeinc.service.recommender.core.impl.SuccessReplyNoneResponse;
import com.anjukeinc.service.recommender.mahout.MahoutService;

public class UpdateHandler extends BaseHandler{

    public UpdateHandler(MahoutService service) {
        super(service);
        // TODO Auto-generated constructor stub
    }

    @Override
    public RecommendResponse process(RecommendRequest request)
            throws Exception {
        service.setPreference(request.getUserId(), request.getItemId(), request.getPreference());
        return SuccessReplyNoneResponse.instance;
    }
}
