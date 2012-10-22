package com.anjuke.romar.http.jetty;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.mahout.common.ClassUtils;

import com.anjuke.romar.core.RomarDefaultPathFactory;
import com.anjuke.romar.core.RomarPathProcessFactory;
import com.anjuke.romar.core.RomarRequest;
import com.anjuke.romar.core.impl.request.BadRequest;
import com.anjuke.romar.core.impl.request.MultiItemIdRequest;
import com.anjuke.romar.core.impl.request.NoneContentRequest;
import com.anjuke.romar.core.impl.request.PreferenceRomarRequest;

public class RequestParser {

    private static class RequestParserFactory extends RomarDefaultPathFactory<RequestParser>{
        RequestParser parser=new RequestParser();
        @Override
        protected RequestParser getInstance() {
            return parser;
        }


        @Override
        protected void setRecommend(String path) {
            parser.register(path, PreferenceRomarRequest.class, Arrays.asList("userId"));

        }

        @Override
        protected void setUpdate(String path) {
            parser.register(path, PreferenceRomarRequest.class, Arrays.asList("userId","itemId","value"));

        }

        @Override
        protected void setRemove(String path) {
            parser.register(path, PreferenceRomarRequest.class, Arrays.asList("userId","itemId"));

        }

        @Override
        protected void setCommit(String path) {
            parser.register(path, NoneContentRequest.class, Collections.<String>emptyList());
        }

        @Override
        protected void setItemRecommend(String path) {
            parser.register(path, MultiItemIdRequest.class, Arrays.asList("itemId"));
        }

    }

    public static RequestParser createParser(){
        return RomarPathProcessFactory.createPathProcessor(new RequestParserFactory());
    }



    private static class ParamMeta {
        Class<? extends RomarRequest> clazz;
        List<String> list;

        public ParamMeta(Class<? extends RomarRequest> clazz, List<String> list) {
            super();
            this.clazz = clazz;
            this.list = list;
        }
    }

    Map<String, ParamMeta> params = new HashMap<String, ParamMeta>();

    private void register(String path,
            Class<? extends RomarRequest> requestClass, List<String> paramsNames) {
        params.put(path, new ParamMeta(requestClass, paramsNames));
    }

    public RomarRequest parseRequest(String path, HttpServletRequest request) {
        ParamMeta meta = params.get(path);
        if (meta == null) {
            return new BadRequest(path);
        } else {
            RomarRequest romarRequest = ClassUtils.instantiateAs(meta.clazz,
                    RomarRequest.class, new Class<?>[] { String.class },
                    new Object[] { path });
            for (String name : meta.list) {
                String[] values=request.getParameterValues(name);
                if(values==null){
                    return new BadRequest(path);
                }

                try {
                    if(values.length==1){
                        BeanUtils.setProperty(romarRequest, name, values[0]);
                    }else{
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
