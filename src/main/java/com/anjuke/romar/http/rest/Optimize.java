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
