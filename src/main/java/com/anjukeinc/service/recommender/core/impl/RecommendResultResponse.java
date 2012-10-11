package com.anjukeinc.service.recommender.core.impl;

import java.util.Collections;
import java.util.List;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import com.anjukeinc.service.recommender.core.RecommendResponse;

public class RecommendResultResponse implements RecommendResponse{
    private final List<RecommendedItem> list;

    public RecommendResultResponse(List<RecommendedItem> list) {
        super();
        this.list = Collections.unmodifiableList(list);
    }

    public List<RecommendedItem> getList() {
        return list;
    }



}
