package com.anjukeinc.service.recommender.http.jetty;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.anjukeinc.service.recommender.core.RecommendRequest;
import com.anjukeinc.service.recommender.core.RecommendResponse;
import com.anjukeinc.service.recommender.core.RecommenderCore;
import com.anjukeinc.service.recommender.core.impl.ErrorResponse;
import com.anjukeinc.service.recommender.core.impl.RecommendResultResponse;
import com.anjukeinc.service.recommender.core.impl.SimpleRecommenderRequest;
import com.anjukeinc.service.recommender.core.impl.SuccessReplyNoneResponse;

public class JettyRecommenderHandler extends AbstractHandler {
    private final RecommenderCore core;

    public JettyRecommenderHandler(RecommenderCore core) {
        this.core = core;
    }

    @Override
    public void handle(String target, Request baseRequest,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String path=request.getPathInfo();
        System.out.println(path);

        RecommendRequest recomRequest;
        try {
            recomRequest = getRequest(path, request);
        } catch (NumberFormatException nfe) {
            response.setStatus(404);
            return;
        }

        RecommendResponse recomResponse = core.execute(recomRequest);

        if (recomResponse instanceof ErrorResponse) {
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setStatus(((ErrorResponse) recomResponse).getCode());
            response.getWriter().println(
                    ((ErrorResponse) recomResponse).getMessage());
            response.getWriter().flush();

        } else if (recomResponse instanceof SuccessReplyNoneResponse) {
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setStatus(200);
            response.getWriter().println("success");
            response.getWriter().flush();
        } else if (recomResponse instanceof RecommendResultResponse) {
            response.setStatus(200);
            List<RecommendedItem> items = ((RecommendResultResponse) recomResponse)
                    .getList();
            String format = request.getParameter("format");
            if (format == null) {
                format = "text";
            }

            if ("text".equals(format)) {
                writePlainText(response, items);
            } else if ("xml".equals(format)) {
                writeXML(response, items);
            } else if ("json".equals(format)) {
                writeJSON(response, items);
            } else {
                throw new ServletException("Bad format parameter: " + format);
            }
            response.getWriter().flush();
        }

    }

    private RecommendRequest getRequest(String path, HttpServletRequest request)
            throws NumberFormatException {
        SimpleRecommenderRequest srr = new SimpleRecommenderRequest(path);
        String rawUserId=request.getParameter("userId");

        long userId = rawUserId==null?0:Long.parseLong(rawUserId);
        String rawItemId=request.getParameter("itemId");
        long itemId = rawItemId==null?0:Long.parseLong(rawItemId);
        String rawValue=request.getParameter("value");

        float preference =rawValue==null?0f:Float.parseFloat(request.getParameter("value"));

        srr.setUserId(userId);
        srr.setItemId(itemId);
        srr.setPreference(preference);
        return srr;
    }

    private static void writeXML(HttpServletResponse response,
            Iterable<RecommendedItem> items) throws IOException {
        response.setContentType("application/xml");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter writer = response.getWriter();
        writer.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><recommendedItems>");
        for (RecommendedItem recommendedItem : items) {
            writer.print("<item><value>");
            writer.print(recommendedItem.getValue());
            writer.print("</value><id>");
            writer.print(recommendedItem.getItemID());
            writer.print("</id></item>");
        }
        writer.println("</recommendedItems>");
    }

    private static void writeJSON(HttpServletResponse response,
            Iterable<RecommendedItem> items) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter writer = response.getWriter();
        writer.print("{\"recommendedItems\":{\"item\":[");
        int i=0;
        for (RecommendedItem recommendedItem : items) {
            if(i>0){
                writer.print(',');
            }
            i++;
            writer.print("{\"value\":\"");
            writer.print(recommendedItem.getValue());
            writer.print("\",\"id\":\"");
            writer.print(recommendedItem.getItemID());
            writer.print("\"}");
        }
        writer.println("]}}");
    }

    private void writePlainText(HttpServletResponse response,
            Iterable<RecommendedItem> items) throws IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter writer = response.getWriter();
        for (RecommendedItem recommendedItem : items) {
            writer.print(recommendedItem.getValue());
            writer.print('\t');
            writer.println(recommendedItem.getItemID());
        }
    }

}
