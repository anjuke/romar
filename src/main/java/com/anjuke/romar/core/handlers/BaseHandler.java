package com.anjuke.romar.core.handlers;

import com.anjuke.romar.core.RomarRequestHandler;
import com.anjuke.romar.mahout.MahoutService;

public abstract class BaseHandler implements RomarRequestHandler{
    protected final MahoutService _service;

    public BaseHandler(MahoutService theService) {
        super();
        this._service = theService;
    }
}
