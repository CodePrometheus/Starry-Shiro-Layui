package com.star.system.security.authentication;

import com.star.common.entity.StarryConstant;
import com.star.common.entity.Strings;
import com.star.common.properties.ShiroProperties;
import com.star.system.utils.StarryProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
 * 将注册时机较早的Bean单独提取出来，并且相关依赖延迟注入，
 * 尽可能的缩小对Bean后置处理器的影响
 *
 * @Author: zzStar
 * @Date: 03-09-2021 13:28
 */
@Configuration(proxyBeanMethods = false)
public class ShiroEarlyBeanRegistrar {

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Lazy DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Lazy DefaultWebSecurityManager securityManager,
                                                         @Lazy StarryProperties starryProperties) {
        ShiroProperties shiro = starryProperties.getShiro();
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 设置 securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 登录的 url
        shiroFilterFactoryBean.setLoginUrl(shiro.getLoginUrl());
        // 登录成功后跳转的 url
        shiroFilterFactoryBean.setSuccessUrl(shiro.getSuccessUrl());
        // 未授权 url
        shiroFilterFactoryBean.setUnauthorizedUrl(shiro.getUnauthorizedUrl());
        // 设置免认证 url
        LinkedHashMap<String, String> filterChainDefinitionMap;
        String[] anonUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(shiro.getAnonUrl(), Strings.COMMA);
        filterChainDefinitionMap = Arrays.stream(anonUrls).collect(Collectors.toMap(url -> url, url -> "anon", (a, b) -> b, LinkedHashMap::new));
        // 配置退出过滤器，其中具体的退出代码 Shiro已经替我们实现了
        filterChainDefinitionMap.put(shiro.getLogoutUrl(), "logout");
        // 除上以外所有 url都必须认证通过才可以访问，未通过认证自动访问 LoginUrl
        filterChainDefinitionMap.put(StarryConstant.REQUEST_ALL, "user");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }
}
