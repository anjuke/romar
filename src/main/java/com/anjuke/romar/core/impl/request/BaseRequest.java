package com.anjuke.romar.core.impl.request;

import com.anjuke.romar.core.RomarRequest;

public abstract class BaseRequest implements RomarRequest {
    private final String path;

    public BaseRequest(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }
}
