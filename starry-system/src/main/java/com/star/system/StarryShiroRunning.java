package com.star.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动项目
 * 奔跑吧 zzStar！
 *
 * @Author: zzStar
 * @Date: 03-02-2021 22:39
 */
@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
public class StarryShiroRunning {

    public static void main(String[] args) {
        new SpringApplicationBuilder(StarryShiroRunning.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

}
