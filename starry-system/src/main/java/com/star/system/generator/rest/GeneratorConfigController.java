package com.star.system.generator.rest;

import com.star.common.annotation.OperationLog;
import com.star.system.framework.rest.BaseController;
import com.star.common.entity.AjaxResult;
import com.star.common.exception.StarryException;
import com.star.system.generator.domain.GeneratorConfig;
import com.star.system.generator.service.IGeneratorConfigService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * @Author: zzStar
 * @Date: 03-09-2021 10:07
 */
@RestController
@RequestMapping("generatorConfig")
@RequiredArgsConstructor
public class GeneratorConfigController extends BaseController {

    private final IGeneratorConfigService generatorConfigService;

    @GetMapping
    @RequiresPermissions("generator:configure:view")
    public AjaxResult getGeneratorConfig() {
        return new AjaxResult().success().data(generatorConfigService.findGeneratorConfig());
    }

    @PostMapping("update")
    @RequiresPermissions("generator:configure:update")
    @OperationLog(operation = "修改GeneratorConfig", exceptionMessage = "修改GeneratorConfig失败")
    public AjaxResult updateGeneratorConfig(@Valid GeneratorConfig generatorConfig) {
        if (StringUtils.isBlank(generatorConfig.getId())) {
            throw new StarryException("配置id不能为空");
        }
        generatorConfigService.updateGeneratorConfig(generatorConfig);
        return new AjaxResult().success();
    }
}
