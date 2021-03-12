package com.star.common.annotation;

import com.star.common.entity.Strings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: zzStar
 * @Date: 03-06-2021 16:00
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    String operation() default Strings.EMPTY;

    String exceptionMessage() default "StarryShiro内部异常";
}
