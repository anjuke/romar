package com.anjukeinc.service.recommender.core;

import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjukeinc.service.recommender.core.impl.ErrorResponse;
import com.anjukeinc.service.recommender.mahout.MahoutService;

public class RecommenderCore {

    public static Logger log = LoggerFactory.getLogger(RecommenderCore.class);

    private RecommendDispatcher dispatcher;

    private MahoutService service;

    private static final ErrorResponse PATH_404_ERROR = new ErrorResponse(
            404, "path unavailable");

    public RecommendResponse execute(RecommendRequest request) {
        RecommendRequestHandler handler = dispatcher.getHandler(request);
        if (handler == null) {
            return PATH_404_ERROR;
        }

        try {
            return handler.process(request);
        }catch(NoSuchUserException e){
            log.error(e.getMessage(), e);
            return new ErrorResponse(500, "internal error: no such user "
                    + e.getMessage());
        }catch(NoSuchItemException e){
             log.error(e.getMessage(), e);
             return new ErrorResponse(500, "internal error: no such item "
                     + e.getMessage());
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return new ErrorResponse(500, "internal error: "
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
