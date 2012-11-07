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
package com.anjuke.romar.core.impl.response;

import com.anjuke.romar.core.RomarResponse;

public class ErrorResponse implements RomarResponse {
    public final static int INTERNAL_ERROR = 500;
    public final static int RESOURCE_NOT_FOUND = 404;
    private final int _code;
    private final String _message;

    public ErrorResponse(int code, String message) {
        super();
        _code = code;
        _message = message;
    }

    public int getCode() {
        return _code;
    }

    public String getMessage() {
        return _message;
    }

}
