package com.anjuke.romar.core.handlers;

import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.request.PreferenceRomarRequest;
import com.anjuke.romar.core.impl.response.SuccessReplyNoneResponse;
import com.anjuke.romar.mahout.MahoutService;

public class RemoveUserHandler extends PreferenceBaseHandler {

    public RemoveUserHandler(MahoutService service) {
        super(service);
    }

    @Override
    public RomarResponse process(PreferenceRomarRequest request) throws Exception {
        _service.removeUser(request.getUserId());
        return SuccessReplyNoneResponse.INSTANCE;
    }

}
