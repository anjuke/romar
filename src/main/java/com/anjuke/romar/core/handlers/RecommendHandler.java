package com.anjuke.romar.core.handlers;

import java.util.List;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.request.PreferenceRomarRequest;
import com.anjuke.romar.core.impl.response.RecommendResultResponse;
import com.anjuke.romar.mahout.MahoutService;

public class RecommendHandler extends PreferenceBaseHandler {

    public RecommendHandler(MahoutService service) {
        super(service);
    }

    @Override
    public RomarResponse process(PreferenceRomarRequest request) throws Exception {
        int howMany = request.getLimit();
        if (howMany <= 0) {
            howMany = DEFAULT_HOW_MANY;
        }
        List<RecommendedItem> list = _service.recommend(request.getUserId(), howMany);
        return new RecommendResultResponse(list);
    }

}
