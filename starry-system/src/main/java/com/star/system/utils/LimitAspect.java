package com.star.system.utils;

import com.google.common.collect.ImmutableList;
import com.star.common.annotation.Limit;
import com.star.common.aspect.BaseAspect;
import com.star.common.entity.LimitType;
import com.star.common.entity.Strings;
import com.star.common.exception.LimitAccessException;
import com.star.common.utils.HttpContextUtil;
import com.star.common.utils.IpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;


/**
 * 基于Redis的接口限流，如果未开启Redis，则该功能不生效
 *
 * @Author: zzStar
 * @Date: 03-06-2021 17:44
 */
@Slf4j
@Aspect
@Component
@ConditionOnRedisCache
@RequiredArgsConstructor
public class LimitAspect extends BaseAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    @Pointcut("@annotation(com.star.common.annotation.Limit)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        Method method = resolveMethod(point);
        Limit limitAnnotation = method.getAnnotation(Limit.class);
        LimitType limitType = limitAnnotation.limitType();
        String name = limitAnnotation.name();
        String key;
        String ip = IpUtil.getIpAddr(request);
        int limitPeriod = limitAnnotation.period();
        int limitCount = limitAnnotation.count();
        switch (limitType) {
            case IP:
                key = ip;
                break;
            case CUSTOMER:
                key = limitAnnotation.key();
                break;
            default:
                key = StringUtils.upperCase(method.getName());
        }

        // 将数组以符号或其他字符串为间隔组装成新的字符串
        String join = StringUtils.join(limitAnnotation.prefix() + Strings.UNDER_LINE, key, ip);
        // ImmutableList是一个不可变、线程安全的列表集合，它只会获取传入对象的一个副本，而不会影响到原来的变量或者对象
        ImmutableList<String> keys = ImmutableList.of(join);


        String luaScript = buildLuaScript();
        RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
        Long count = redisTemplate.execute(redisScript, keys, limitCount, limitPeriod);
        if (count != null && count.intValue() <= limitCount) {
            log.info("IP:{} 第 {} 次访问key为 {}，描述为 [{}] 的接口", ip, count, keys, name);
            return point.proceed();
        } else {
            log.error("key为 {}，描述为 [{}] 的接口访问超出频率限制", keys, name);
            throw new LimitAccessException("接口访问超出频率限制");
        }
    }

    /**
     * 限流脚本
     * 调用的时候不超过阈值，则直接返回并执行计算器自加。
     *
     * @return lua脚本
     */
    private String buildLuaScript() {
        return "local c" +
                "\nc = redis.call('get',KEYS[1])" +
                "\nif c and tonumber(c) > tonumber(ARGV[1]) then" +
                "\nreturn c;" +
                "\nend" +
                "\nc = redis.call('incr',KEYS[1])" +
                "\nif tonumber(c) == 1 then" +
                "\nredis.call('expire',KEYS[1],ARGV[2])" +
                "\nend" +
                "\nreturn c;";
    }
}
