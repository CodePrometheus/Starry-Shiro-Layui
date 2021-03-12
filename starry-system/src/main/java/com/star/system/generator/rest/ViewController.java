package com.star.system.generator.rest;

import com.star.common.entity.StarryConstant;
import com.star.system.security.authentication.StarryUtil;
import com.star.system.generator.domain.GeneratorConfig;
import com.star.system.generator.service.IGeneratorConfigService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: zzStar
 * @Date: 03-09-2021 10:26
 */
@Controller("generatorViews")
@RequestMapping(StarryConstant.VIEW_PREFIX + "generator")
@RequiredArgsConstructor
public class ViewController {

    private final IGeneratorConfigService generatorConfigService;

    @GetMapping("generator")
    @RequiresPermissions("generator:view")
    public String generator() {
        return StarryUtil.view("generator/generator");
    }

    @GetMapping("configure")
    @RequiresPermissions("generator:configure:view")
    public String generatorConfigure(Model model) {
        GeneratorConfig generatorConfig = generatorConfigService.findGeneratorConfig();
        model.addAttribute("config", generatorConfig);
        return StarryUtil.view("generator/configure");
    }
}
