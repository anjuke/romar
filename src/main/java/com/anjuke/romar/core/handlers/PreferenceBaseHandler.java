package com.anjuke.romar.core.handlers;

import com.anjuke.romar.core.RomarRequest;
import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.ErrorResponse;
import com.anjuke.romar.core.impl.PreferenceRomarRequest;
import com.anjuke.romar.mahout.MahoutService;

public abstract class PreferenceBaseHandler extends BaseHandler {

    public PreferenceBaseHandler(MahoutService service) {
        super(service);
    }

    @Override
    public RomarResponse process(RomarRequest request) throws Exception {
        if (request instanceof PreferenceRomarRequest) {
            return process((PreferenceRomarRequest) request);
        } else {
            return new ErrorResponse(400, "Invalid Request Format");
        }
    }

    public abstract RomarResponse process(PreferenceRomarRequest request)
            throws Exception;

}
