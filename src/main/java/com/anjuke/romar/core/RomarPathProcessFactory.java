/**
 * Copyright 2012 Anjuke Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.anjuke.romar.core;

import java.io.File;

import org.apache.mahout.cf.taste.model.IDMigrator;

import com.anjuke.romar.core.handlers.CommitHandler;
import com.anjuke.romar.core.handlers.CompactHandler;
import com.anjuke.romar.core.handlers.EstimateHandler;
import com.anjuke.romar.core.handlers.ItemRecommendHandler;
import com.anjuke.romar.core.handlers.RecommendHandler;
import com.anjuke.romar.core.handlers.RemoveHandler;
import com.anjuke.romar.core.handlers.RemoveItemHandler;
import com.anjuke.romar.core.handlers.RemoveUserHandler;
import com.anjuke.romar.core.handlers.SimilarUserHandler;
import com.anjuke.romar.core.handlers.UpdateHandler;
import com.anjuke.romar.core.impl.SimpleRomarDispatcher;
import com.anjuke.romar.mahout.MahoutService;
import com.anjuke.romar.mahout.factory.MahoutServiceFactory;
import com.anjuke.romar.mahout.model.BDBIDMigrator;
import com.anjuke.romar.mahout.model.RomarMemoryIDMigrator;

public final class RomarPathProcessFactory {

    private RomarPathProcessFactory() {

    }

    public static <T> T createPathProcessor(RomarDefaultPathFactory<T> factory) {
        factory.init();
        factory.setRecommend(RequestPath.RECOMMEND);
        factory.setUpdate(RequestPath.UPDATE);
        factory.setRemove(RequestPath.REMOVE);
        factory.setCommit(RequestPath.COMMIT);
        factory.setItemRecommend(RequestPath.ITEM_RECOMMEND);
        factory.setSimilarUser(RequestPath.SIMILAR_USER);
        factory.setOptimize(RequestPath.OPTIMIZE);
        factory.setEstimate(RequestPath.ESTIMATE);
        factory.setRemoveUser(RequestPath.REMOVE_USER);
        factory.setRemoveItem(RequestPath.REMOVE_ITEM);
        T instance = factory.getInstance();
        return instance;
    }

    private static class RomarCoreFactory implements RomarDefaultPathFactory<RomarCore> {
        private RomarConfig _config = RomarConfig.getInstance();
        private MahoutServiceFactory _serviceFactory = _config.getMahoutServiceFactory();
        private RomarCore _core = new RomarCore();
        private SimpleRomarDispatcher _dispatcher = new SimpleRomarDispatcher();
        private MahoutService _service = _serviceFactory.createService();
        private final static int BDB_CACHE_SIZE = 102400;


        private IDMigrator getIdMigrator(String type){
            String path = _config.getPersistencePath();
            if (path != null && !path.isEmpty()) {
                File userPath = new File(_config.getPersistencePath()
                        + File.separator + type);
                if (!userPath.exists()) {
                    userPath.mkdirs();
                }
                return new BDBIDMigrator(userPath.getPath(),
                        BDB_CACHE_SIZE);
            } else {
                return new RomarMemoryIDMigrator();
            }
        }

        @Override
        public RomarCore getInstance() {
            _dispatcher.prepare();
            if (_config.isAllowUserStringID()) {
               _core.setUserIdMigrator(getIdMigrator("user_id"));
            }

            if (_config.isAllowItemStringID()) {
                _core.setItemIdMigrator(getIdMigrator("item_id"));
             }

            _core.setDispatcher(_dispatcher);
            _core.setService(_service);
            return _core;
        }

        @Override
        public void setRecommend(RequestPath path) {
            _dispatcher.registerHandler(path, new RecommendHandler(_service));
        }

        @Override
        public void setUpdate(RequestPath path) {
            _dispatcher.registerHandler(path, new UpdateHandler(_service));
        }

        @Override
        public void setRemove(RequestPath path) {
            _dispatcher.registerHandler(path, new RemoveHandler(_service));
        }

        @Override
        public void setCommit(RequestPath path) {
            _dispatcher.registerHandler(path, new CommitHandler(_service));
        }

        @Override
        public void setItemRecommend(RequestPath path) {
            _dispatcher.registerHandler(path, new ItemRecommendHandler(_service));
        }

        @Override
        public void setOptimize(RequestPath path) {
            _dispatcher.registerHandler(path, new CompactHandler(_service));
        }

        @Override
        public void setEstimate(RequestPath path) {
            _dispatcher.registerHandler(path, new EstimateHandler(_service));
        }

        @Override
        public void setRemoveUser(RequestPath path) {
            _dispatcher.registerHandler(path, new RemoveUserHandler(_service));
        }

        @Override
        public void setRemoveItem(RequestPath path) {
            _dispatcher.registerHandler(path, new RemoveItemHandler(_service));
        }

        @Override
        public void setSimilarUser(RequestPath path) {
            _dispatcher.registerHandler(path, new SimilarUserHandler(_service));

        }

        @Override
        public void init() {

        }

    }

    public static RomarCore createCore() {
        return createPathProcessor(new RomarCoreFactory());
    }

}
