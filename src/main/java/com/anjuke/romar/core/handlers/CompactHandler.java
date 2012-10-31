package com.anjuke.romar.core.handlers;

import com.anjuke.romar.core.RomarRequest;
import com.anjuke.romar.core.RomarRequestHandler;
import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.response.SuccessReplyNoneResponse;
import com.anjuke.romar.mahout.MahoutService;
import com.anjuke.romar.mahout.PreferenceDataModel;

public class CompactHandler  extends BaseHandler implements RomarRequestHandler {

    public CompactHandler(MahoutService service) {
        super(service);
    }

    @Override
    public RomarResponse process(RomarRequest request) throws Exception {
        ((PreferenceDataModel) _service.getDataModel()).compact();
        return SuccessReplyNoneResponse.INSTANCE;
    }

}
