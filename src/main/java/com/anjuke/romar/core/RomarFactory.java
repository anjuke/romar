package com.anjuke.romar.core;

import com.anjuke.romar.core.handlers.RecommendHandler;
import com.anjuke.romar.core.handlers.UpdateHandler;
import com.anjuke.romar.core.impl.SimpleRomarDispacher;
import com.anjuke.romar.mahout.MahoutService;
import com.anjuke.romar.mahout.MahoutServiceFactory;

public class RomarFactory {
    public static RomarCore getDefaultRecommenderCore() {
        RomarCore core = new RomarCore();
        MahoutService service=new MahoutServiceFactory().getService();
        SimpleRomarDispacher dispacher = new SimpleRomarDispacher();
        dispacher.registerHandler("/update", new UpdateHandler(service));
        dispacher.registerHandler("/recommend", new RecommendHandler(service));

        dispacher.prepare();
        core.setDispatcher(dispacher);
        core.setService(service);


        return core;
    }
}
