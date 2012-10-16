package com.anjuke.romar.core;

import com.anjuke.romar.core.handlers.RecommendHandler;
import com.anjuke.romar.core.handlers.ReloadHandler;
import com.anjuke.romar.core.handlers.RemoveHandler;
import com.anjuke.romar.core.handlers.UpdateHandler;
import com.anjuke.romar.core.impl.SimpleRomarDispacher;
import com.anjuke.romar.mahout.MahoutService;
import com.anjuke.romar.mahout.MahoutServiceFactory;

public class RomarPathProcessFactory {

    public static <T> T createPathProcessor(RomarDefaultPathFactory<T> factory) {
        factory.init();
        factory.setRecommend("/recommend");
        factory.setUpdate("/update");
        factory.setRemove("/remove");
        factory.setReload("/reload");
        T instance = factory.getInstance();
        return instance;
    }

    private RomarCore core = new RomarCore();

    public void createInstance() {
        SimpleRomarDispacher dispacher = new SimpleRomarDispacher();
        MahoutService service = new MahoutServiceFactory().getService();
        dispacher.registerHandler("/recommend", new RecommendHandler(service));
        dispacher.registerHandler("/update", new UpdateHandler(service));
        dispacher.registerHandler("/remove", new RemoveHandler(service));
        dispacher.registerHandler("/reload", new ReloadHandler(service));
        dispacher.prepare();
        core.setDispatcher(dispacher);
        core.setService(service);
    }

    public RomarCore getCore() {
        return core;
    }

}
