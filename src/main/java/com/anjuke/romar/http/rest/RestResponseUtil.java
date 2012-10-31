package com.anjuke.romar.http.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import com.anjuke.romar.core.impl.response.RecommendResultResponse;
import com.anjuke.romar.http.rest.bean.RecommendBean;

final class RestResponseUtil {
    private RestResponseUtil() {

    }

    static List<RecommendBean> wrapRecommendItem(RecommendResultResponse recommendResponse) {
        List<RecommendedItem> list = recommendResponse.getList();
        List<RecommendBean> result = new ArrayList<RecommendBean>();
        for (RecommendedItem item : list) {
            RecommendBean bean = new RecommendBean();
            bean.setItem(item.getItemID());
            bean.setValue(item.getValue());
            result.add(bean);
        }
        return result;
    }

}
