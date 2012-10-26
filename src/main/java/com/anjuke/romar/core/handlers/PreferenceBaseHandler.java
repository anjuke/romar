package com.anjuke.romar.core.handlers;

import com.anjuke.romar.core.RomarRequest;
import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.request.PreferenceRomarRequest;
import com.anjuke.romar.core.impl.response.ErrorResponse;
import com.anjuke.romar.mahout.MahoutService;

public abstract class PreferenceBaseHandler extends BaseHandler {

    static final int DEFAULT_HOW_MANY = 5;

    public PreferenceBaseHandler(MahoutService service) {
        super(service);
    }

    @Override
    public RomarResponse process(RomarRequest request) throws Exception {
        if (request instanceof PreferenceRomarRequest) {
            return process((PreferenceRomarRequest) request);
        } else {
            return new ErrorResponse(ErrorResponse.RESOURCE_NOT_FOUND,
                    "Invalid Request Format");
        }
    }

    public abstract RomarResponse process(PreferenceRomarRequest request)
            throws Exception;

}
