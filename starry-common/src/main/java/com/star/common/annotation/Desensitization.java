package com.star.common.annotation;

import com.star.common.entity.DesensitizationType;
import com.star.common.entity.Strings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据脱敏注解
 *
 * @Author: zzStar
 * @Date: 03-07-2021 13:33
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Desensitization {

    /**
     * 脱敏规则类型
     */
    DesensitizationType type();

    /**
     * 附加值, 自定义正则表达式等
     */
    String[] attach() default Strings.EMPTY;
}
