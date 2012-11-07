package com.anjuke.romar.core.handlers;

import java.util.List;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import com.anjuke.romar.core.RomarRequest;
import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.request.MultiItemIdRequest;
import com.anjuke.romar.core.impl.response.RecommendResultResponse;
import com.anjuke.romar.mahout.MahoutService;

public class ItemRecommendHandler extends BaseHandler {

    public ItemRecommendHandler(MahoutService service) {
        super(service);
    }

    @Override
    public RomarResponse process(RomarRequest request) throws Exception {
        MultiItemIdRequest mr = (MultiItemIdRequest) request;
        int howMany = mr.getLimit();
        if (howMany <= 0) {
            howMany = PreferenceBaseHandler.DEFAULT_HOW_MANY;
        }
        List<RecommendedItem> list = _service.mostSimilarItems(mr.getItemId(), howMany);
        return new RecommendResultResponse(list);
    }

}
