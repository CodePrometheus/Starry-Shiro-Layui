package com.star.common.shutdown;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * @Description: stopped
 * @Author: zzStar
 * @Date: 03-12-2021 12:43
 */
@Slf4j
@Component
public class StarryShutDownHook {

    @EventListener(classes = {ContextClosedEvent.class})
    public void onStarryApplicationClosed(@NonNull ApplicationEvent event) {
        log.info("=== Starry系统已关闭，Bye ~~ ===");
    }
}
