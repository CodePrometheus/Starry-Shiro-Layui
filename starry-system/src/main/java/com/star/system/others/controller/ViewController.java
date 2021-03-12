package com.star.system.others.controller;

import com.star.common.entity.StarryConstant;
import com.star.system.security.authentication.StarryUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: zzStar
 * @Date: 03-12-2021 10:21
 */
@Controller("othersView")
@RequestMapping(StarryConstant.VIEW_PREFIX + "others")
public class ViewController {

    @GetMapping("febs/form")
    @RequiresPermissions("febs:form:view")
    public String febsForm() {
        return StarryUtil.view("others/febs/form");
    }

    @GetMapping("febs/form/group")
    @RequiresPermissions("febs:formgroup:view")
    public String febsFormGroup() {
        return StarryUtil.view("others/febs/formGroup");
    }

    @GetMapping("febs/tools")
    @RequiresPermissions("febs:tools:view")
    public String febsTools() {
        return StarryUtil.view("others/febs/tools");
    }

    @GetMapping("febs/icon")
    @RequiresPermissions("febs:icons:view")
    public String febsIcon() {
        return StarryUtil.view("others/febs/icon");
    }

    @GetMapping("febs/others")
    @RequiresPermissions("others:febs:others")
    public String febsOthers() {
        return StarryUtil.view("others/febs/others");
    }

    @GetMapping("apex/line")
    @RequiresPermissions("apex:line:view")
    public String apexLine() {
        return StarryUtil.view("others/apex/line");
    }

    @GetMapping("apex/area")
    @RequiresPermissions("apex:area:view")
    public String apexArea() {
        return StarryUtil.view("others/apex/area");
    }

    @GetMapping("apex/column")
    @RequiresPermissions("apex:column:view")
    public String apexColumn() {
        return StarryUtil.view("others/apex/column");
    }

    @GetMapping("apex/radar")
    @RequiresPermissions("apex:radar:view")
    public String apexRadar() {
        return StarryUtil.view("others/apex/radar");
    }

    @GetMapping("apex/bar")
    @RequiresPermissions("apex:bar:view")
    public String apexBar() {
        return StarryUtil.view("others/apex/bar");
    }

    @GetMapping("apex/mix")
    @RequiresPermissions("apex:mix:view")
    public String apexMix() {
        return StarryUtil.view("others/apex/mix");
    }

    @GetMapping("map")
    @RequiresPermissions("map:view")
    public String map() {
        return StarryUtil.view("others/map/gaodeMap");
    }

    @GetMapping("eximport")
    @RequiresPermissions("others:eximport:view")
    public String eximport() {
        return StarryUtil.view("others/eximport/eximport");
    }

    @GetMapping("eximport/result")
    @RequiresPermissions("others:eximport:view")
    public String eximportResult() {
        return StarryUtil.view("others/eximport/eximportResult");
    }

    @GetMapping("datapermission")
    @RequiresPermissions("others:datapermission")
    public String dataPermissionTest() {
        return StarryUtil.view("others/datapermission/test");
    }
}
