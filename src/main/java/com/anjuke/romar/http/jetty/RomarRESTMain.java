package com.anjuke.romar.http.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.sun.jersey.spi.container.servlet.ServletContainer;

public final class RomarRESTMain {

    private RomarRESTMain() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("usage :  java classname $port");
            return;
        }
        Server server = new Server(Integer.parseInt(args[0]));
        ServletContextHandler context = new ServletContextHandler(
                ServletContextHandler.NO_SESSIONS);
        ServletHolder servletHolder = new ServletHolder(new ServletContainer());
        servletHolder.setInitParameter("com.sun.jersey.config.property.packages",
                "com.anjuke.romar.http.rest");
        servletHolder.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
                "true");
        context.addServlet(servletHolder, "/*");
        server.setHandler(context);
        server.start();
        server.join();
    }
}
