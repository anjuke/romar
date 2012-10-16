package com.anjuke.romar.core.impl.response;

import java.util.Collections;
import java.util.List;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import com.anjuke.romar.core.RomarResponse;

public class RecommendResultResponse implements RomarResponse{
    private final List<RecommendedItem> list;

    public RecommendResultResponse(List<RecommendedItem> list) {
        super();
        this.list = Collections.unmodifiableList(list);
    }

    public List<RecommendedItem> getList() {
        return list;
    }



}
