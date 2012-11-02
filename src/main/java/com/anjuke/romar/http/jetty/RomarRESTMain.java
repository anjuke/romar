package com.anjuke.romar.http.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.anjuke.romar.core.RomarConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public final class RomarRESTMain {

    private RomarRESTMain() {
    }

    public static void main(String[] args) throws Exception {
        java.util.logging.Logger rootLogger = java.util.logging.LogManager
                .getLogManager().getLogger("");
        java.util.logging.Handler[] handlers = rootLogger.getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            rootLogger.removeHandler(handlers[i]);
        }
        org.slf4j.bridge.SLF4JBridgeHandler.install();
        RomarConfig config = RomarConfig.getInstance();

        Server server = new Server(config.getServerPort());
        ServletContextHandler context = new ServletContextHandler(
                ServletContextHandler.NO_SESSIONS);
        ServletHolder servletHolder = new ServletHolder(new ServletContainer());
        servletHolder.setInitParameter("com.sun.jersey.config.property.packages",
                "com.anjuke.romar.http.rest;org.codehaus.jackson.jaxrs");
        servletHolder.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
                "true");
        context.addServlet(servletHolder, "/*");
        server.setHandler(context);
        server.start();
        server.join();
    }
}
