package com.star.system.monitor.rest;

import com.star.common.annotation.OperationLog;
import com.star.system.framework.rest.BaseController;
import com.star.common.entity.AjaxResult;
import com.star.common.utils.DateUtil;
import com.star.system.monitor.endpoint.StarryHttpTraceEndpoint;
import com.star.system.monitor.domain.StarryHttpTrace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author: zzStar
 * @Date: 03-08-2021 19:45
 */
@Slf4j
@RestController
@RequestMapping("febs/actuator")
@RequiredArgsConstructor
public class StarryActuatorController extends BaseController {

    private final StarryHttpTraceEndpoint httpTraceEndpoint;

    @GetMapping("httptrace")
    @RequiresPermissions("httptrace:view")
    @OperationLog(exceptionMessage = "请求追踪失败")
    public AjaxResult httpTraces(String method, String url) {
        List<HttpTrace> httpTraceList = httpTraceEndpoint.traces().getTraces();
        List<StarryHttpTrace> starryHttpTraces = new ArrayList<>();
        httpTraceList.forEach(t -> {
            StarryHttpTrace starryHttpTrace = new StarryHttpTrace();
            starryHttpTrace.setRequestTime(DateUtil.formatInstant(t.getTimestamp(), DateUtil.FULL_TIME_SPLIT_PATTERN));
            starryHttpTrace.setMethod(t.getRequest().getMethod());
            starryHttpTrace.setUrl(t.getRequest().getUri());
            starryHttpTrace.setStatus(t.getResponse().getStatus());
            starryHttpTrace.setTimeTaken(t.getTimeTaken());
            if (StringUtils.isNotBlank(method) && StringUtils.isNotBlank(url)) {
                if (StringUtils.equalsIgnoreCase(method, starryHttpTrace.getMethod())
                        && StringUtils.containsIgnoreCase(starryHttpTrace.getUrl().toString(), url)) {
                    starryHttpTraces.add(starryHttpTrace);
                }
            } else if (StringUtils.isNotBlank(method)) {
                if (StringUtils.equalsIgnoreCase(method, starryHttpTrace.getMethod())) {
                    starryHttpTraces.add(starryHttpTrace);
                }
            } else if (StringUtils.isNotBlank(url)) {
                if (StringUtils.containsIgnoreCase(starryHttpTrace.getUrl().toString(), url)) {
                    starryHttpTraces.add(starryHttpTrace);
                }
            } else {
                starryHttpTraces.add(starryHttpTrace);
            }
        });
        return new AjaxResult().success()
                .data(getDataTable(starryHttpTraces, starryHttpTraces.size()));
    }
}
