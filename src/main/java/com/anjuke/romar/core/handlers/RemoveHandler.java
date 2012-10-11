package com.anjuke.romar.core.handlers;

import com.anjuke.romar.core.RomarRequest;
import com.anjuke.romar.core.RomarRequestHandler;
import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.SuccessReplyNoneResponse;
import com.anjuke.romar.mahout.MahoutService;

public class RemoveHandler extends BaseHandler implements RomarRequestHandler {

    public RemoveHandler(MahoutService service) {
        super(service);
    }

    @Override
    public RomarResponse process(RomarRequest request) throws Exception {
        service.removePreference(request.getUserId(), request.getItemId());
        return SuccessReplyNoneResponse.instance;
    }

}
