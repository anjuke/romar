/**
 * Copyright 2012 Anjuke Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        ErrorResponse errorResponse = exception.getErrorResponse();
        return Response.status(errorResponse.getCode())
                .entity(errorResponse.getMessage()).type("text/plain").build();
    }

}
