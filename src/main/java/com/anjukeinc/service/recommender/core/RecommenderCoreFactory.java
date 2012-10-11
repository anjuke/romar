package com.anjukeinc.service.recommender.core;

import com.anjukeinc.service.recommender.core.handlers.RecommendHandler;
import com.anjukeinc.service.recommender.core.handlers.ReloadHandler;
import com.anjukeinc.service.recommender.core.handlers.RemoveHandler;
import com.anjukeinc.service.recommender.core.handlers.UpdateHandler;
import com.anjukeinc.service.recommender.core.impl.SimpleRecommenderDispacher;
import com.anjukeinc.service.recommender.mahout.MahoutService;
import com.anjukeinc.service.recommender.mahout.MahoutServiceFactory;

public class RecommenderCoreFactory {
    public static RecommenderCore getCore(){
        MahoutService service=new MahoutServiceFactory().getService();
        SimpleRecommenderDispacher dispacher=new SimpleRecommenderDispacher();
        dispacher.registerHandler("/recommend", new RecommendHandler(service));
        dispacher.registerHandler("/update", new UpdateHandler(service));
        dispacher.registerHandler("/remove", new RemoveHandler(service));
        dispacher.registerHandler("/reload", new ReloadHandler(service));
        dispacher.prepare();
        RecommenderCore core=new RecommenderCore();
        core.setDispatcher(dispacher);
        core.setService(service);
        return core;
    }
}
