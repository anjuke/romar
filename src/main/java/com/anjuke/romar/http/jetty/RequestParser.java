package com.anjuke.romar.http.jetty;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.mahout.common.ClassUtils;

import com.anjuke.romar.core.RomarRequest;
import com.anjuke.romar.core.impl.BadRequest;

public class RequestParser {

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

    public void register(String path,
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
                try {
                    BeanUtils.setProperty(romarRequest, name, name);
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
