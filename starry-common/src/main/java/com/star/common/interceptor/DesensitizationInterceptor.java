package com.star.common.interceptor;

import com.star.common.annotation.Desensitization;
import com.star.common.entity.DesensitizationType;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * SQL拦截器，用于数据脱敏
 *
 * @Author: zzStar
 * @Date: 03-07-2021 13:37
 */
@Data
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),})
public class DesensitizationInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = invocation.proceed();
        if (result instanceof ArrayList<?>) {
            List<?> list = (ArrayList<?>) result;
            return desensitization(list);
        } else {
            return desensitization(result);
        }
    }

    /**
     * 让mybatis判断，要不要进行拦截，然后做出决定是否生成一个代理
     * Plugin的wrap方法，它根据当前的Interceptor上面的注解定义哪些接口需要拦截，然后判断当前目标对象是否有实现对应需要拦截的接口，
     * 如果没有则返回目标对象本身，如果有则返回一个代理对象。而这个代理对象的InvocationHandler正是一个Plugin。
     * 所以当目标对象在执行接口方法时，如果是通过代理对象执行的，则会调用对应InvocationHandler的invoke方法，也就是Plugin的invoke方法。
     * 所以接着我们来看一下该invoke方法的内容。这里invoke方法的逻辑是：如果当前执行的方法是定义好的需要拦截的方法，
     * 则把目标对象、要执行的方法以及方法参数封装成一个Invocation对象，再把封装好的Invocation作为参数传递给当前拦截器的intercept方法。
     * 如果不需要拦截，则直接调用当前的方法。Invocation中定义了定义了一个proceed方法，其逻辑就是调用当前方法，
     * 所以如果在intercept中需要继续调用当前方法的话可以调用invocation的procced方法。
     *
     * @param target
     * @return
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private List<?> desensitization(List<?> list) {
        Class<?> cls = null;
        Field[] objFields = null;
        if (list != null && list.size() > 0) {
            for (Object o : list) {
                if (cls == null) {
                    cls = o.getClass();
                    objFields = cls.getDeclaredFields();
                }
                desensitizationField(o, objFields);
            }
        }
        return list;
    }

    private Object desensitization(Object obj) {
        Class<?> cls;
        Field[] objFields;
        if (obj != null) {
            cls = obj.getClass();
            objFields = cls.getDeclaredFields();
            desensitizationField(obj, objFields);
        }
        return obj;
    }

    private void desensitizationField(Object obj, Field[] objFields) {
        for (Field field : objFields) {
            Desensitization desensitization;
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }
            if (String.class != field.getType() || (desensitization = field.getAnnotation(Desensitization.class)) == null) {
                continue;
            }
            field.setAccessible(true);
            String value;
            try {
                value = field.get(obj) != null ? field.get(obj).toString() : null;
            } catch (Exception e) {
                value = null;
            }
            if (value == null) {
                continue;
            }
            List<String> regular;
            DesensitizationType type = desensitization.type();
            regular = Arrays.asList(type.getRegular());
            if (regular.size() > 1) {
                String match = regular.get(0);
                String result = regular.get(1);
                if (StringUtils.isNotBlank(match) && StringUtils.isNotBlank(result)) {
                    value = value.replaceAll(match, result);
                    try {
                        field.set(obj, value);
                    } catch (Exception ignore) {
                    }
                }
            }
        }
    }
}
