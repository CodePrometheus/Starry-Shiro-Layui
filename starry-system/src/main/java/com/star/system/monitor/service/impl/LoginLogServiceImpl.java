package com.star.system.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.common.entity.QueryRequest;
import com.star.common.entity.StarryConstant;
import com.star.common.utils.AddressUtil;
import com.star.common.utils.HttpContextUtil;
import com.star.common.utils.IpUtil;
import com.star.common.utils.SortUtil;
import com.star.system.framework.entity.User;
import com.star.system.monitor.entity.LoginLog;
import com.star.system.monitor.mapper.LoginLogMapper;
import com.star.system.monitor.service.ILoginLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Author: zzStar
 * @Date: 03-06-2021 15:11
 */
@Service("loginLogService")
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements ILoginLogService {

    @Override
    public IPage<LoginLog> findLoginLogs(LoginLog loginLog, QueryRequest request) {
        QueryWrapper<LoginLog> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(loginLog.getLoginTimeFrom()) &&
                StringUtils.equals(loginLog.getLoginTimeFrom(), loginLog.getLoginTimeTo())) {
            loginLog.setLoginTimeFrom(loginLog.getLoginTimeFrom() + StarryConstant.DAY_START_PATTERN_SUFFIX);
            loginLog.setLoginTimeTo(loginLog.getLoginTimeTo() + StarryConstant.DAY_END_PATTERN_SUFFIX);
        }
        if (StringUtils.isNotBlank(loginLog.getUsername())) {
            queryWrapper.lambda().eq(LoginLog::getUsername, loginLog.getUsername().toLowerCase());
        }
        if (StringUtils.isNotBlank(loginLog.getLoginTimeFrom()) && StringUtils.isNotBlank(loginLog.getLoginTimeTo())) {
            queryWrapper.lambda()
                    .ge(LoginLog::getLoginTime, loginLog.getLoginTimeFrom())
                    .le(LoginLog::getLoginTime, loginLog.getLoginTimeTo());
        }

        Page<LoginLog> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handlePageSort(request, page, "loginTime", StarryConstant.ORDER_DESC, true);
        return page(page, queryWrapper);
    }

    @Override
    public void saveLoginLog(LoginLog loginLog) {
        loginLog.setLoginTime(new Date());
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        String ip = IpUtil.getIpAddr(request);
        loginLog.setIp(ip);
        loginLog.setLocation(AddressUtil.getCityInfo(ip));
        save(loginLog);
    }

    @Override
    public void saveLoginLog(String username) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUsername(username);
        loginLog.setSystemBrowserInfo();
        saveLoginLog(loginLog);
    }

    @Override
    public void deleteLoginLogs(String[] ids) {
        List<String> list = Arrays.asList(ids);
        baseMapper.deleteBatchIds(list);
    }

    @Override
    public Long findTotalVisitCount() {
        return baseMapper.findTotalVisitCount();
    }

    @Override
    public Long findTodayVisitCount() {
        return baseMapper.findTodayVisitCount();
    }

    @Override
    public Long findTodayIp() {
        return baseMapper.findTodayIp();
    }

    @Override
    public List<Map<String, Object>> findLastSevenDaysVisitCount(User user) {
        return baseMapper.findLastSevenDaysVisitCount(user);
    }

    private List<Map<String, Object>> findLastSevenDaysVisitCount() {
        return findLastSevenDaysVisitCount(new User());
    }

    private List<Map<String, Object>> findLastSevenDaysVisitCount(String username) {
        User param = new User();
        param.setUsername(username);
        return findLastSevenDaysVisitCount(param);
    }

    @Override
    public Map<String, Object> retrieveIndexPageData(String username) {
        Map<String, Object> data = new HashMap<>(8);
        // 获取系统访问记录
        data.put("totalVisitCount", findTotalVisitCount());
        data.put("todayVisitCount", findTodayVisitCount());
        data.put("todayIp", findTodayIp());
        // 获取近期系统访问记录
        data.put("lastSevenVisitCount", findLastSevenDaysVisitCount());
        data.put("lastSevenUserVisitCount", findLastSevenDaysVisitCount(username));
        return data;
    }
}
