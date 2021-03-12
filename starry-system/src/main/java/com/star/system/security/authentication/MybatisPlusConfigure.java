package com.star.system.security.authentication;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.star.common.interceptor.DesensitizationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @Author: zzStar
 * @Date: 03-07-2021 13:11
 */
@MapperScan("com.star.**.mapper")
@Configuration(proxyBeanMethods = false)
public class MybatisPlusConfigure {
    /**
     * 注册数据权限
     */
    @Bean
    @Order(0)
    public DataPermissionInterceptor dataPermissionInterceptor() {
        return new DataPermissionInterceptor();
    }

    /**
     * 数据脱敏
     */
    @Bean
    @Order(-1)
    public DesensitizationInterceptor desensitizationInterceptor() {
        return new DesensitizationInterceptor();
    }

    /**
     * 注册分页插件
     */
    @Bean
    @Order(-2)
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }
}
