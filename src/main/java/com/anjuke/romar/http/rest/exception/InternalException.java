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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.anjuke.romar.core.impl.response.ErrorResponse;

public class InternalException extends WebApplicationException {

    private static final long serialVersionUID = -9174551498572603831L;
    private final ErrorResponse errorResponse;

    public InternalException(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

}
