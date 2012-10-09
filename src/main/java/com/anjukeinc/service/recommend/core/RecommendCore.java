package com.anjukeinc.service.recommend.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjukeinc.service.recommend.core.impl.ErrorRecommendResponse;
import com.anjukeinc.service.recommend.mahout.MahoutService;

public class RecommendCore {

    public static Logger log = LoggerFactory.getLogger(RecommendCore.class);

    private RecommendDispatcher dispatcher;

    private MahoutService service;

    private final ErrorRecommendResponse PATH_404_ERROR = new ErrorRecommendResponse(
            404, "path unavailable");

    public RecommendResponse execute(RecommendRequest request) {
        RecommendRequestHandler handler = dispatcher.getHandler(request);
        if (handler == null) {
            return PATH_404_ERROR;
        }

        try {
            return handler.process(request);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return new ErrorRecommendResponse(500, "internal error: "
                    + e.getMessage());
        }
    }

    public void setDispatcher(RecommendDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public MahoutService getService() {
        return service;
    }

    public void setService(MahoutService service) {
        this.service = service;
    }

}
