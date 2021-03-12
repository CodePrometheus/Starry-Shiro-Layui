package com.star.system.security.authentication;

import com.google.common.base.Stopwatch;
import com.star.common.annotation.Listener;
import com.star.common.entity.StarryConstant;
import com.star.common.event.UserAuthenticationUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;

import java.util.Set;

/**
 * 事件监听器
 *
 * @Author: zzStar
 * @Date: 03-09-2021 10:58
 */
@Slf4j
@Listener
@RequiredArgsConstructor
public class UserAuthenticationUpdatedEventListener {

    private final ShiroRealm realm;

    @EventListener
    @Async(StarryConstant.Starry_SHIRO_THREAD_POOL)
    public void onUserAuthenticationUpdated(@NonNull UserAuthenticationUpdatedEvent event) {
        Set<Long> userIds = event.getUserIds();
        if (CollectionUtils.isNotEmpty(userIds)) {
            // 计时器
            Stopwatch stopwatch = Stopwatch.createStarted();
            userIds.forEach(realm::clearCache);
            event.cleanSet(userIds);
            log.info("clean user [userId: {}] authentication cache,which took {}", userIds, stopwatch.stop());
        }
    }
}
