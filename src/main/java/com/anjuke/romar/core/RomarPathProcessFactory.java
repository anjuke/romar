package com.anjuke.romar.core;

import com.anjuke.romar.core.handlers.CompactHandler;
import com.anjuke.romar.core.handlers.EstimateHandler;
import com.anjuke.romar.core.handlers.ItemRecommendHandler;
import com.anjuke.romar.core.handlers.RecommendHandler;
import com.anjuke.romar.core.handlers.CommitHandler;
import com.anjuke.romar.core.handlers.RemoveHandler;
import com.anjuke.romar.core.handlers.UpdateHandler;
import com.anjuke.romar.core.impl.SimpleRomarDispatcher;
import com.anjuke.romar.mahout.MahoutService;
import com.anjuke.romar.mahout.factory.MahoutServiceFactory;

public final class RomarPathProcessFactory {

    private RomarPathProcessFactory(){

    }

    public static <T> T createPathProcessor(RomarDefaultPathFactory<T> factory) {
        factory.init();
        factory.setRecommend(RequestPath.RECOMMEND);
        factory.setUpdate(RequestPath.UPDATE);
        factory.setRemove(RequestPath.REMOVE);
        factory.setCommit(RequestPath.COMMIT);
        factory.setItemRecommend(RequestPath.ITEM_RECOMMEND);
        factory.setCompact(RequestPath.COMPACT);
        factory.setEstimate(RequestPath.ESTIMATE);
        T instance = factory.getInstance();
        return instance;
    }

    private static class RomarCoreFactory extends
            RomarDefaultPathFactory<RomarCore> {
        private RomarConfig _config = RomarConfig.getInstance();
        private MahoutServiceFactory _serviceFactory = _config.getMahoutServiceFactory();
        private RomarCore _core = new RomarCore();
        private SimpleRomarDispatcher _dispatcher = new SimpleRomarDispatcher();
        private MahoutService _service = _serviceFactory.createService();

        @Override
        protected RomarCore getInstance() {
            _dispatcher.prepare();
            _core.setDispatcher(_dispatcher);
            _core.setService(_service);
            return _core;
        }

        @Override
        protected void setRecommend(RequestPath path) {
            _dispatcher.registerHandler(path, new RecommendHandler(_service));
        }

        @Override
        protected void setUpdate(RequestPath path) {
            _dispatcher.registerHandler(path, new UpdateHandler(_service));
        }

        @Override
        protected void setRemove(RequestPath path) {
            _dispatcher.registerHandler(path, new RemoveHandler(_service));
        }

        @Override
        protected void setCommit(RequestPath path) {
            _dispatcher.registerHandler(path, new CommitHandler(_service));
        }

        @Override
        protected void setItemRecommend(RequestPath path) {
            _dispatcher.registerHandler(path, new ItemRecommendHandler(_service));
        }

        @Override
        protected void setCompact(RequestPath path) {
            _dispatcher.registerHandler(path, new CompactHandler(_service));
        }

        @Override
        protected void setEstimate(RequestPath path) {
            _dispatcher.registerHandler(path, new EstimateHandler(_service));
        }
    }

    public static RomarCore createCore() {
        return createPathProcessor(new RomarCoreFactory());
    }

}
