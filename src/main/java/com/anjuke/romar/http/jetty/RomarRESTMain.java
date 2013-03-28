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

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import ch.qos.logback.access.jetty.RequestLogImpl;

import com.anjuke.romar.core.CoreContainer;
import com.anjuke.romar.core.RomarConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public final class RomarRESTMain {
    private final static String ACCESS_LOG_CONF_FILE = "access.xml";

    private RomarRESTMain() {
    }

    public static void main(String[] args) throws Exception {
        java.util.logging.Logger rootLogger = java.util.logging.LogManager
                .getLogManager().getLogger("");
        java.util.logging.Handler[] logHandlers = rootLogger.getHandlers();
        for (int i = 0; i < logHandlers.length; i++) {
            rootLogger.removeHandler(logHandlers[i]);
        }
        org.slf4j.bridge.SLF4JBridgeHandler.install();
        RomarConfig config = RomarConfig.getInstance();
        //fast fail init
        CoreContainer.getCore();
        Server server = new Server(config.getServerPort());
        ServletContextHandler context = new ServletContextHandler(
                ServletContextHandler.NO_SESSIONS);
        ServletHolder servletHolder = new ServletHolder(new ServletContainer());
        servletHolder.setInitParameter("com.sun.jersey.config.property.packages",
                "com.anjuke.romar.http.rest;org.codehaus.jackson.jaxrs;com.anjuke.romar.http.rest.exception");
        servletHolder.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
                "true");

        HandlerCollection handlers = new HandlerCollection();
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        RequestLogImpl requestLog = new RequestLogImpl();
        String romarHome = System.getProperty("romar.home");
        if (romarHome == null) {
            requestLog.setResource("/" + ACCESS_LOG_CONF_FILE);
        } else {
            requestLog.setFileName(romarHome + "/conf/" + ACCESS_LOG_CONF_FILE);
        }

        requestLogHandler.setRequestLog(requestLog);

        handlers.setHandlers(new Handler[] {context, requestLogHandler});

        context.addServlet(servletHolder, "/*");
        server.setHandler(handlers);
        server.start();
        server.join();
    }
}
