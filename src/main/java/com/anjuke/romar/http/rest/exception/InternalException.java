package com.anjuke.romar.http.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.anjuke.romar.core.impl.response.ErrorResponse;

public class InternalException extends WebApplicationException {

    private static final long serialVersionUID = -9174551498572603831L;

    public InternalException(ErrorResponse response) {
        super(Response.status(response.getCode()).entity(response.getMessage())
                .type("text/plain").build());
    }

}
