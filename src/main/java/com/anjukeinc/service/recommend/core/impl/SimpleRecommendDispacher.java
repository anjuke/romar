package com.anjukeinc.service.recommend.core.impl;

import java.util.HashMap;
import java.util.Map;

import com.anjukeinc.service.recommend.core.RecommendDispatcher;
import com.anjukeinc.service.recommend.core.RecommendRequest;
import com.anjukeinc.service.recommend.core.RecommendRequestHandler;

public class SimpleRecommendDispacher implements RecommendDispatcher{

    private Map<String,RecommendRequestHandler> handlers=new HashMap<String, RecommendRequestHandler>();

    private boolean prepared=false;

    public RecommendRequestHandler getHandler(RecommendRequest request) {
        checkPrepared();
        return handlers.get(request.getPath());
    }

    public void registerHandler(String path,RecommendRequestHandler handler){
        checkNotPrepared();
        handlers.put(path, handler);
    }

    public void prepare(){
        prepared=true;
    }

    private void checkPrepared(){
        if(!prepared){
            throw new IllegalStateException("not prepared");
        }
    }

    private void checkNotPrepared(){
        if(prepared){
            throw new IllegalStateException("already prepared");
        }
    }

}
