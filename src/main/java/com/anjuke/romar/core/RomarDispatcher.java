package com.anjuke.romar.core;

public interface RomarDispatcher {

    RomarRequestHandler getHandler(RomarRequest request);
}
