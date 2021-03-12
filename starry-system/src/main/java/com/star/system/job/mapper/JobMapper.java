package com.star.system.job.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.system.job.entity.Job;

import java.util.List;

/**
 * @Author: zzStar
 * @Date: 03-07-2021 17:35
 */
public interface JobMapper extends BaseMapper<Job> {

    /**
     * 获取定时任务列表
     *
     * @return 定时任务列表
     */
    List<Job> queryList();
}
