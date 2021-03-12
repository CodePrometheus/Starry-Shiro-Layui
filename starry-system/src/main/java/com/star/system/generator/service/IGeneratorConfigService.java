package com.star.system.generator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.system.generator.entity.GeneratorConfig;

/**
 * @Author: zzStar
 * @Date: 03-09-2021 09:54
 */
public interface IGeneratorConfigService extends IService<GeneratorConfig> {

    /**
     * 查询
     *
     * @return GeneratorConfig
     */
    GeneratorConfig findGeneratorConfig();

    /**
     * 修改
     *
     * @param generatorConfig generatorConfig
     */
    void updateGeneratorConfig(GeneratorConfig generatorConfig);

}
