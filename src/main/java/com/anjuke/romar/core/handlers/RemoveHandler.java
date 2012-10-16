package com.anjuke.romar.core.handlers;

import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.PreferenceRomarRequest;
import com.anjuke.romar.core.impl.SuccessReplyNoneResponse;
import com.anjuke.romar.mahout.MahoutService;

public class RemoveHandler extends PreferenceBaseHandler {

    public RemoveHandler(MahoutService service) {
        super(service);
    }

    @Override
    public RomarResponse process(PreferenceRomarRequest request)
            throws Exception {
        service.removePreference(request.getUserId(), request.getItemId());
        return SuccessReplyNoneResponse.instance;
    }

}
