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
package com.anjuke.romar.http.jetty;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.mahout.common.ClassUtils;

import com.anjuke.romar.core.RequestPath;
import com.anjuke.romar.core.RomarDefaultPathFactory;
import com.anjuke.romar.core.RomarPathProcessFactory;
import com.anjuke.romar.core.RomarRequest;
import com.anjuke.romar.core.impl.request.BadRequest;
import com.anjuke.romar.core.impl.request.MultiItemIdRequest;
import com.anjuke.romar.core.impl.request.NoneContentRequest;
import com.anjuke.romar.core.impl.request.PreferenceRomarRequest;

public class RequestParser {
    private final static String RECOMMEND = "/recommend";
    private final static String UPDATE = "/update";
    private final static String REMOVE = "/remove";
    private final static String COMMIT = "/commit";
    private final static String SIMILAR_USER = "/user/similar";
    private final static String ITEM_RECOMMEND = "/item/recommend";
    private final static String COMPACT = "/optimize";
    private final static String ESTMATE = "/estmate";
    private final static String REMOVE_USER = "/user/remove";
    private final static String REMOVE_ITEM = "/item/remove";

    private static class RequestParserFactory implements
            RomarDefaultPathFactory<RequestParser> {
        RequestParser _parser = new RequestParser();

        public RequestParser getInstance() {
            return _parser;
        }

        @Override
        public void setRecommend(RequestPath path) {
            _parser._stringToPathMap.put(RECOMMEND, path);
            _parser.register(RECOMMEND, PreferenceRomarRequest.class,
                    Arrays.asList("userId"));

        }

        @Override
        public void setUpdate(RequestPath path) {
            _parser._stringToPathMap.put(UPDATE, path);
            _parser.register(UPDATE, PreferenceRomarRequest.class,
                    Arrays.asList("userId", "itemId", "value"));

        }

        @Override
        public void setRemove(RequestPath path) {
            _parser._stringToPathMap.put(REMOVE, path);
            _parser.register(REMOVE, PreferenceRomarRequest.class,
                    Arrays.asList("userId", "itemId"));

        }

        @Override
        public void setCommit(RequestPath path) {
            _parser._stringToPathMap.put(COMMIT, path);
            _parser.register(COMMIT, NoneContentRequest.class,
                    Collections.<String> emptyList());
        }

        @Override
        public void setItemRecommend(RequestPath path) {
            _parser._stringToPathMap.put(ITEM_RECOMMEND, path);
            _parser.register(ITEM_RECOMMEND, MultiItemIdRequest.class,
                    Arrays.asList("itemId"));
        }

        @Override
        public void setOptimize(RequestPath path) {
            _parser._stringToPathMap.put(COMPACT, path);
            _parser.register(COMPACT, NoneContentRequest.class,
                    Collections.<String> emptyList());
        }

        @Override
        public void init() {

        }

        @Override
        public void setEstimate(RequestPath path) {
            _parser._stringToPathMap.put(ESTMATE, path);
            _parser.register(ESTMATE, PreferenceRomarRequest.class,
                    Arrays.asList("userId", "itemId"));

        }

        @Override
        public void setRemoveUser(RequestPath path) {
            _parser._stringToPathMap.put(REMOVE_USER, path);
            _parser.register(REMOVE_USER, PreferenceRomarRequest.class,
                    Arrays.asList("userId"));

        }

        @Override
        public void setRemoveItem(RequestPath path) {
            _parser._stringToPathMap.put(REMOVE_ITEM, path);
            _parser.register(REMOVE_ITEM, PreferenceRomarRequest.class,
                    Arrays.asList("itemId"));
        }

        @Override
        public void setSimilarUser(RequestPath path) {
            _parser._stringToPathMap.put(SIMILAR_USER, path);
            _parser.register(SIMILAR_USER, PreferenceRomarRequest.class,
                    Arrays.asList("userId"));
        }

    }

    public static RequestParser createParser() {
        return INSTANCE;
    }

    private final static RequestParser INSTANCE = RomarPathProcessFactory
            .createPathProcessor(new RequestParserFactory());

    private static class ParamMeta {
        Class<? extends RomarRequest> _clazz;
        List<String> _list;

        public ParamMeta(Class<? extends RomarRequest> clazz, List<String> list) {
            super();
            _clazz = clazz;
            _list = list;
        }
    }

    private final Map<String, ParamMeta> _params = new HashMap<String, ParamMeta>();

    private final Map<String, RequestPath> _stringToPathMap = new HashMap<String, RequestPath>();

    private void register(String path, Class<? extends RomarRequest> requestClass,
            List<String> paramsNames) {
        _params.put(path, new ParamMeta(requestClass, paramsNames));
    }

    public RomarRequest parseRequest(String path, HttpServletRequest request) {
        ParamMeta meta = _params.get(path);
        RequestPath requestPath = _stringToPathMap.get(path);
        if (meta == null) {
            return new BadRequest(requestPath);
        } else {
            RomarRequest romarRequest = ClassUtils.instantiateAs(meta._clazz,
                    RomarRequest.class, new Class<?>[] {RequestPath.class},
                    new Object[] {requestPath});
            for (String name : meta._list) {
                String[] values = request.getParameterValues(name);
                if (values == null) {
                    return new BadRequest(requestPath);
                }

                try {
                    if (values.length == 1) {
                        BeanUtils.setProperty(romarRequest, name, values[0]);
                    } else {
                        BeanUtils.setProperty(romarRequest, name, values);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            return romarRequest;
        }
    }
}
