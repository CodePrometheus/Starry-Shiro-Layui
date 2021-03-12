package com.star.system.monitor.rest;

import com.star.common.entity.StarryConstant;
import com.star.system.security.authentication.StarryUtil;
import com.star.system.monitor.endpoint.StarryMetricsEndpoint;
import com.star.system.monitor.domain.JvmInfo;
import com.star.system.monitor.domain.ServerInfo;
import com.star.system.monitor.domain.TomcatInfo;
import com.star.system.monitor.helper.StarryActuatorHelper;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author: zzStar
 * @Date: 03-08-2021 20:09
 */
@Controller("monitorView")
@RequestMapping(StarryConstant.VIEW_PREFIX + "monitor")
@RequiredArgsConstructor
public class ViewController {

    private final StarryActuatorHelper actuatorHelper;

    @GetMapping("online")
    @RequiresPermissions("online:view")
    public String online() {
        return StarryUtil.view("monitor/online");
    }

    @GetMapping("log")
    @RequiresPermissions("log:view")
    public String log() {
        return StarryUtil.view("monitor/log");
    }

    @GetMapping("loginlog")
    @RequiresPermissions("loginlog:view")
    public String loginLog() {
        return StarryUtil.view("monitor/loginLog");
    }

    @GetMapping("httptrace")
    @RequiresPermissions("httptrace:view")
    public String httptrace() {
        return StarryUtil.view("monitor/httpTrace");
    }

    @GetMapping("jvm")
    @RequiresPermissions("jvm:view")
    public String jvmInfo(Model model) {
        List<StarryMetricsEndpoint.StarryMetricResponse> jvm = actuatorHelper.getMetricResponseByType("jvm");
        JvmInfo jvmInfo = actuatorHelper.getJvmInfoFromMetricData(jvm);
        model.addAttribute("jvm", jvmInfo);
        return StarryUtil.view("monitor/jvmInfo");
    }

    @GetMapping("tomcat")
    @RequiresPermissions("tomcat:view")
    public String tomcatInfo(Model model) {
        List<StarryMetricsEndpoint.StarryMetricResponse> tomcat = actuatorHelper.getMetricResponseByType("tomcat");
        TomcatInfo tomcatInfo = actuatorHelper.getTomcatInfoFromMetricData(tomcat);
        model.addAttribute("tomcat", tomcatInfo);
        return StarryUtil.view("monitor/tomcatInfo");
    }

    @GetMapping("server")
    @RequiresPermissions("server:view")
    public String serverInfo(Model model) {
        List<StarryMetricsEndpoint.StarryMetricResponse> jdbcInfo = actuatorHelper.getMetricResponseByType("jdbc");
        List<StarryMetricsEndpoint.StarryMetricResponse> systemInfo = actuatorHelper.getMetricResponseByType("system");
        List<StarryMetricsEndpoint.StarryMetricResponse> processInfo = actuatorHelper.getMetricResponseByType("process");

        ServerInfo serverInfo = actuatorHelper.getServerInfoFromMetricData(jdbcInfo, systemInfo, processInfo);
        model.addAttribute("server", serverInfo);
        return StarryUtil.view("monitor/serverInfo");
    }

    @GetMapping("swagger")
    public String swagger() {
        return StarryUtil.view("monitor/swagger");
    }
}
