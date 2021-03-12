package com.star.system.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.system.framework.entity.RoleMenu;

import java.util.List;
import java.util.Set;

/**
 * @Author: zzStar
 * @Date: 03-03-2021 13:59
 */
public interface IRoleMenuService extends IService<RoleMenu> {

    /**
     * 通过角色ID删除
     *
     * @param roleIds 角色ID
     */
    void deleteRoleMenusByRoleId(List<String> roleIds);

    /**
     * 通过菜单（按钮）ID删除
     *
     * @param menuIds 菜单（按钮）ID
     */
    void deleteRoleMenusByMenuId(List<String> menuIds);

    /**
     * 通过菜单ID集合查找关联的用户ID集合
     *
     * @param menuIds 菜单ID集合
     * @return 用户ID集合
     */
    Set<Long> findUserIdByMenuIds(List<String> menuIds);
}
