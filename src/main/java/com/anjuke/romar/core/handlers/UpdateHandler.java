package com.anjuke.romar.core.handlers;

import com.anjuke.romar.core.RomarRequest;
import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.SuccessReplyNoneResponse;
import com.anjuke.romar.mahout.MahoutService;

public class UpdateHandler extends BaseHandler{

    public UpdateHandler(MahoutService service) {
        super(service);
        // TODO Auto-generated constructor stub
    }

    @Override
    public RomarResponse process(RomarRequest request)
            throws Exception {
        service.setPreference(request.getUserId(), request.getItemId(), request.getPreference());
        return SuccessReplyNoneResponse.instance;
    }
}
