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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.anjuke.romar.core.RequestPath;
import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.request.PreferenceRomarRequest;
import com.anjuke.romar.http.rest.bean.ValueBean;
import com.sun.jersey.spi.resource.Singleton;

@Path("/preferences")
@Singleton
public class Preferences extends BaseResource {
    private final int _paramSize = 3;

    @PUT
    @Path("/{user}/{item}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setPreference(@PathParam("user") String userString,
            @PathParam("item") String itemString, ValueBean bean) {
        long[] tmp = getUserAndItem(userString, itemString);
        long user = tmp[0];
        long item = tmp[1];

        PreferenceRomarRequest request = new PreferenceRomarRequest(RequestPath.UPDATE);
        request.setUserId(user);
        request.setItemId(item);
        request.setValue(bean.getValue());
        RomarResponse response = _romarCore.execute(request);
        checkErrors(response);
        return Response.status(Status.ACCEPTED).build();
    }

    @DELETE
    @Path("/{user}/{item}")
    public Response removePreference(@PathParam("user") String userString,
            @PathParam("item") String itemString) {
        long[] tmp = getUserAndItem(userString, itemString);
        long user = tmp[0];
        long item = tmp[1];
        PreferenceRomarRequest request = new PreferenceRomarRequest(RequestPath.REMOVE);
        request.setUserId(user);
        request.setItemId(item);
        RomarResponse response = _romarCore.execute(request);
        checkErrors(response);
        return Response.status(Status.ACCEPTED).build();
    }

    @GET
    @Path("/{user}/{item}")
    public Response estimatePreference(@PathParam("user") String userString,
            @PathParam("item") String itemString) {
        long[] tmp = getUserAndItem(userString, itemString);
        long user = tmp[0];
        long item = tmp[1];
        PreferenceRomarRequest request = new PreferenceRomarRequest(RequestPath.ESTIMATE);
        request.setUserId(user);
        request.setItemId(item);
        RomarResponse response = _romarCore.execute(request);
        checkErrors(response);
        return Response.ok().entity(response).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setPreference(List<List<String>> values) {
        if (values.size() == 0) {
            return Response.status(Status.ACCEPTED).build();
        }
        for (List<String> list : values) {
            if (list.size() != _paramSize) {
                return Response.status(Status.BAD_REQUEST).build();
            }
        }

        for (List<String> pref : values) {
            PreferenceRomarRequest request = new PreferenceRomarRequest(
                    RequestPath.UPDATE);
            String userString = pref.get(0);
            String itemString = pref.get(1);
            long[] tmp = getUserAndItem(userString, itemString);
            long user = tmp[0];
            long item = tmp[1];
            request.setUserId(user);
            request.setItemId(item);
            request.setValue(Float.parseFloat(pref.get(2)));
            RomarResponse response = _romarCore.execute(request);
            checkErrors(response);
        }
        return Response.status(Status.ACCEPTED).build();
    }
}
