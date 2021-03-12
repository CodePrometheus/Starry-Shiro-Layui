package com.star.common.annotation;

import com.star.common.entity.Strings;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 健康检查信息
 *
 * @Author: zzStar
 * @Date: 03-08-2021 13:37
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface StarryEndPoint {
    @AliasFor(annotation = Component.class)
    String value() default Strings.EMPTY;
}
