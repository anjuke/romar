package com.anjuke.romar.http.rest;

import com.anjuke.romar.core.CoreContainer;
import com.anjuke.romar.core.RomarCore;
import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.response.ErrorResponse;
import com.anjuke.romar.http.rest.exception.InternalException;

abstract class BaseResource {

    protected final RomarCore _romarCore = CoreContainer.getCore();

    void checkErrors(RomarResponse response) {
        if (response instanceof ErrorResponse) {
            throw new InternalException((ErrorResponse) response);
        }
    }
}
