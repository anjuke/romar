package com.anjuke.romar.core.impl;

import java.util.HashMap;
import java.util.Map;

import com.anjuke.romar.core.RequestPath;
import com.anjuke.romar.core.RomarDispatcher;
import com.anjuke.romar.core.RomarRequest;
import com.anjuke.romar.core.RomarRequestHandler;

public class SimpleRomarDispatcher implements RomarDispatcher {

    private Map<RequestPath, RomarRequestHandler> handlers = new HashMap<RequestPath, RomarRequestHandler>();

    private boolean prepared = false;

    public RomarRequestHandler getHandler(RomarRequest request) {
        checkPrepared();
        return handlers.get(request.getPath());
    }

    public void registerHandler(RequestPath path, RomarRequestHandler handler) {
        checkNotPrepared();
        handlers.put(path, handler);
    }

    public void prepare() {
        prepared = true;
    }

    private void checkPrepared() {
        if (!prepared) {
            throw new IllegalStateException("not prepared");
        }
    }

    private void checkNotPrepared() {
        if (prepared) {
            throw new IllegalStateException("already prepared");
        }
    }

}
