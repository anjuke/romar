package com.anjuke.romar.mahout.similarity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.GenericUserSimilarity;

public class ReadableGenericSimilarityProxy implements InvocationHandler {
    private final Object target;
    private final Field field;

    private ReadableGenericSimilarityProxy(Object target) {
        super();
        this.target = target;
        try {
            field = target.getClass().getDeclaredField("similarityMaps");
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static ReadableGenericItemSimilarity proxySimilarity(
            GenericItemSimilarity simialrity) {
        return proxy(simialrity,ReadableGenericItemSimilarity.class);
    }

    public static ReadableGenericUserSimilarity proxySimilarity(
            GenericUserSimilarity simialrity) {
        return proxy(simialrity,ReadableGenericUserSimilarity.class);
    }


    @SuppressWarnings("unchecked")
    private static <T> T proxy(Object o,Class<T> intf){
        return (T)Proxy.newProxyInstance(o.getClass().getClassLoader(),
                new Class<?>[]{ intf}, new ReadableGenericSimilarityProxy(
                         o));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == ReadableSimilarity.class
                && method.getName().equals("getSimilarityMaps")) {
            boolean wasAccessible = field.isAccessible();
            field.setAccessible(true);
            Object value = field.get(target);
            field.setAccessible(wasAccessible);
            return value;
        }

        return method.invoke(target, args);
    }

}
