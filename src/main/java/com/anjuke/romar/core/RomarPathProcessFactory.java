package com.anjuke.romar.core;

import com.anjuke.romar.core.handlers.ItemRecommendHandler;
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
        factory.setItemRecommend("/item/recommend");
        T instance = factory.getInstance();
        return instance;
    }

    private static class RomarCoreFactory extends RomarDefaultPathFactory<RomarCore> {
        RomarCore core = new RomarCore();
        SimpleRomarDispacher dispacher = new SimpleRomarDispacher();
        MahoutService service = new MahoutServiceFactory().getService();
        @Override
        protected RomarCore getInstance() {
            dispacher.prepare();
            core.setDispatcher(dispacher);
            core.setService(service);
            return core;
        }

        @Override
        protected void setRecommend(String path) {
            dispacher.registerHandler(path, new RecommendHandler(service));



        }

        @Override
        protected void setUpdate(String path) {
             dispacher.registerHandler(path, new UpdateHandler(service));
        }

        @Override
        protected void setRemove(String path) {
            dispacher.registerHandler(path, new RemoveHandler(service));
        }

        @Override
        protected void setReload(String path) {
             dispacher.registerHandler(path, new ReloadHandler(service));
        }

        @Override
        protected void setItemRecommend(String path) {
            dispacher.registerHandler(path, new ItemRecommendHandler(service));
        }
    }


    public static RomarCore createCore() {
        return createPathProcessor(new RomarCoreFactory());
    }

}
