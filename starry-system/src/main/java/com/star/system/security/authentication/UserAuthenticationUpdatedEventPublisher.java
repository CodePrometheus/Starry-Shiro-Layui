package com.star.system.security.authentication;

import com.star.common.annotation.Publisher;
import com.star.common.event.UserAuthenticationUpdatedEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.NonNull;

import java.util.Set;

/**
 * 权限更新发布
 * 动态权限
 *
 * @Author: zzStar
 * @Date: 03-05-2021 13:07
 */
@Slf4j
@Getter
@Publisher
public class UserAuthenticationUpdatedEventPublisher implements ApplicationEventPublisherAware, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 广播📢{@link UserAuthenticationUpdatedEvent}事件
     * 事件广播
     *
     * @param userId 用户ID集合
     */
    public void publishEvent(Set<Long> userId) {
        UserAuthenticationUpdatedEvent event = new UserAuthenticationUpdatedEvent(applicationContext);
        event.setUserIds(userId);
        applicationEventPublisher.publishEvent(event);
        log.info("broadcast UserAuthenticationUpdatedEvent");
    }

}
