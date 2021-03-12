package com.star.system.monitor.configure;

import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: zzStar
 * @Date: 03-08-2021 13:42
 */
@Configuration
public class StarryMonitorConfigure {

    /**
     * 默认会把最近100次的HTTP请求记录到内存中
     *
     * @return
     */
    @Bean
    public HttpTraceRepository inMemoryHttpTraceRepository() {
        return new InMemoryHttpTraceRepository();
    }
}
