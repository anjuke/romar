package com.anjuke.romar.core;

public interface RomarRequestHandler {
    public RomarResponse process(RomarRequest request) throws Exception;
}
