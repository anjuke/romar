package com.anjuke.romar.core.impl.response;

import com.anjuke.romar.core.RomarResponse;

public class ErrorResponse implements RomarResponse {

    private final int _code;
    private final String _message;

    public ErrorResponse(int code, String message) {
        super();
        _code = code;
        _message = message;
    }

    public int getCode() {
        return _code;
    }

    public String getMessage() {
        return _message;
    }

}
