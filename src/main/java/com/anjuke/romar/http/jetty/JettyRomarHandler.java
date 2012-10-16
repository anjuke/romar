package com.anjuke.romar.http.jetty;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.anjuke.romar.core.RomarCore;
import com.anjuke.romar.core.RomarRequest;
import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.request.BadRequest;
import com.anjuke.romar.core.impl.response.ErrorResponse;
import com.anjuke.romar.core.impl.response.RecommendResultResponse;
import com.anjuke.romar.core.impl.response.SuccessReplyNoneResponse;

public class JettyRomarHandler extends AbstractHandler {
    private final RomarCore core;
    private final RequestParser parser;
    public JettyRomarHandler(RomarCore core) {
        this.core = core;
        parser= RequestParser.createParser();
    }

    @Override
    public void handle(String target, Request baseRequest,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String path=request.getPathInfo();
        System.out.println(path);

        RomarRequest recomRequest;
        recomRequest = getRequest(path, request);
        if(recomRequest instanceof BadRequest){
            //TODO
        }
        RomarResponse recomResponse = core.execute(recomRequest);

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

    private RomarRequest getRequest(String path, HttpServletRequest request){
        return parser.parseRequest(path, request);
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
