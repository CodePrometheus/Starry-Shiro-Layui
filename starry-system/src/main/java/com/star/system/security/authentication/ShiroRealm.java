package com.star.system.security.authentication;

import com.star.system.utils.StarryProperties;
import com.star.system.framework.domain.User;
import com.star.system.framework.service.IUserDataPermissionService;
import com.star.system.framework.service.IUserService;
import com.star.system.monitor.service.ISessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.crazycake.shiro.RedisCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 自定义实现 ShiroRealm，包含认证和授权两大模块
 *
 * @Author: zzStar
 * @Date: 03-07-2021 14:24
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShiroRealm extends AuthorizingRealm {

    private final ISessionService sessionService;
    private final ShiroLogoutService shiroLogoutService;
    private final IUserDataPermissionService userDataPermissionService;
    private final IUserService userService;

    private RedisCacheManager redisCacheManager;
    private EhCacheManager ehCacheManager;
    @Value("${" + StarryProperties.ENABLE_REDIS_CACHE + "}")
    private boolean enableRedisCache;

    @Autowired(required = false)
    public void setRedisCacheManager(RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    @Autowired(required = false)
    public void setEhCacheManager(EhCacheManager ehCacheManager) {
        this.ehCacheManager = ehCacheManager;
    }

    /**
     * 当bean创建完成的时候，会后置执行@PostConstruct修饰的方法
     */
    @PostConstruct
    private void initConfig() {
        // 关闭认证缓存
        setAuthenticationCachingEnabled(false);
        // 开启授权缓存
        setAuthorizationCachingEnabled(true);
        // 缓存redis中的hash值命名
        setAuthorizationCacheName("starry");
        setCachingEnabled(true);
        // 动态设置缓存管理器
        setCacheManager(redisCacheManager == null ? ehCacheManager : redisCacheManager);
    }

    /**
     * 授权模块，获取用户角色和权限
     *
     * @param principal principal
     * @return AuthorizationInfo 权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        User user = (User) principal.getPrimaryPrincipal();
        // 获取用户角色和权限集
        userService.doGetUserAuthorizationInfo(user);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        // 添加用户角色信息
        simpleAuthorizationInfo.setRoles(user.getRoles());
        // 添加权限字符串
        simpleAuthorizationInfo.setStringPermissions(user.getStringPermissions());
        return simpleAuthorizationInfo;
    }

    /**
     * 用户认证
     * 也就是说验证用户输入的账号和密码是否正确
     *
     * @param token AuthenticationToken 身份认证 token
     * @return AuthenticationInfo 身份认证信息
     * @throws AuthenticationException 认证相关异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 获取用户输入的用户名和密码（身份，即主体的标识属性，可以是任何东西，如用户名、邮箱等，唯一即可。一个主体可以有多个principals，但只有一个Primary principals，一般是用户名/密码/手机号）获取用户输入的用户名和密码
        String username = (String) token.getPrincipal();
        // 证明/凭证，即只有主体知道的安全值，如密码/数字证书
        String password = new String((char[]) token.getCredentials());

        // 校验密码用户名
        User user = this.userService.findByName(username);

        if (user == null || !StringUtils.equals(password, user.getPassword())) {
            throw new IncorrectCredentialsException("用户名或密码错误！");
        }
        // 校验账号是否激活状态
        if (User.STATUS_LOCK.equals(user.getStatus())) {
            throw new LockedAccountException("账号已被锁定,请联系管理员！");
        }
        // 数据权限
        String deptIds = this.userDataPermissionService.findByUserId(String.valueOf(user.getUserId()));
        user.setDeptIds(deptIds);
        // 第三个字段是realm，即当前realm的名称
        return new SimpleAuthenticationInfo(user, password, getName());
    }

    /**
     * 登出
     * PrincipalCollection是一个身份集合，因为我们可以在Shiro中同时配置多个Realm，所以呢身份信息可能就有多个；
     * 因此其提供了PrincipalCollection用于聚合这些身份信息：
     *
     * @param principals
     */
    @Override
    public void onLogout(PrincipalCollection principals) {
        super.onLogout(principals);
        if (enableRedisCache) {
            shiroLogoutService.cleanCacheFragment(principals);
        }
    }

    public void clearCache(Long userId) {
        List<SimplePrincipalCollection> principals = sessionService.getPrincipals(userId);
        if (CollectionUtils.isNotEmpty(principals)) {
            /** principals.forEach(super::clearCache);*/
            for (SimplePrincipalCollection principal : principals) {
                super.clearCache(principal);
            }
        }
    }
}
