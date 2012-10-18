package com.anjuke.romar.core;

import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.romar.core.impl.response.ErrorResponse;
import com.anjuke.romar.mahout.MahoutService;

public class RomarCore {

    private static final Logger log = LoggerFactory.getLogger(RomarCore.class);

    private RomarDispatcher dispatcher;

    private MahoutService service;

    private static final ErrorResponse PATH_404_ERROR = new ErrorResponse(
            404, "path unavailable");

    public RomarResponse execute(RomarRequest request) {
        RomarRequestHandler handler = dispatcher.getHandler(request);
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

    public void setDispatcher(RomarDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public MahoutService getService() {
        return service;
    }

    public void setService(MahoutService service) {
        this.service = service;
    }

}
