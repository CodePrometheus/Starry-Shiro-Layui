package com.star.system.security.authentication;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.star.system.utils.ConditionOnRedisCache;
import com.star.common.entity.Strings;
import com.star.system.utils.StarryProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

/**
 * shiro缓存配置
 *
 * @Author: zzStar
 * @Date: 03-09-2021 11:05
 */
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class ShiroConfigure {

    private final StarryProperties starryProperties;
    private RedisProperties redisProperties;

    /**
     * remember key
     * //        String s = RandomStringUtils.randomAlphabetic(15);
     * //        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
     * //        String k = Base64Utils.encodeToString(Arrays.copyOf(bytes, 16));
     * //        System.out.println(k);
     */
    private final static String REMEMBER_ME_KEY = "SlRjZ0xwbVJwS0JtWHlqAA";

    @Autowired(required = false)
    public void setRedisProperties(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    /**
     * shiro 中配置 redis 缓存
     *
     * @return RedisManager
     */
    private RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        // 设置地址
        redisManager.setHost(redisProperties.getHost() + Strings.COLON + redisProperties.getPort());
        if (StringUtils.isNotBlank(redisProperties.getPassword())) {
            redisManager.setPassword(redisProperties.getPassword());
        }
        redisManager.setTimeout(redisManager.getTimeout());
        redisManager.setDatabase(redisProperties.getDatabase());
        return redisManager;
    }

    /**
     * 开启shiro缓存处理
     */
    @Bean
    @ConditionOnRedisCache
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        // 权限缓存超时时间，和session超时时间一致
        redisCacheManager.setExpire((int) starryProperties.getShiro().getSessionTimeout().getSeconds());
        // 配置缓存
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }

    @Bean
    @ConditionalOnMissingBean(RedisCacheManager.class)
    public EhCacheManager ehCacheManager() {
        EhCacheManager ehCacheManager = new EhCacheManager();
        ehCacheManager.setCacheManagerConfigFile("classpath:shiro-ehcache.xml");
        return ehCacheManager;
    }

    /**
     * 更改默认的安全管理器
     *
     * @param shiroRealm
     * @param redisCacheManager
     * @param ehCacheManager
     * @param sessionManager
     * @return
     */
    @Bean
    public DefaultWebSecurityManager securityManager(ShiroRealm shiroRealm,
                                                     @Nullable RedisCacheManager redisCacheManager,
                                                     @Nullable EhCacheManager ehCacheManager,
                                                     DefaultWebSessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 配置 SecurityManager，并注入 shiroRealm 使用自己的realm
        securityManager.setRealm(shiroRealm);
        // 配置 shiro session管理器
        securityManager.setSessionManager(sessionManager);
        // 配置 缓存管理类 cacheManager
        securityManager.setCacheManager(redisCacheManager != null ? redisCacheManager : ehCacheManager);
        // 配置 rememberMeCookie
        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    /**
     * rememberMe cookie 效果是重开浏览器后无需重新登录
     *
     * @return SimpleCookie
     */
    private SimpleCookie rememberMeCookie() {
        // 设置 cookie 名称，对应 login.html 页面的 <input type="checkbox" name="rememberMe"/>
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        // 设置 cookie 的过期时间，单位为秒
        cookie.setMaxAge((int) starryProperties.getShiro().getCookieTimeout().getSeconds());
        return cookie;
    }

    /**
     * cookie管理对象
     *
     * @return CookieRememberMeManager
     */
    private CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        cookieRememberMeManager.setCipherKey(Base64.decode(REMEMBER_ME_KEY));
        return cookieRememberMeManager;
    }

    /**
     * 用于开启 Thymeleaf 中的 shiro 标签的使用
     * 在html页面引用shiro标签
     *
     * @return ShiroDialect shiro 方言对象
     */
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

    @Bean
    @ConditionOnRedisCache
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }

    @Bean
    @ConditionalOnMissingBean(RedisSessionDAO.class)
    public MemorySessionDAO memorySessionDAO() {
        return new MemorySessionDAO();
    }

    /**
     * session 管理对象
     *
     * @return DefaultWebSessionManager
     */
    @Bean
    public DefaultWebSessionManager sessionManager(@Nullable RedisSessionDAO redisSessionDAO,
                                                   @Nullable MemorySessionDAO memorySessionDAO) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // 设置 session超时时间
        sessionManager.setGlobalSessionTimeout(starryProperties.getShiro().getSessionTimeout().toMillis());
        // 内存和redis切换
        sessionManager.setSessionDAO(redisSessionDAO == null ? memorySessionDAO : redisSessionDAO);
        // 去掉shiro登录时url里的JSESSIONID,禁用url 重写 url; shiro请求时默认 jsessionId=id
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }
}

