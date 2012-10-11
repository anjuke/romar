package com.anjuke.romar.core;

public interface RomarDispatcher {

    public RomarRequestHandler getHandler(RomarRequest request);
}
