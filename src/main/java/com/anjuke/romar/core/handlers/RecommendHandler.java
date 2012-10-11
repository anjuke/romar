package com.anjuke.romar.core.handlers;

import java.util.List;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import com.anjuke.romar.core.RomarRequest;
import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.RecommendResultResponse;
import com.anjuke.romar.mahout.MahoutService;

public class RecommendHandler extends BaseHandler{

    public RecommendHandler(MahoutService service) {
        super(service);
    }

    @Override
    public RomarResponse process(RomarRequest request)
            throws Exception {
        List<RecommendedItem> list=service.recommend(request.getUserId(), 5);
        return new RecommendResultResponse(list);
    }

}
