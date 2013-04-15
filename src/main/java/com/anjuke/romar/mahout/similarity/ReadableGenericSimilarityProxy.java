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
package com.anjuke.romar.mahout.similarity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.GenericUserSimilarity;

public  final class ReadableGenericSimilarityProxy implements InvocationHandler {
    private final Object _target;
    private final Field _field;

    private ReadableGenericSimilarityProxy(Object target) {
        super();
        this._target = target;
        try {
            _field = target.getClass().getDeclaredField("similarityMaps");
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static ReadableGenericItemSimilarity proxySimilarity(
            GenericItemSimilarity simialrity) {
        return proxy(simialrity, ReadableGenericItemSimilarity.class);
    }

    public static ReadableGenericUserSimilarity proxySimilarity(
            GenericUserSimilarity simialrity) {
        return proxy(simialrity, ReadableGenericUserSimilarity.class);
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Object o, Class<T> intf) {
        return (T) Proxy.newProxyInstance(o.getClass().getClassLoader(),
                new Class<?>[] {intf}, new ReadableGenericSimilarityProxy(o));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == ReadableSimilarity.class
                && method.getName().equals("getSimilarityMaps")) {
            boolean wasAccessible = _field.isAccessible();
            _field.setAccessible(true);
            Object value = _field.get(_target);
            _field.setAccessible(wasAccessible);
            return value;
        }

        return method.invoke(_target, args);
    }

}
