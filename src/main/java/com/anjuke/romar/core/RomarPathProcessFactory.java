package com.anjuke.romar.core;

import com.anjuke.romar.core.handlers.CompactHandler;
import com.anjuke.romar.core.handlers.ItemRecommendHandler;
import com.anjuke.romar.core.handlers.RecommendHandler;
import com.anjuke.romar.core.handlers.CommitHandler;
import com.anjuke.romar.core.handlers.RemoveHandler;
import com.anjuke.romar.core.handlers.UpdateHandler;
import com.anjuke.romar.core.impl.SimpleRomarDispatcher;
import com.anjuke.romar.mahout.MahoutService;
import com.anjuke.romar.mahout.factory.MahoutServiceFactory;

public class RomarPathProcessFactory {

    public static <T> T createPathProcessor(RomarDefaultPathFactory<T> factory) {
        factory.init();
        factory.setRecommend(RequestPath.RECOMMEND);
        factory.setUpdate(RequestPath.UPDATE);
        factory.setRemove(RequestPath.REMOVE);
        factory.setCommit(RequestPath.COMMIT);
        factory.setItemRecommend(RequestPath.ITEM_RECOMMEND);
        factory.setCompact(RequestPath.COMPACT);
        T instance = factory.getInstance();
        return instance;
    }

    private static class RomarCoreFactory extends
            RomarDefaultPathFactory<RomarCore> {
        private RomarConfig config = RomarConfig.getInstance();
        private MahoutServiceFactory serviceFactory = config.getMahoutServiceFactory();
        private RomarCore core = new RomarCore();
        private SimpleRomarDispatcher dispatcher = new SimpleRomarDispatcher();
        private MahoutService service = serviceFactory.createService();

        @Override
        protected RomarCore getInstance() {
            dispatcher.prepare();
            core.setDispatcher(dispatcher);
            core.setService(service);
            return core;
        }

        @Override
        protected void setRecommend(RequestPath path) {
            dispatcher.registerHandler(path, new RecommendHandler(service));
        }

        @Override
        protected void setUpdate(RequestPath path) {
            dispatcher.registerHandler(path, new UpdateHandler(service));
        }

        @Override
        protected void setRemove(RequestPath path) {
            dispatcher.registerHandler(path, new RemoveHandler(service));
        }

        @Override
        protected void setCommit(RequestPath path) {
            dispatcher.registerHandler(path, new CommitHandler(service));
        }

        @Override
        protected void setItemRecommend(RequestPath path) {
            dispatcher.registerHandler(path, new ItemRecommendHandler(service));
        }

        @Override
        protected void setCompact(RequestPath path) {
            dispatcher.registerHandler(path, new CompactHandler(service));
        }
    }

    public static RomarCore createCore() {
        return createPathProcessor(new RomarCoreFactory());
    }

}
