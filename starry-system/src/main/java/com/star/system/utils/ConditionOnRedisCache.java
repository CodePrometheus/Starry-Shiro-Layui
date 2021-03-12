package com.star.system.utils;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;


/**
 * @Description: 是否开启了redis注解
 * @Author: zzStar
 * @Date: 03-06-2021 17:46
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnRedisCacheCondition.class)
public @interface ConditionOnRedisCache {
}
