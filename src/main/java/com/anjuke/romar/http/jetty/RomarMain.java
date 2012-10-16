package com.anjuke.romar.http.jetty;

import org.eclipse.jetty.server.Server;

import com.anjuke.romar.core.RomarCore;
import com.anjuke.romar.core.RomarPathProcessFactory;

public class RomarMain {
    public static void main(String[] args) throws Exception {
        if(args.length!=1){
            System.out.println("usage :  java classname $port");
            return;
        }
        RomarCore core=RomarPathProcessFactory.getCore();
        Server server = new Server(Integer.parseInt(args[0]));
        server.setHandler(new JettyRomarHandler(core));
        server.start();
        server.join();
    }
}
