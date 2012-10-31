package com.anjuke.romar.core.handlers;

import com.anjuke.romar.core.RomarRequest;
import com.anjuke.romar.core.RomarRequestHandler;
import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.response.SuccessReplyNoneResponse;
import com.anjuke.romar.mahout.MahoutService;

public class CommitHandler extends BaseHandler implements RomarRequestHandler {

    public CommitHandler(MahoutService service) {
        super(service);
    }

    @Override
    public RomarResponse process(RomarRequest request) throws Exception {
        _service.refresh(null);
        return SuccessReplyNoneResponse.INSTANCE;
    }

}
