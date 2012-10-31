package com.anjuke.romar.core.handlers;

import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.request.PreferenceRomarRequest;
import com.anjuke.romar.core.impl.response.ValueResponse;
import com.anjuke.romar.mahout.MahoutService;

public class EstimateHandler extends PreferenceBaseHandler {

    public EstimateHandler(MahoutService service) {
        super(service);
    }

    @Override
    public RomarResponse process(PreferenceRomarRequest request) throws Exception {
        float value = _service.estimatePreference(request.getUserId(),
                request.getItemId());
        return new ValueResponse(value);
    }

}
