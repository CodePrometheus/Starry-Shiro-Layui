package com.star.common.annotation;

import com.star.common.entity.Strings;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 权限更新发布
 *
 * @Author: zzStar
 * @Date: 03-05-2021 13:08
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Publisher {
    @AliasFor(annotation = Component.class)
    String value() default Strings.EMPTY;
}
