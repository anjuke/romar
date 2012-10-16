package com.anjuke.romar.core.impl.response;

import com.anjuke.romar.core.RomarResponse;

public class ErrorResponse implements RomarResponse {

    private final int code;
    private final String message;

    public ErrorResponse(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
