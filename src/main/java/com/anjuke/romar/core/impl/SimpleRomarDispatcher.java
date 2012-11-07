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
package com.anjuke.romar.core.impl;

import java.util.HashMap;
import java.util.Map;

import com.anjuke.romar.core.RequestPath;
import com.anjuke.romar.core.RomarDispatcher;
import com.anjuke.romar.core.RomarRequest;
import com.anjuke.romar.core.RomarRequestHandler;

public class SimpleRomarDispatcher implements RomarDispatcher {

    private Map<RequestPath, RomarRequestHandler> _handlers = new HashMap<RequestPath, RomarRequestHandler>();

    private boolean _prepared = false;

    public RomarRequestHandler getHandler(RomarRequest request) {
        checkPrepared();
        return _handlers.get(request.getPath());
    }

    public void registerHandler(RequestPath path, RomarRequestHandler handler) {
        checkNotPrepared();
        _handlers.put(path, handler);
    }

    public void prepare() {
        _prepared = true;
    }

    private void checkPrepared() {
        if (!_prepared) {
            throw new IllegalStateException("not prepared");
        }
    }

    private void checkNotPrepared() {
        if (_prepared) {
            throw new IllegalStateException("already prepared");
        }
    }

}
