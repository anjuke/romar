package com.anjuke.romar.http.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.anjuke.romar.core.RequestPath;
import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.request.PreferenceRomarRequest;
import com.anjuke.romar.core.impl.response.RecommendResultResponse;
import com.anjuke.romar.http.rest.bean.RecommendBean;
import com.sun.jersey.spi.resource.Singleton;

@Path("/users")
@Singleton
public class Users extends BaseResource {

    @GET
    @Path("/{user}/recommendations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response recommend(@PathParam("user") long user) {
        PreferenceRomarRequest request = new PreferenceRomarRequest(RequestPath.RECOMMEND);
        request.setUserId(user);
        RomarResponse response = _romarCore.execute(request);
        checkErrors(response);
        RecommendResultResponse recommendResponse = (RecommendResultResponse) response;
        List<RecommendBean> result = RestResponseUtil
                .wrapRecommendItem(recommendResponse);
        return Response.ok().entity(result).build();
    }

}
