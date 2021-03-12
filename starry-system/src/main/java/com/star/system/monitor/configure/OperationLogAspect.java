package com.star.system.monitor.configure;

import com.star.common.annotation.OperationLog;
import com.star.common.aspect.BaseAspect;
import com.star.common.exception.StarryException;
import com.star.system.security.authentication.StarryUtil;
import com.star.system.framework.entity.User;
import com.star.system.monitor.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 实现操作日志切面
 *
 * @Author: zzStar
 * @Date: 03-06-2021 16:06
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect extends BaseAspect {

    private final OperationLogService logService;

    @Pointcut("@annotation(com.star.common.annotation.OperationLog)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws StarryException {
        Object result;
        Method targetMethod = resolveMethod(point);
        OperationLog annotation = targetMethod.getAnnotation(OperationLog.class);
        String operation = annotation.operation();

        // 创建时间
        long start = System.currentTimeMillis();

        try {
            // 环绕通知=前置+目标方法执行+后置通知，proceed用于启动目标方法执行
            result = point.proceed();
            if (StringUtils.isNotBlank(operation)) {
                // 当前请求的request对象，转成前端请求方法名、参数、路径等信息
                RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
                ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) attributes;
                String ip = StringUtils.EMPTY;
                if (servletRequestAttributes != null) {
                    // 在服务器取客户端ip
                    ip = servletRequestAttributes.getRequest().getRemoteAddr();
                }
                // 设置操作用户
                User user = (User) SecurityUtils.getSubject().getPrincipal();
                logService.saveLog(user, point, targetMethod, ip, operation, start);
            }
            // 目标方法执行完的返回值
            return result;
        } catch (Throwable throwable) {
            // Error和Exception的父类，用来定义所有可以作为异常被抛出来的类
            log.error(throwable.getMessage(), throwable);
            String exceptionMessage = annotation.exceptionMessage();
            String message = throwable.getMessage();
            // 内部异常+异常消息
            String error = StarryUtil.containChinese(message) ? exceptionMessage + "，" + message : exceptionMessage;
            throw new StarryException(error);
        }
    }
}



