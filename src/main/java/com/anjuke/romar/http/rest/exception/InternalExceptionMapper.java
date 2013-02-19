package com.anjuke.romar.http.rest.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.romar.core.impl.response.ErrorResponse;

@Provider
public class InternalExceptionMapper implements ExceptionMapper<InternalException> {
    private static final Logger LOG = LoggerFactory
            .getLogger(InternalExceptionMapper.class);

    @Override
    public Response toResponse(InternalException exception) {
        if (LOG.isDebugEnabled()) {
            LOG.warn(exception.getMessage(), exception);
        }
        ErrorResponse errorResponse=exception.getErrorResponse();
        return Response.status(errorResponse.getCode()).entity(errorResponse.getMessage())
        .type("text/plain").build();
    }

}
