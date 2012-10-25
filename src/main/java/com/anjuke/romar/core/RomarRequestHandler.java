package com.anjuke.romar.core;

public interface RomarRequestHandler {
    RomarResponse process(RomarRequest request) throws Exception;
}
