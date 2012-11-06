package com.anjuke.romar.http.rest;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
    public Response recommend(@PathParam("user") String userString) {
        PreferenceRomarRequest request = new PreferenceRomarRequest(RequestPath.RECOMMEND);
        request.setUserId(getUser(userString));
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
    public Response similarUser(@PathParam("user") String userString) {
        PreferenceRomarRequest request = new PreferenceRomarRequest(
                RequestPath.SIMILAR_USER);
        request.setUserId(getUser(userString));
        RomarResponse response = _romarCore.execute(request);
        checkErrors(response);
        MultiValueResponse res = wrapMultiUserValues((MultiValueResponse) response);
        return Response.ok().entity(res).build();
    }
}
