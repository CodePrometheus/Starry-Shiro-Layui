package com.star.common.exception;

/**
 * @Author: zzStar
 * @Date: 03-06-2021 18:18
 */
public class LimitAccessException extends StarryException {

    private static final long serialVersionUID = -3608667856397125671L;

    public LimitAccessException(String message) {
        super(message);
    }
}
