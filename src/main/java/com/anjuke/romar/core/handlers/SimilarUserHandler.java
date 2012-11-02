package com.anjuke.romar.core.handlers;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.request.PreferenceRomarRequest;
import com.anjuke.romar.core.impl.response.MultiValueResponse;
import com.anjuke.romar.mahout.MahoutService;

public class SimilarUserHandler extends PreferenceBaseHandler{

    public SimilarUserHandler(MahoutService service) {
        super(service);
    }

    @Override
    public RomarResponse process(PreferenceRomarRequest request) throws Exception {
        long[] ids=_service.mostSimilarUserIDs(request.getUserId(), DEFAULT_HOW_MANY);
        return new MultiValueResponse(Arrays.asList(ArrayUtils.toObject(ids)));
    }

}
