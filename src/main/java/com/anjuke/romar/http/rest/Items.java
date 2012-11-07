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

import java.util.Arrays;
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
import com.anjuke.romar.core.impl.request.MultiItemIdRequest;
import com.anjuke.romar.core.impl.request.PreferenceRomarRequest;
import com.anjuke.romar.core.impl.response.RecommendResultResponse;
import com.sun.jersey.spi.resource.Singleton;

@Path("/items")
@Singleton
public class Items extends BaseResource {
    @GET
    @Path("/similars")
    @Produces(MediaType.APPLICATION_JSON)
    public Response similar(@QueryParam("item") List<String> items,
            @QueryParam("limit") int limit) {
        MultiItemIdRequest request = new MultiItemIdRequest(RequestPath.ITEM_RECOMMEND);
        request.setItemId(getItemIds(items));
        request.setLimit(limit);
        RomarResponse response = _romarCore.execute(request);
        checkErrors(response);
        RecommendResultResponse recommendResponse = (RecommendResultResponse) response;
        List<?> result = wrapRecommendItem(recommendResponse);
        return Response.ok().entity(result).build();
    }

    @GET
    @Path("/{item}/similars")
    @Produces(MediaType.APPLICATION_JSON)
    public Response similar(@PathParam("item") String item, @QueryParam("limit") int limit) {
        return similar(Arrays.asList(item), limit);
    }

    @DELETE
    @Path("/{item}")
    public Response remove(@PathParam("item") String itemString) {
        PreferenceRomarRequest request = new PreferenceRomarRequest(
                RequestPath.REMOVE_ITEM);
        request.setItemId(getItem(itemString));
        RomarResponse response = _romarCore.execute(request);
        checkErrors(response);
        return Response.status(Status.ACCEPTED).build();
    }
}
