package com.anjukeinc.service.recommender.http.jetty;

import org.eclipse.jetty.server.Server;

import com.anjukeinc.service.recommender.core.RecommenderCore;
import com.anjukeinc.service.recommender.core.RecommenderCoreFactory;

public class RecommenderMain {
    public static void main(String[] args) throws Exception {
        if(args.length!=1){
            System.out.println("usage :  java classname $port");
            return;
        }

        RecommenderCore core=RecommenderCoreFactory.getCore();
        Server server = new Server(Integer.parseInt(args[0]));
        server.setHandler(new JettyRecommenderHandler(core));
        server.start();
        server.join();
    }
}
