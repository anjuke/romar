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

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.anjuke.romar.core.RequestPath;
import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.request.PreferenceRomarRequest;
import com.anjuke.romar.core.impl.response.MultiValueResponse;
import com.anjuke.romar.core.impl.response.RecommendResultResponse;
import com.sun.jersey.spi.resource.Singleton;

@Path("/users")
@Singleton
public class Users extends BaseResource {

    @GET
    @Path("/{user}/recommendations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response recommend(@PathParam("user") String userString,
            @QueryParam("limit") int limit) {
        PreferenceRomarRequest request = new PreferenceRomarRequest(RequestPath.RECOMMEND);
        request.setUserId(getUser(userString));
        request.setLimit(limit);
        RomarResponse response = _romarCore.execute(request);
        checkErrors(response);
        RecommendResultResponse recommendResponse = (RecommendResultResponse) response;
        List<?> result = wrapRecommendItem(recommendResponse);
        return Response.ok().entity(result).build();
    }

    @DELETE
    @Path("/{user}")
    public Response remove(@PathParam("user") String userString) {
        PreferenceRomarRequest request = new PreferenceRomarRequest(
                RequestPath.REMOVE_USER);
        request.setUserId(getUser(userString));
        RomarResponse response = _romarCore.execute(request);
        checkErrors(response);
        return Response.status(Status.ACCEPTED).build();
    }

    @GET
    @Path("/{user}/similars")
    public Response similarUser(@PathParam("user") String userString,
            @QueryParam("limit") int limit) {
        PreferenceRomarRequest request = new PreferenceRomarRequest(
                RequestPath.SIMILAR_USER);
        request.setUserId(getUser(userString));
        request.setLimit(limit);
        RomarResponse response = _romarCore.execute(request);
        checkErrors(response);
        MultiValueResponse res = wrapMultiUserValues((MultiValueResponse) response);
        return Response.ok().entity(res).build();
    }
}
