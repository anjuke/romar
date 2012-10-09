package com.anjukeinc.service.recommend.core.impl;

import com.anjukeinc.service.recommend.core.RecommendResponse;

public class ErrorRecommendResponse implements RecommendResponse {

    private final int code;
    private final String message;

    public ErrorRecommendResponse(int code, String message) {
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
