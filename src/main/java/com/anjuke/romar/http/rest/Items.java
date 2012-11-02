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

import org.apache.commons.lang.ArrayUtils;

import com.anjuke.romar.core.RequestPath;
import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.request.MultiItemIdRequest;
import com.anjuke.romar.core.impl.request.PreferenceRomarRequest;
import com.anjuke.romar.core.impl.response.RecommendResultResponse;
import com.anjuke.romar.http.rest.bean.RecommendBean;
import com.sun.jersey.spi.resource.Singleton;

@Path("/items")
@Singleton
public class Items extends BaseResource {
    @GET
    @Path("/similars")
    @Produces(MediaType.APPLICATION_JSON)
    public Response similar(@QueryParam("item") List<Long> items) {
        MultiItemIdRequest request = new MultiItemIdRequest(RequestPath.ITEM_RECOMMEND);
        request.setItemId(ArrayUtils.toPrimitive(items.toArray(new Long[items.size()])));
        RomarResponse response = _romarCore.execute(request);
        checkErrors(response);
        RecommendResultResponse recommendResponse = (RecommendResultResponse) response;
        List<RecommendBean> result = RestResponseUtil
                .wrapRecommendItem(recommendResponse);
        return Response.ok().entity(result).build();
    }

    @DELETE
    @Path("/{item}")
    public Response remove(@PathParam("item") long item) {
        PreferenceRomarRequest request = new PreferenceRomarRequest(RequestPath.REMOVE_ITEM);
        request.setItemId(item);
        RomarResponse response = _romarCore.execute(request);
        checkErrors(response);
        return Response.status(Status.ACCEPTED).build();
    }
}
