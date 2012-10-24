package com.anjuke.romar.http.jetty;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
    private final static String ITEM_RECOMMEND = "/item/recommend";
    private final static String COMPACT = "/compact";

    private static class RequestParserFactory extends
            RomarDefaultPathFactory<RequestParser> {
        RequestParser parser = new RequestParser();

        @Override
        protected RequestParser getInstance() {
            return parser;
        }

        @Override
        protected void setRecommend(RequestPath path) {
            parser.stringToPathMap.put(RECOMMEND, path);
            parser.register(RECOMMEND, PreferenceRomarRequest.class,
                    Arrays.asList("userId"));

        }

        @Override
        protected void setUpdate(RequestPath path) {
            parser.stringToPathMap.put(UPDATE, path);
            parser.register(UPDATE, PreferenceRomarRequest.class,
                    Arrays.asList("userId", "itemId", "value"));

        }

        @Override
        protected void setRemove(RequestPath path) {
            parser.stringToPathMap.put(REMOVE, path);
            parser.register(REMOVE, PreferenceRomarRequest.class,
                    Arrays.asList("userId", "itemId"));

        }

        @Override
        protected void setCommit(RequestPath path) {
            parser.stringToPathMap.put(COMMIT, path);
            parser.register(COMMIT, NoneContentRequest.class,
                    Collections.<String> emptyList());
        }

        @Override
        protected void setItemRecommend(RequestPath path) {
            parser.stringToPathMap.put(ITEM_RECOMMEND, path);
            parser.register(ITEM_RECOMMEND, MultiItemIdRequest.class,
                    Arrays.asList("itemId"));
        }

        @Override
        protected void setCompact(RequestPath path) {
            parser.stringToPathMap.put(COMPACT, path);
            parser.register(COMPACT, NoneContentRequest.class,
                    Collections.<String> emptyList());
        }

    }

    public static RequestParser createParser() {
        return INSTANCE;
    }

    private final static RequestParser INSTANCE = RomarPathProcessFactory
            .createPathProcessor(new RequestParserFactory());

    private static class ParamMeta {
        Class<? extends RomarRequest> clazz;
        List<String> list;

        public ParamMeta(Class<? extends RomarRequest> clazz, List<String> list) {
            super();
            this.clazz = clazz;
            this.list = list;
        }
    }

    private final Map<String, ParamMeta> params = new HashMap<String, ParamMeta>();

    private final Map<String, RequestPath> stringToPathMap = new HashMap<String, RequestPath>();

    private void register(String path,
            Class<? extends RomarRequest> requestClass, List<String> paramsNames) {
        params.put(path, new ParamMeta(requestClass, paramsNames));
    }

    public RomarRequest parseRequest(String path, HttpServletRequest request) {
        ParamMeta meta = params.get(path);
        RequestPath requestPath = stringToPathMap.get(path);
        if (meta == null) {
            return new BadRequest(requestPath);
        } else {
            RomarRequest romarRequest = ClassUtils.instantiateAs(meta.clazz,
                    RomarRequest.class, new Class<?>[] {RequestPath.class},
                    new Object[] {requestPath});
            for (String name : meta.list) {
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
