package com.star.system.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.system.framework.entity.User;
import com.star.system.monitor.entity.LoginLog;

import java.util.List;
import java.util.Map;

/**
 * @Author: zzStar
 * @Date: 03-06-2021 15:09
 */
public interface LoginLogMapper extends BaseMapper<LoginLog> {

    /**
     * 获取系统总访问次数
     *
     * @return Long
     */
    Long findTotalVisitCount();

    /**
     * 获取系统今日访问次数
     *
     * @return Long
     */
    Long findTodayVisitCount();

    /**
     * 获取系统今日访问 IP数
     *
     * @return Long
     */
    Long findTodayIp();

    /**
     * 获取系统近七天来的访问记录
     *
     * @param user 用户
     * @return 系统近七天来的访问记录
     */
    List<Map<String, Object>> findLastSevenDaysVisitCount(User user);
}
