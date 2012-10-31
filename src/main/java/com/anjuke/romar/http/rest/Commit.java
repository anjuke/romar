package com.anjuke.romar.http.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.anjuke.romar.core.CoreContainer;
import com.anjuke.romar.core.RequestPath;
import com.anjuke.romar.core.RomarCore;
import com.anjuke.romar.core.impl.request.NoneContentRequest;
import com.sun.jersey.spi.resource.Singleton;


@Path("/commit")
@Singleton
public class Commit {
     private final RomarCore _romarCore = CoreContainer.getCore();


     @POST
     public Response commit(){
         _romarCore.execute(new NoneContentRequest(RequestPath.COMMIT));
         return Response.status(Status.ACCEPTED).build();
     }
}
