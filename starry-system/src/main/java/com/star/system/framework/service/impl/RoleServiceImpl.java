package com.star.system.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.star.common.entity.QueryRequest;
import com.star.common.entity.StarryConstant;
import com.star.common.entity.Strings;
import com.star.system.security.authentication.UserAuthenticationUpdatedEventPublisher;
import com.star.common.utils.SortUtil;
import com.star.system.framework.entity.Role;
import com.star.system.framework.entity.RoleMenu;
import com.star.system.framework.mapper.RoleMapper;
import com.star.system.framework.service.IRoleMenuService;
import com.star.system.framework.service.IRoleService;
import com.star.system.framework.service.IUserRoleService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Author: zzStar
 * @Date: 03-05-2021 13:40
 */
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    private final IRoleMenuService roleMenuService;
    private final IUserRoleService userRoleService;
    private final UserAuthenticationUpdatedEventPublisher publisher;

    @Override
    public List<Role> findUserRole(String username) {
        return baseMapper.findUserRole(username);
    }

    @Override
    public List<Role> findRoles(Role role) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(role.getRoleName())) {
            queryWrapper.lambda().like(Role::getRoleName, role.getRoleName());
        }
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public IPage<Role> findRoles(Role role, QueryRequest request) {
        Page<Role> page = new Page<>(request.getPageNum(), request.getPageSize());
        page.setSearchCount(false);
        page.setTotal(baseMapper.countRole(role));
        SortUtil.handlePageSort(request, page, "createTime", StarryConstant.ORDER_DESC, false);
        return baseMapper.findRolePage(page, role);
    }

    @Override
    public Role findByName(String roleName) {
        return baseMapper.selectOne(new QueryWrapper<Role>().lambda().eq(Role::getRoleName, roleName));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRole(Role role) {
        role.setCreateTime(new Date());
        baseMapper.insert(role);
        saveRoleMenus(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Role role) {
        role.setModifyTime(new Date());
        updateById(role);
        List<String> roleIdList = Lists.newArrayList(String.valueOf(role.getRoleId()));
        roleMenuService.deleteRoleMenusByRoleId(roleIdList);
        saveRoleMenus(role);
        Set<Long> userIds = userRoleService.findUserIdByRoleId(role.getRoleId());
        if (CollectionUtils.isNotEmpty(userIds)) {
            publisher.publishEvent(userIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoles(String roleIds) {
        List<String> list = Arrays.asList(roleIds.split(Strings.COMMA));
        baseMapper.delete(new QueryWrapper<Role>().lambda().in(Role::getRoleId, list));

        roleMenuService.deleteRoleMenusByRoleId(list);
        userRoleService.deleteUserRolesByRoleId(list);

        Set<Long> userIds = userRoleService.findUserIdByRoleIds(list);
        if (CollectionUtils.isNotEmpty(userIds)) {
            publisher.publishEvent(userIds);
        }
    }

    private void saveRoleMenus(Role role) {
        if (StringUtils.isNotBlank(role.getMenuIds())) {
            String[] menuIds = role.getMenuIds().split(Strings.COMMA);
            List<RoleMenu> roleMenus = Lists.newArrayList();
            Arrays.stream(menuIds).forEach(menuId -> {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setMenuId(Long.valueOf(menuId));
                roleMenu.setRoleId(role.getRoleId());
                roleMenus.add(roleMenu);
            });
            roleMenuService.saveBatch(roleMenus);
        }
    }
}
