package com.star.common.annotation;

import com.star.common.entity.Strings;
import com.star.common.entity.LimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流
 *
 * @Author: zzStar
 * @Date: 03-05-2021 20:36
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {

    /**
     * 资源名称，用于描述接口功能
     */
    String name() default Strings.EMPTY;

    /**
     * 资源 key
     */
    String key() default Strings.EMPTY;

    /**
     * key prefix
     */
    String prefix() default Strings.EMPTY;

    /**
     * 时间范围，单位秒
     */
    int period();

    /**
     * 限制访问次数
     */
    int count();

    /**
     * 限制类型
     */
    LimitType limitType() default LimitType.CUSTOMER;
}
