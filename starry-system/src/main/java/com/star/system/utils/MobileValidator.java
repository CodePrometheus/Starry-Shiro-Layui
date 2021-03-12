package com.star.system.utils;

import com.star.common.entity.Regexp;
import com.star.system.security.authentication.StarryUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 校验是否为合法的手机号码
 *
 * @Author: zzStar
 * @Date: 03-02-2021 23:10
 */
public class MobileValidator implements ConstraintValidator<IsMobile, String> {

    @Override
    public void initialize(IsMobile isMobile) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        try {
            if (StringUtils.isBlank(s)) {
                return true;
            } else {
                String regex = Regexp.MOBILE_REG;
                return StarryUtil.match(regex, s);
            }
        } catch (Exception e) {
            return false;
        }
    }
}
