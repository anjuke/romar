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
package com.anjuke.romar.http.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.anjuke.romar.core.RequestPath;
import com.anjuke.romar.core.impl.request.NoneContentRequest;
import com.sun.jersey.spi.resource.Singleton;

@Path("/optimize")
@Singleton
public class Optimize extends BaseResource {

    @POST
    public Response optimize() {
        checkErrors(_romarCore.execute(new NoneContentRequest(RequestPath.OPTIMIZE)));
        return Response.status(Status.ACCEPTED).build();
    }

}
