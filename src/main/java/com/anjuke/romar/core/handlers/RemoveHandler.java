package com.anjuke.romar.core.handlers;

import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.request.PreferenceRomarRequest;
import com.anjuke.romar.core.impl.response.SuccessReplyNoneResponse;
import com.anjuke.romar.mahout.MahoutService;

public class RemoveHandler extends PreferenceBaseHandler {

    public RemoveHandler(MahoutService service) {
        super(service);
    }

    @Override
    public RomarResponse process(PreferenceRomarRequest request)
            throws Exception {
        _service.removePreference(request.getUserId(), request.getItemId());
        return SuccessReplyNoneResponse.INSTANCE;
    }

}
