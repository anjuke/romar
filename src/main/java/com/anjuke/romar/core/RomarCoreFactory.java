package com.anjuke.romar.core;

import com.anjuke.romar.core.handlers.RecommendHandler;
import com.anjuke.romar.core.handlers.ReloadHandler;
import com.anjuke.romar.core.handlers.RemoveHandler;
import com.anjuke.romar.core.handlers.UpdateHandler;
import com.anjuke.romar.core.impl.SimpleRomarDispacher;
import com.anjuke.romar.mahout.MahoutService;
import com.anjuke.romar.mahout.MahoutServiceFactory;

public class RomarCoreFactory {
    public static RomarCore getCore(){
        MahoutService service=new MahoutServiceFactory().getService();
        SimpleRomarDispacher dispacher=new SimpleRomarDispacher();
        dispacher.registerHandler("/recommend", new RecommendHandler(service));
        dispacher.registerHandler("/update", new UpdateHandler(service));
        dispacher.registerHandler("/remove", new RemoveHandler(service));
        dispacher.registerHandler("/reload", new ReloadHandler(service));
        dispacher.prepare();
        RomarCore core=new RomarCore();
        core.setDispatcher(dispacher);
        core.setService(service);
        return core;
    }
}
