package com.star.system.others.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.common.annotation.StarryDataPermission;
import com.star.system.others.domain.DataPermissionTest;

/**
 * @Author: zzStar
 * @Date: 03-12-2021 10:10
 */
@StarryDataPermission(methods = {"selectPage"})
public interface DataPermissionTestMapper extends BaseMapper<DataPermissionTest> {
}
