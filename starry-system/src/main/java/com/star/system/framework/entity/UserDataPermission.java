package com.star.system.framework.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: zzStar
 * @Date: 03-03-2021 13:43
 */
@Data
@TableName("t_user_data_permission")
public class UserDataPermission {

    @TableField("USER_ID")
    private Long userId;
    @TableField("DEPT_ID")
    private Long deptId;

}
