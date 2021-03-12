package com.star.common.entity;

import org.springframework.http.HttpStatus;

import java.util.HashMap;

/**
 * 结果返回类
 *
 * @Author: zzStar
 * @Date: 03-05-2021 20:28
 */
public class AjaxResult extends HashMap<String, Object> {

    private static final long serialVersionUID = -8713837118340960775L;

    public AjaxResult code(HttpStatus status) {
        put("code", status.value());
        return this;
    }

    public AjaxResult message(String message) {
        put("message", message);
        return this;
    }

    public AjaxResult data(Object data) {
        put("data", data);
        return this;
    }

    public AjaxResult success() {
        code(HttpStatus.OK);
        return this;
    }

    public AjaxResult fail() {
        code(HttpStatus.INTERNAL_SERVER_ERROR);
        return this;
    }

    @Override
    public AjaxResult put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
