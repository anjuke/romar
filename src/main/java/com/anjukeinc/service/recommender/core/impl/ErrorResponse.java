package com.anjukeinc.service.recommender.core.impl;

import com.anjukeinc.service.recommender.core.RecommendResponse;

public class ErrorResponse implements RecommendResponse {

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
