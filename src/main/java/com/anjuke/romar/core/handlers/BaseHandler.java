package com.anjuke.romar.core.handlers;

import com.anjuke.romar.core.RomarRequestHandler;
import com.anjuke.romar.mahout.MahoutService;

public abstract class BaseHandler implements RomarRequestHandler{
    protected final MahoutService service;

    public BaseHandler(MahoutService service) {
        super();
        this.service = service;
    }


}
