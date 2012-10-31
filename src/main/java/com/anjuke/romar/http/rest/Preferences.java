package com.anjuke.romar.http.rest;

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
import com.anjuke.romar.http.rest.bean.PreferenceBean;
import com.sun.jersey.spi.resource.Singleton;

@Path("/preferences")
@Singleton
public class Preferences extends BaseResource{

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setPreference(PreferenceBean bean) {
        PreferenceRomarRequest request = new PreferenceRomarRequest(RequestPath.UPDATE);
        request.setUserId(bean.getUser());
        request.setItemId(bean.getItem());
        request.setValue(bean.getValue());
        RomarResponse response = _romarCore.execute(request);
        checkErrors(response);
        return Response.status(Status.ACCEPTED).build();
    }

    @DELETE
    @Path("/{user}/{item}")
    public Response removePreference(@PathParam("user") long user,
            @PathParam("item") long item) {
        PreferenceRomarRequest request = new PreferenceRomarRequest(RequestPath.REMOVE);
        request.setUserId(user);
        request.setItemId(item);
        RomarResponse response = _romarCore.execute(request);
        checkErrors(response);
        return Response.status(Status.ACCEPTED).build();
    }

    @GET
    @Path("/{user}/{item}")
    public Response estimatePreference(@PathParam("user") long user,
            @PathParam("item") long item) {
        PreferenceRomarRequest request = new PreferenceRomarRequest(RequestPath.ESTIMATE);
        request.setUserId(user);
        request.setItemId(item);
        RomarResponse response = _romarCore.execute(request);
        checkErrors(response);
        return Response.ok().entity(response).build();
    }

}
