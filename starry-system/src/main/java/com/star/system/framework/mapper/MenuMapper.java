package com.star.system.framework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.system.framework.domain.Menu;

import java.util.List;

/**
 * @Author: zzStar
 * @Date: 03-03-2021 13:45
 */
public interface MenuMapper extends BaseMapper<Menu> {
    /**
     * 查找用户权限集
     *
     * @param username 用户名
     * @return 用户权限集合
     */
    List<Menu> findUserPermissions(String username);

    /**
     * 查找用户菜单集合
     *
     * @param username 用户名
     * @return 用户菜单集合
     */
    List<Menu> findUserMenus(String username);
}
