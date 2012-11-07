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
package com.anjuke.romar.http.jetty;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.romar.core.RomarCore;
import com.anjuke.romar.core.RomarRequest;
import com.anjuke.romar.core.RomarResponse;
import com.anjuke.romar.core.impl.response.ErrorResponse;
import com.anjuke.romar.core.impl.response.MultiValueResponse;
import com.anjuke.romar.core.impl.response.RecommendResultResponse;
import com.anjuke.romar.core.impl.response.SuccessReplyNoneResponse;
import com.anjuke.romar.core.impl.response.ValueResponse;

public class JettyRomarHandler extends AbstractHandler {
    private static final Logger _logger = LoggerFactory.getLogger(JettyRomarHandler.class);

    private final RomarCore _core;
    private final RequestParser _parser;
    public JettyRomarHandler(RomarCore core) {
        _core = core;
        _parser = RequestParser.createParser();
    }

    @Override
    public void handle(String target, Request baseRequest,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String path = request.getPathInfo();
        _logger.debug(path);
        RomarRequest romarRequest;
        romarRequest = getRequest(path, request);
        // if (romarRequest instanceof BadRequest) {
        //    //TODO
        //}
        RomarResponse romarResponse = _core.execute(romarRequest);

        if (romarResponse instanceof ErrorResponse) {
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setStatus(((ErrorResponse) romarResponse).getCode());
            response.getWriter().println(
                    ((ErrorResponse) romarResponse).getMessage());
            response.getWriter().flush();

        } else if (romarResponse instanceof SuccessReplyNoneResponse) {
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setStatus(HttpStatus.OK_200);
            response.getWriter().println("success");
            response.getWriter().flush();
        } else if (romarResponse instanceof RecommendResultResponse) {
            response.setStatus(HttpStatus.OK_200);
            List<RecommendedItem> items = ((RecommendResultResponse) romarResponse)
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
        }else if(romarResponse instanceof ValueResponse){
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setStatus(HttpStatus.OK_200);
            response.getWriter().println(((ValueResponse) romarResponse).getValue());
            response.getWriter().flush();
        }else if (romarResponse instanceof MultiValueResponse){
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setStatus(HttpStatus.OK_200);
            response.getWriter().println(((MultiValueResponse) romarResponse).getValues());
            response.getWriter().flush();
        }

    }

    private RomarRequest getRequest(String path, HttpServletRequest request){
        return _parser.parseRequest(path, request);
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
        int i = 0;
        for (RecommendedItem recommendedItem : items) {
            if (i > 0){
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
