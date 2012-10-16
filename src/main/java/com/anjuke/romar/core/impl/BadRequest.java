package com.anjuke.romar.core.impl;

import com.anjuke.romar.core.RomarRequest;

public class BadRequest implements RomarRequest{
    private final String path;

    public BadRequest(String path) {
        super();
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

}
