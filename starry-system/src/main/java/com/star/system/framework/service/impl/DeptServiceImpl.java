package com.star.system.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.system.utils.DeptTree;
import com.star.common.entity.QueryRequest;
import com.star.common.entity.StarryConstant;
import com.star.common.utils.SortUtil;
import com.star.system.utils.TreeUtil;
import com.star.system.framework.entity.Dept;
import com.star.system.framework.mapper.DeptMapper;
import com.star.system.framework.service.IDeptService;
import com.star.system.framework.service.IUserDataPermissionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author: zzStar
 * @Date: 03-03-2021 14:54
 */
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements IDeptService {

    private final IUserDataPermissionService userDataPermissionService;

    @Override
    public List<DeptTree<Dept>> findDept() {
        List<Dept> deptList = baseMapper.selectList(new QueryWrapper<>());
        List<DeptTree<Dept>> trees = convertDept(deptList);
        return TreeUtil.buildDeptTree(trees);
    }

    @Override
    public List<DeptTree<Dept>> findDept(Dept dept) {
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(dept.getDeptName())) {
            queryWrapper.lambda().eq(Dept::getDeptName, dept.getDeptName());
        }
        queryWrapper.lambda().orderByAsc(Dept::getOrderNum);

        List<Dept> deptList = baseMapper.selectList(queryWrapper);
        List<DeptTree<Dept>> trees = convertDept(deptList);
        return TreeUtil.buildDeptTree(trees);
    }

    @Override
    public List<Dept> findDept(Dept dept, QueryRequest request) {
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(dept.getDeptName())) {
            queryWrapper.lambda().eq(Dept::getDeptName, dept.getDeptName());
        }
        SortUtil.handleWrapperSort(request, queryWrapper, "orderNum", StarryConstant.ORDER_ASC, true);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDept(Dept dept) {
        Long parentId = dept.getParentId();
        if (parentId == null) {
            dept.setParentId(Dept.TOP_NODE);
        }
        dept.setCreateTime(new Date());
        save(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDept(Dept dept) {
        dept.setModifyTime(new Date());
        baseMapper.updateById(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDept(String[] deptIds) {
        delete(Arrays.asList(deptIds));
    }

    private List<DeptTree<Dept>> convertDept(List<Dept> Dept) {
        List<DeptTree<Dept>> trees = new ArrayList<>();
        Dept.forEach(dept -> {
            DeptTree<Dept> tree = new DeptTree<>();
            tree.setId(String.valueOf(dept.getDeptId()));
            tree.setParentId(String.valueOf(dept.getParentId()));
            tree.setName(dept.getDeptName());
            tree.setData(dept);
            trees.add(tree);
        });
        return trees;
    }

    private void delete(List<String> deptIds) {
        removeByIds(deptIds);
        userDataPermissionService.deleteByDeptIds(deptIds);

        LambdaQueryWrapper<Dept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dept::getParentId, deptIds);
        List<Dept> Dept = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(Dept)) {
            List<String> deptIdList = new ArrayList<>();
            Dept.forEach(d -> deptIdList.add(String.valueOf(d.getDeptId())));
            delete(deptIdList);
        }
    }
}
