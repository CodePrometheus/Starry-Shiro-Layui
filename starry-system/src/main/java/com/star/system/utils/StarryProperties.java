package com.star.system.utils;

import com.star.common.properties.ShiroProperties;
import com.star.common.properties.SwaggerProperties;
import com.star.common.properties.ValidateCodeProperties;
import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 对应配置文件starry.yml
 *
 * @Author: zzStar
 * @Date: 03-06-2021 13:55
 */
@Data
@SpringBootConfiguration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = StarryProperties.PROPERTIES_PREFIX)
public class StarryProperties {

    public static final String PROPERTIES_PREFIX = "starry";
    public static final String ENABLE_REDIS_CACHE = "starry.enable-redis-cache";

    private ShiroProperties shiro = new ShiroProperties();
    private SwaggerProperties swagger = new SwaggerProperties();
    /**
     * 批量插入提交commit数据量
     */
    private int maxBatchInsertNum = 1000;

    private ValidateCodeProperties code = new ValidateCodeProperties();
    /**
     * 是否开启Redis缓存，true开启，false关闭
     * 为false时，采用基于内存的ehcache缓存
     */
    private boolean enableRedisCache;
}
