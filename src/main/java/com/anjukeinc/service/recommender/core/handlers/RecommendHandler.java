package com.anjukeinc.service.recommender.core.handlers;

import java.util.List;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import com.anjukeinc.service.recommender.core.RecommendRequest;
import com.anjukeinc.service.recommender.core.RecommendResponse;
import com.anjukeinc.service.recommender.core.impl.RecommendResultResponse;
import com.anjukeinc.service.recommender.mahout.MahoutService;

public class RecommendHandler extends BaseHandler{

    public RecommendHandler(MahoutService service) {
        super(service);
    }

    @Override
    public RecommendResponse process(RecommendRequest request)
            throws Exception {
        List<RecommendedItem> list=service.recommend(request.getUserId(), 5);
        return new RecommendResultResponse(list);
    }

}
