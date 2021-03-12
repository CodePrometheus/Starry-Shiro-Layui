package com.star.common.entity;

/**
 * 正则常量
 *
 * @Author: zzStar
 * @Date: 03-02-2021 23:13
 */
public interface Regexp {

    /**
     * 简单手机号正则（这里只是简单校验是否为 11位，实际规则更复杂）
     */
    String MOBILE_REG = "[1]\\d{10}";

}
