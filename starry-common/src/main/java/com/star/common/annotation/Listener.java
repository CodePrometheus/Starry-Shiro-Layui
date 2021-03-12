package com.star.common.annotation;

import com.star.common.entity.Strings;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @Author: zzStar
 * @Date: 03-09-2021 11:03
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Listener {
    @AliasFor(annotation = Component.class)
    String value() default Strings.EMPTY;
}
