package com.anjuke.romar.core.handlers;

import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.request.PreferenceRomarRequest;
import com.anjuke.romar.core.impl.response.SuccessReplyNoneResponse;
import com.anjuke.romar.mahout.MahoutService;

public class UpdateHandler extends PreferenceBaseHandler {

    public UpdateHandler(MahoutService service) {
        super(service);
        // TODO Auto-generated constructor stub
    }

    @Override
    public RomarResponse process(PreferenceRomarRequest request)
            throws Exception {
        _service.setPreference(request.getUserId(), request.getItemId(),
                request.getValue());
        return SuccessReplyNoneResponse.INSTANCE;
    }
}
