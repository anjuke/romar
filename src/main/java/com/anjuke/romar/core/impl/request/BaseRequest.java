package com.anjuke.romar.core.impl.request;

import com.anjuke.romar.core.RequestPath;
import com.anjuke.romar.core.RomarRequest;

public abstract class BaseRequest implements RomarRequest {
    private final RequestPath _path;

    public BaseRequest(RequestPath path) {
        this._path = path;
    }

    @Override
    public RequestPath getPath() {
        return _path;
    }
}
