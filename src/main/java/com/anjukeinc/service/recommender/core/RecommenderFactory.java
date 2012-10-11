package com.anjukeinc.service.recommender.core;

import com.anjukeinc.service.recommender.core.handlers.RecommendHandler;
import com.anjukeinc.service.recommender.core.handlers.UpdateHandler;
import com.anjukeinc.service.recommender.core.impl.SimpleRecommenderDispacher;
import com.anjukeinc.service.recommender.mahout.MahoutService;
import com.anjukeinc.service.recommender.mahout.MahoutServiceFactory;

public class RecommenderFactory {
    public static RecommenderCore getDefaultRecommenderCore() {
        RecommenderCore core = new RecommenderCore();
        MahoutService service=new MahoutServiceFactory().getService();
        SimpleRecommenderDispacher dispacher = new SimpleRecommenderDispacher();
        dispacher.registerHandler("/update", new UpdateHandler(service));
        dispacher.registerHandler("/recommend", new RecommendHandler(service));

        dispacher.prepare();
        core.setDispatcher(dispacher);
        core.setService(service);


        return core;
    }
}
